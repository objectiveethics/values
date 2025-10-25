/*
 * To change this license header, 
choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectiveethics.values.logic.method.information;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import objectiveethics.values.structure.BaseValue;
import objectiveethics.values.structure.Information;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
@BaseValue(
    id= 2,
    category = "information",
    acronym = "AIH",
    name = "Assembly Index (Heuristic)",
    description = "Heuristic estimator of the binary Assembly Index (Conin-style). "
                + "Uses a RePair-like bigram replacement to build binary rules (S→A B). "
                + "The final cost is the number of distinct nonterminals reachable from the "
                + "top-level sequence (each built once) plus (k-1) to sequentially glue the "
                + "remaining top-level symbols.",
    algorithm =
        "1) Map input to integer symbols (terminals).\n" +
        "2) Repeatedly replace the most frequent adjacent pair by a new nonterminal (RePair-like),\n" +
        "   storing rules S→A B; stop when no pair occurs ≥ 2 times.\n" +
        "3) Let 'k' be the length of the resulting top-level sequence.\n" +
        "4) Let 'NeededNT' be the set of distinct nonterminals reachable from the top-level sequence.\n" +
        "5) Return |NeededNT| + max(0, k-1).",
        minEstimationSize=1000)
public class AssemblyIndexHeuristic implements Information {



    @Override
    public double value(byte[] arr) {
        if (arr == null || arr.length == 0) return 0;

        // 1) Kezdeti szekvencia szimbólumokként (0..255 terminálok)
        List<Integer> seq = new ArrayList<>(arr.length);
        for (byte b : arr) seq.add(b & 0xFF);

        // 2) RePair-szerű párcsere
        int nextSym = 256; // új nemterminálok innen
        Map<Integer, Rule> rules = new HashMap<>(); // nonterm -> (left,right)

        while (true) {
            // párok számlálása
            Map<Long, Integer> freq = new HashMap<>();
            for (int i = 0; i + 1 < seq.size(); i++) {
                long key = (((long) seq.get(i)) << 32) ^ (seq.get(i + 1) & 0xffffffffL);
                freq.merge(key, 1, Integer::sum);
            }
            // legjobb pár kiválasztása
            long bestKey = 0;
            int bestCnt = 0;
            for (var e : freq.entrySet()) {
                int c = e.getValue();
                if (c >= 2 && c > bestCnt) {
                    bestCnt = c;
                    bestKey = e.getKey();
                }
            }
            if (bestCnt < 2) break; // nincs ismétlődő pár

            int a = (int) (bestKey >> 32);
            int b = (int) (bestKey & 0xffffffffL);
            int S = nextSym++;
            rules.put(S, new Rule(a, b));

            // nem átfedő cserék
            List<Integer> newSeq = new ArrayList<>(seq.size());
            for (int i = 0; i < seq.size(); ) {
                if (i + 1 < seq.size() && seq.get(i) == a && seq.get(i + 1) == b) {
                    newSeq.add(S);
                    i += 2;
                } else {
                    newSeq.add(seq.get(i));
                    i += 1;
                }
            }
            seq = newSeq;
        }

        // 3) Mélységek kiszámítása (terminál: 0; S->AB: 1+max)
        Map<Integer, Integer> depthMemo = new HashMap<>();
        for (Integer nt : rules.keySet()) {
            depth(nt, rules, depthMemo);
        }
        int maxDepth = 0;
        for (int s : seq) {
            maxDepth = Math.max(maxDepth, depth(s, rules, depthMemo));
        }

        // 4) A maradék k szimbólum bináris összefűzésének mélysége
        int k = seq.size();
        int glue = ceilLog2(k);

        return maxDepth + glue;
    }

    private static int depth(int sym, Map<Integer, Rule> rules, Map<Integer, Integer> memo) {
        if (sym < 256) return 0; // terminál
        Integer d = memo.get(sym);
        if (d != null) return d;
        Rule r = rules.get(sym);
        if (r == null) return 0; // biztonsági
        int left = depth(r.left, rules, memo);
        int right = depth(r.right, rules, memo);
        int res = 1 + Math.max(left, right);
        memo.put(sym, res);
        return res;
    }

    private static int ceilLog2(int x) {
        if (x <= 1) return 0;
        int p = 0, v = 1;
        while (v < x) { v <<= 1; p++; }
        return p;
    }

    @Override
    public double value(Collection<?> collection) {
        if (collection == null || collection.isEmpty()) return 0;
        // Egyedi ID minden különböző elemnek (terminálok)
        Map<Object, Integer> dict = new LinkedHashMap<>();
        List<Integer> seq = new ArrayList<>(collection.size());
        int nextTermId = 0;
        for (Object e : collection) {
            dict.putIfAbsent(e, nextTermId++);
            seq.add(dict.get(e));
        }
        // Nemterminálisok számozását a terminálok után folytatjuk
        return estimateFromIntSeq(seq, nextTermId);
    }

    // ===== Heurisztika magja =================================================

    private double estimateFromIntSeq(List<Integer> seqInput, int nextSymbolStart) {
        // Védjük az input listát
        List<Integer> seq = new ArrayList<>(seqInput);
        int nextSym = nextSymbolStart;                 // nemterminálisok innen indulnak
        Map<Integer, Rule> rules = new HashMap<>();    // S -> (A,B)

        // RePair-szerű iteráció: a leggyakoribb (≥2) bigram cseréje új nemterminálisra
        while (true) {
            Map<Long, Integer> freq = new HashMap<>();
            // bigramok számlálása
            for (int i = 0; i + 1 < seq.size(); i++) {
                long key = pairKey(seq.get(i), seq.get(i + 1));
                freq.merge(key, 1, Integer::sum);
            }

            long bestKey = 0;
            int bestCnt = 0;

            // Leggyakoribb pár kiválasztása (≥2)
            for (Map.Entry<Long, Integer> e : freq.entrySet()) {
                int c = e.getValue();
                if (c >= 2 && c > bestCnt) {
                    bestCnt = c;
                    bestKey = e.getKey();
                }
            }
            if (bestCnt < 2) break; // nincs ismétlődő pár

            int a = leftOf(bestKey);
            int b = rightOf(bestKey);
            int S = nextSym++;
            rules.put(S, new Rule(a, b));

            // Nem átfedő előfordulások cseréje
            List<Integer> newSeq = new ArrayList<>(seq.size());
            for (int i = 0; i < seq.size(); ) {
                if (i + 1 < seq.size() && seq.get(i) == a && seq.get(i + 1) == b) {
                    newSeq.add(S);
                    i += 2;
                } else {
                    newSeq.add(seq.get(i));
                    i += 1;
                }
            }
            seq = newSeq;
        }

        // Szükséges nemterminálisok (top-levelből elérhető closure)
        Set<Integer> neededNT = reachableNonterminals(seq, rules);

        // Szekvenciális költség: minden szükséges nemterminális egyszeri építése + a felső szint összefűzése
        int k = seq.size();
        int buildOnce = neededNT.size();
        int glueSeq = Math.max(0, k - 1);

        return (double) buildOnce + glueSeq;
    }

    // ===== Segédfüggvények ===================================================

    private static long pairKey(int a, int b) {
        // 32+32 biten kódolt kulcs
        return (((long) a) << 32) ^ (b & 0xffffffffL);
    }

    private static int leftOf(long key) {
        return (int) (key >> 32);
    }

    private static int rightOf(long key) {
        return (int) (key & 0xffffffffL);
    }

    private static Set<Integer> reachableNonterminals(List<Integer> topSeq, Map<Integer, Rule> rules) {
        Set<Integer> needed = new HashSet<>();
        Deque<Integer> dq = new ArrayDeque<>(topSeq);
        // Bejárjuk a szabályfát a top-szintű szimbólumoktól indulva
        while (!dq.isEmpty()) {
            int s = dq.pollFirst();
            Rule r = rules.get(s);
            if (r != null) {
                // s egy nemterminális
                if (needed.add(s)) {
                    dq.addLast(r.left);
                    dq.addLast(r.right);
                }
            }
        }
        return needed;
    }

    private static final class Rule {
        final int left, right;
        Rule(int left, int right) { this.left = left; this.right = right; }
    }
 
}
