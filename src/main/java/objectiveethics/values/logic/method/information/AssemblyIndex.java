/*
 * To change this license header, 
choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectiveethics.values.logic.method.information;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import objectiveethics.values.structure.BaseValue;
import objectiveethics.values.structure.Information;

@BaseValue(id = 1, category = "information", acronym = "AI", name = "Assembly Index", description = "Exact Conin-style assembly index via IDA* over buildable substrings. "
        + "State = set of built substrings; move = concatenate two built substrings to form a longer substring of T. "
        + "Uses admissible lower bounds (doubling and glue bounds) for pruning.", algorithm = "1) Enumerate distinct substrings of T and assign IDs.\n"
                +
                "2) For each substring z and each cut, if z = x+y, add rule (x,y)->z.\n" +
                "3) Initial state: all length-1 substrings present in T.\n" +
                "4) IDA*: depth-first search with limit, using h = max(doubling bound, glue bound).\n" +
                "5) First time target is reached -> exact minimal steps.", minEstimationSize=100)
public class AssemblyIndex implements Information {

    // ===== Public API =====

    @Override
    public double value(byte[] arr) {
        if (arr == null || arr.length == 0)
            return 0;
        List<Integer> seq = new ArrayList<>(arr.length);
        for (byte b : arr)
            seq.add(b & 0xFF);
        return solveExact(seq);
    }

    @Override
    public double value(Collection<?> collection) {
        if (collection == null || collection.isEmpty())
            return 0;
        Map<Object, Integer> dict = new LinkedHashMap<>();
        List<Integer> seq = new ArrayList<>(collection.size());
        int next = 0;
        for (Object e : collection) {
            dict.putIfAbsent(e, next);
            seq.add(dict.get(e));
            if (dict.get(e) == next)
                next++;
        }
        return solveExact(seq);
    }

    // ===== Core solver =====

    private static final int FOUND = -1;
    private int solutionDepth = -1; // <-- ÚJ: ide írjuk a pontos g-t

    private double solveExact(List<Integer> baseSeq) {
        final int n = baseSeq.size();
        // --- 1) All distinct substrings by content
        Map<IntListKey, Integer> idByContent = new HashMap<>();
        List<IntListKey> contentById = new ArrayList<>();
        // positions for glue-bound coverage
        Map<Integer, List<int[]>> occById = new HashMap<>(); // id -> list of [start,end] (inclusive)

        for (int i = 0; i < n; i++) {
            int[] cur = new int[0];
            for (int j = i; j < n; j++) {
                cur = Arrays.copyOf(cur, cur.length + 1);
                cur[cur.length - 1] = baseSeq.get(j);
                IntListKey key = new IntListKey(cur);
                Integer id = idByContent.get(key);
                if (id == null) {
                    id = contentById.size();
                    idByContent.put(key, id);
                    contentById.add(key);
                }
                occById.computeIfAbsent(id, k -> new ArrayList<>()).add(new int[] { i, j });
            }
        }

        IntListKey targetKey = new IntListKey(toIntArray(baseSeq));
        Integer targetId = idByContent.get(targetKey);
        if (targetId == null)
            return 0; // should not happen

        final int M = contentById.size();
        int[] lenById = new int[M];
        for (int id = 0; id < M; id++)
            lenById[id] = contentById.get(id).data.length;
        final int targetLen = lenById[targetId];

        // --- 2) Rules: for each z and cut, z = x + y
        // store rules by z to help action generation when both x and y are ready
        List<int[]> rules = new ArrayList<>(); // (x,y,z)
        List<List<int[]>> producersOfZ = new ArrayList<>(M);
        for (int z = 0; z < M; z++)
            producersOfZ.add(new ArrayList<>());
        for (int z = 0; z < M; z++) {
            int L = lenById[z];
            if (L < 2)
                continue;
            int[] cz = contentById.get(z).data;
            for (int cut = 1; cut < L; cut++) {
                IntListKey leftK = new IntListKey(Arrays.copyOfRange(cz, 0, cut));
                IntListKey rightK = new IntListKey(Arrays.copyOfRange(cz, cut, L));
                Integer x = idByContent.get(leftK);
                Integer y = idByContent.get(rightK);
                if (x != null && y != null) {
                    int[] r = new int[] { x, y, z };
                    rules.add(r);
                    producersOfZ.get(z).add(r);
                }
            }
        }

        // --- 3) Initial state: all length-1 substrings present in T
        BitSet start = new BitSet(M);
        {
            boolean[] seen = new boolean[1 << 16]; // enough for small alphabets; fallback below if needed
            for (int v : new LinkedHashSet<>(baseSeq)) {
                IntListKey k = new IntListKey(new int[] { v });
                Integer id = idByContent.get(k);
                if (id != null)
                    start.set(id);
            }
        }
        if (start.get(targetId))
            return 0;

        // Precompute mask of length-1 ids for quick maxLenReady
        List<Integer> len1Ids = new ArrayList<>();
        for (int id = 0; id < M; id++)
            if (lenById[id] == 1)
                len1Ids.add(id);

        solutionDepth = -1; // <-- reset
        int bound = heuristic(start, baseSeq, targetId, targetLen, contentById, occById, lenById);
        SearchCtx ctx = new SearchCtx(producersOfZ, lenById, targetId, targetLen,
                baseSeq, contentById, occById);

        while (true) {
            int t = dfsIDA(start, 0, bound, ctx);
            if (t == FOUND) {
                return solutionDepth; // <-- A PONTOS g
            }
            if (t == Integer.MAX_VALUE)
                break;
            bound = t; // emeljük a küszöböt
        }
        return Math.max(0, targetLen - 1);
    }

    // Megjegyzés: elhagytam a seenLayer-t: IDA*-ban a bound és a h miatt jó a
    // pruning, nem kell set.
    private int dfsIDA(BitSet state, int g, int bound, SearchCtx ctx) {
        int h = heuristic(state, ctx.baseSeq, ctx.targetId, ctx.targetLen, ctx.contentById, ctx.occById, ctx.lenById);
        int f = g + h;
        if (f > bound)
            return f;
        if (state.get(ctx.targetId)) {
            solutionDepth = g; // <-- itt tároljuk el a VALÓDI mélységet
            return FOUND;
        }

        // Jelöltek előállítása (mint korábban)
        List<Integer> candZ = new ArrayList<>();
        for (int z = 0; z < ctx.lenById.length; z++) {
            if (state.get(z))
                continue;
            boolean ok = false;
            for (int[] r : ctx.producersOfZ.get(z)) {
                if (state.get(r[0]) && state.get(r[1])) {
                    ok = true;
                    break;
                }
            }
            if (ok)
                candZ.add(z);
        }
        if (candZ.isEmpty())
            return Integer.MAX_VALUE;

        // Rendezés: hossz szerint, majd „coverage gain”
        candZ.sort((z1, z2) -> {
            int c = Integer.compare(ctx.lenById[z2], ctx.lenById[z1]);
            if (c != 0)
                return c;
            int g1 = coverageGain(state, z1, ctx);
            int g2 = coverageGain(state, z2, ctx);
            return Integer.compare(g2, g1);
        });

        int minNext = Integer.MAX_VALUE;
        for (int z : candZ) {
            BitSet ns = (BitSet) state.clone();
            ns.set(z);
            int t = dfsIDA(ns, g + 1, bound, ctx);
            if (t == FOUND)
                return FOUND;
            if (t < minNext)
                minNext = t;
        }
        return minNext;
    }

    // ===== Heuristics =====

    /** Admisszibilis h = max(doubling bound, glue bound). */
    private int heuristic(BitSet state,
            List<Integer> baseSeq,
            int targetId, int targetLen,
            List<IntListKey> contentById,
            Map<Integer, List<int[]>> occById,
            int[] lenById) {

        // 1) doubling bound (length growth)
        int maxLenReady = 1;
        for (int id = state.nextSetBit(0); id >= 0; id = state.nextSetBit(id + 1)) {
            if (lenById[id] > maxLenReady)
                maxLenReady = lenById[id];
            if (id == targetId)
                return 0;
        }
        int hLen = ceilLog2(divUp(targetLen, maxLenReady));

        // 2) glue bound (ready coverage of target, greedy maximal blocks)
        // fedjük le a célt max. hosszú READY blokkokkal; b = blokkok száma
        int b = 0;
        int i = 0, n = baseSeq.size();
        while (i < n) {
            int best = 0; // best block length starting at i
            // végigmegyünk az összes kész substringen, és megnézzük, mekkora blokk
            // illeszthető ide
            for (int id = state.nextSetBit(0); id >= 0; id = state.nextSetBit(id + 1)) {
                for (int[] occ : occById.getOrDefault(id, Collections.emptyList())) {
                    if (occ[0] == i) {
                        int L = occ[1] - occ[0] + 1;
                        if (L > best)
                            best = L;
                    }
                }
            }
            if (best == 0) {
                // ha semmi kész nem illik ide, legalább 1 hosszú terminált rakunk
                best = 1;
            }
            b++;
            i += best;
        }
        int hGlue = Math.max(0, b - 1);

        return Math.max(hLen, hGlue);
    }

    /** Heurisztikus másodlagos rendezési szempont: mekkora „hézagot” töm be a z. */
    private int coverageGain(BitSet state, int z, SearchCtx ctx) {
        int gain = 0;
        for (int[] occ : ctx.occById.getOrDefault(z, Collections.emptyList())) {
            // balról greedy: ha a kezdő pozíción még nincs hosszú kész blokk, értékesebb
            gain += ctx.lenById[z];
        }
        return gain;
    }

    // ===== Utilities =====

    private static int ceilLog2(int x) {
        if (x <= 1)
            return 0;
        int p = 0, v = 1;
        while (v < x) {
            v <<= 1;
            p++;
        }
        return p;
    }

    private static int divUp(int a, int b) {
        return (a + b - 1) / b;
    }

    private static int[] toIntArray(List<Integer> list) {
        int[] a = new int[list.size()];
        for (int i = 0; i < list.size(); i++)
            a[i] = list.get(i);
        return a;
    }

    private static final class SearchCtx {
        final List<List<int[]>> producersOfZ;
        final int[] lenById;
        final int targetId, targetLen;
        final List<Integer> baseSeq;
        final List<IntListKey> contentById;
        final Map<Integer, List<int[]>> occById;

        SearchCtx(List<List<int[]>> producersOfZ, int[] lenById,
                int targetId, int targetLen,
                List<Integer> baseSeq, List<IntListKey> contentById,
                Map<Integer, List<int[]>> occById) {
            this.producersOfZ = producersOfZ;
            this.lenById = lenById;
            this.targetId = targetId;
            this.targetLen = targetLen;
            this.baseSeq = baseSeq;
            this.contentById = contentById;
            this.occById = occById;
        }
    }

    // immutable key over int[]
    private static final class IntListKey {
        final int[] data;
        final int hash;

        IntListKey(int[] arr) {
            this.data = arr;
            this.hash = Arrays.hashCode(arr);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof IntListKey))
                return false;
            return Arrays.equals(this.data, ((IntListKey) o).data);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
