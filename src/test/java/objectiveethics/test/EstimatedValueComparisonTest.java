package objectiveethics.test;

import objectiveethics.values.logic.method.EstimatedValue;
import objectiveethics.values.logic.method.entropy.CollisionEntropy;
import objectiveethics.values.logic.method.entropy.ShannonEntropy;
import objectiveethics.values.logic.method.information.*;
import objectiveethics.values.structure.Value;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Összehasonlító teszt az EstimatedValue és a normál Value implementációk között.
 * Különböző típusú bemenetek (konstans, véletlenszerű, strukturált) és különböző
 * hosszúságok esetén méri a különbségeket.
 */
public class EstimatedValueComparisonTest {

    private static final DecimalFormat FORMAT = new DecimalFormat("0.0000");
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("0.00%");
    private static final Random RANDOM = new Random(42); // Fix seed a reprodukálhatóságért

    public static void main(String[] args) {
        System.out.println("=".repeat(100));
        System.out.println("ESTIMATED VALUE vs ORIGINAL VALUE - ÖSSZEHASONLÍTÓ TESZT");
        System.out.println("=".repeat(100));
        System.out.println();

        // Value implementációk létrehozása
        Value[] values = {
            new ShannonInfo(),
            new ShannonEntropy(),
            new GZIPInfo(),
            new HuffmanInfo(),
            new MarkovInfo(),
            new MaxInfo(),
            new MinInfo(),
            new CollisionEntropy(),
            new RLEInfo(),
            new SCMInfo()
        };

        // Különböző méretű bemenetek tesztelése
        // Kiterjesztve nagyobb méretekkel is, hogy a becslési logika is tesztelhető legyen
        int[] sizes = {100, 500, 1000, 2000, 5000, 10000, 20000, 30000, 50000, 100000};

        for (Value value : values) {
            testValue(value, sizes);
            System.out.println();
        }
    }

    private static void testValue(Value originalValue, int[] sizes) {
        String valueName = originalValue.getClass().getSimpleName();
        System.out.println("━".repeat(100));
        System.out.println("Teszt: " + valueName);
        System.out.println("━".repeat(100));

        Value estimatedValue = new EstimatedValue(originalValue);

        for (int size : sizes) {
            System.out.println("\n--- Méret: " + size + " ---");
            
            // 1. Konstans sorozat (csupa 'A')
            testConstantSequence(originalValue, estimatedValue, size);
            
            // 2. Véletlenszerű sorozat
            testRandomSequence(originalValue, estimatedValue, size);
            
            // 3. Ismétlődő minta
            testRepeatingPattern(originalValue, estimatedValue, size);
            
            // 4. Vegyes entrópiájú sorozat
            testMixedEntropySequence(originalValue, estimatedValue, size);
        }
    }

    private static void testConstantSequence(Value original, Value estimated, int size) {
        byte[] data = new byte[size];
        // Csupa ugyanaz a byte
        for (int i = 0; i < size; i++) {
            data[i] = 'A';
        }

        double origValue = original.value(data);
        double estValue = estimated.value(data);
        double diff = Math.abs(origValue - estValue);
        double relDiff = origValue != 0 ? diff / Math.abs(origValue) : 0;

        System.out.printf("  Konstans:      Eredeti=%-10s Becsült=%-10s Különbség=%-10s (%s)%n",
            FORMAT.format(origValue),
            FORMAT.format(estValue),
            FORMAT.format(diff),
            PERCENT_FORMAT.format(relDiff));
    }

    private static void testRandomSequence(Value original, Value estimated, int size) {
        byte[] data = new byte[size];
        RANDOM.nextBytes(data);

        double origValue = original.value(data);
        double estValue = estimated.value(data);
        double diff = Math.abs(origValue - estValue);
        double relDiff = origValue != 0 ? diff / Math.abs(origValue) : 0;

        System.out.printf("  Véletlenszerű: Eredeti=%-10s Becsült=%-10s Különbség=%-10s (%s)%n",
            FORMAT.format(origValue),
            FORMAT.format(estValue),
            FORMAT.format(diff),
            PERCENT_FORMAT.format(relDiff));
    }

    private static void testRepeatingPattern(Value original, Value estimated, int size) {
        byte[] pattern = "ABCDEFGH".getBytes();
        byte[] data = new byte[size];
        
        for (int i = 0; i < size; i++) {
            data[i] = pattern[i % pattern.length];
        }

        double origValue = original.value(data);
        double estValue = estimated.value(data);
        double diff = Math.abs(origValue - estValue);
        double relDiff = origValue != 0 ? diff / Math.abs(origValue) : 0;

        System.out.printf("  Ismétlődő:     Eredeti=%-10s Becsült=%-10s Különbség=%-10s (%s)%n",
            FORMAT.format(origValue),
            FORMAT.format(estValue),
            FORMAT.format(diff),
            PERCENT_FORMAT.format(relDiff));
    }

    private static void testMixedEntropySequence(Value original, Value estimated, int size) {
        byte[] data = new byte[size];
        
        // Első fele: konstans
        for (int i = 0; i < size / 2; i++) {
            data[i] = 'X';
        }
        
        // Második fele: véletlenszerű
        for (int i = size / 2; i < size; i++) {
            data[i] = (byte) RANDOM.nextInt(256);
        }

        double origValue = original.value(data);
        double estValue = estimated.value(data);
        double diff = Math.abs(origValue - estValue);
        double relDiff = origValue != 0 ? diff / Math.abs(origValue) : 0;

        System.out.printf("  Vegyes:        Eredeti=%-10s Becsült=%-10s Különbség=%-10s (%s)%n",
            FORMAT.format(origValue),
            FORMAT.format(estValue),
            FORMAT.format(diff),
            PERCENT_FORMAT.format(relDiff));
    }

    /**
     * Statisztikai elemzés: több futtatás átlaga
     */
    public static void runStatisticalAnalysis(Value original, Value estimated, int size, int iterations) {
        double sumDiff = 0;
        double sumRelDiff = 0;
        int count = 0;

        for (int i = 0; i < iterations; i++) {
            byte[] data = new byte[size];
            RANDOM.nextBytes(data);

            double origValue = original.value(data);
            double estValue = estimated.value(data);
            
            if (origValue != 0) {
                double diff = Math.abs(origValue - estValue);
                double relDiff = diff / Math.abs(origValue);
                sumDiff += diff;
                sumRelDiff += relDiff;
                count++;
            }
        }

        System.out.printf("Statisztika (%d futás, %d méret): Átlagos különbség=%.4f, Átlagos relatív különbség=%.2f%%%n",
            iterations, size, sumDiff / count, (sumRelDiff / count) * 100);
    }
}
