package objectiveethics.values.logic.method;

import objectiveethics.values.structure.BaseValue;

import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import objectiveethics.values.structure.Value;

/**
 * MultiBlockRegressionValue (MBR)
 *
 * - Kis bemeneten (< minEstimationSize) a delegate pontos értékét adja.
 * - Nagy bemeneten (>= minEstimationSize) időkerethez (expectedTimeMillis)
 * igazított
 * mintavételezéssel több blokkméretre becsli F(B)-t, majd súlyozott
 * lineáris regresszióval (y = a + k*(N/B - 1)) a teljes értéket: a ≈ C(X).
 *
 * Megjegyzések:
 * - Collection<?> esetén a bemenetet egyszer snapshot-oljuk egy ArrayList-be
 * (indexelhetőség, O(N) referencia-másolás).
 * - minEstimationSize: byte[]-nél bájt, Collection-nél elemszám.
 */

public final class EstimatedValue implements Value {

    private static final int DEFAULT_MIN_ESTIMATION_SIZE = 900;
    private final Value delegate;
    private final int minEstimationSize;


    public EstimatedValue(Value delegate) {
        if (delegate == null)
            throw new IllegalArgumentException("delegate cannot be null");
        this.delegate = delegate;
        this.minEstimationSize = extractMinEstimationSize(delegate);
    }

    @Override
    public double value(byte[] bytes) {
        if (bytes.length < minEstimationSize) {
            return delegate.value(bytes);
        }
        
        return estimateValueForBytes(bytes);
    }

    private double getMarginalCost(byte[] block1, byte[] block2) {
        double value1 = delegate.value(block1);
        double value2 = delegate.value(block2);
        double mergedValue = delegate.value(mergeBlocks(block1, block2));
        return value1 + value2 - mergedValue;
    }

    private byte[] mergeBlocks(byte[] block1, byte[] block2) {
        byte[] merged = new byte[block1.length + block2.length];
        System.arraycopy(block1, 0, merged, 0, block1.length);
        System.arraycopy(block2, 0, merged, block1.length, block2.length);
        return merged;
    }

    private byte[] getBlock(int i, int blockSize, byte[] values) {
        int start = i * blockSize;
        int end = Math.min(start + blockSize, values.length);
        return Arrays.copyOfRange(values, start, end);
    }

    @Override
    public double value(Collection<?> values) {
        if (values.size() < minEstimationSize) {
            return delegate.value(values);
        }
        
        return estimateValueForCollection(values);
    }

    /**
     * Becslési algoritmus byte[] típusú bemenetre.
     * Blokkokra osztja az inputot és logaritmikus mintavételezéssel becsli az értéket.
     */
    private double estimateValueForBytes(byte[] bytes) {
        int totalLength = bytes.length;
        
        // Blokkméretek logaritmikus skálán: 1K, 2K, 4K, 8K, stb.
        List<Integer> blockSizes = getLogarithmicBlockSizes(totalLength);
        
        double totalEstimatedValue = 0.0;
        double averageMarginalCost = calculateAverageMarginalCostForBytes(bytes, blockSizes);
        
        for (int blockSize : blockSizes) {
            int numBlocks = (int) Math.ceil(totalLength / (double) blockSize);
            if (numBlocks == 0) continue;
            
            // Logaritmikus mintavételezés - nem minden blokkot mérünk
            int samplesToTake = Math.max(1, (int) Math.ceil(Math.log(numBlocks + 1)));
            
            double blockSum = 0.0;
            Random random = new Random(42); // Fix seed a reprodukálhatóságért
            
            for (int i = 0; i < samplesToTake && i < numBlocks; i++) {
                int blockIndex = random.nextInt(numBlocks);
                byte[] block = getBlock(blockIndex, blockSize, bytes);
                blockSum += delegate.value(block);
            }
            
            // Átlagérték és extrapoláció
            double averageBlockValue = blockSum / samplesToTake;
            double estimatedSumForBlockSize = averageBlockValue * numBlocks;
            
            // Határköltség-korrekció
            double correction = averageMarginalCost * (numBlocks - 1);
            totalEstimatedValue += estimatedSumForBlockSize - correction;
        }
    

        return totalEstimatedValue / blockSizes.size();
    }

    /**
     * Becslési algoritmus Collection<?> típusú bemenetre.
     */
    private double estimateValueForCollection(Collection<?> values) {
        // Konvertáljuk ArrayList-re az indexelhetőség érdekében
        List<?> list = values instanceof List ? (List<?>) values : new ArrayList<>(values);
        int totalSize = list.size();
        
        // Blokkméretek logaritmikus skálán
        List<Integer> blockSizes = getLogarithmicBlockSizes(totalSize);
        
        double totalEstimatedValue = 0.0;
        double averageMarginalCost = calculateAverageMarginalCostForCollection(list, blockSizes);
        
        for (int blockSize : blockSizes) {
            int numBlocks = totalSize / blockSize;
            if (numBlocks == 0) continue;
            
            // Logaritmikus mintavételezés
            int samplesToTake = Math.max(1, (int) Math.ceil(Math.log(numBlocks + 1)));
            
            double blockSum = 0.0;
            Random random = new Random(42);
            
            for (int i = 0; i < samplesToTake && i < numBlocks; i++) {
                int blockIndex = random.nextInt(numBlocks);
                List<?> block = getCollectionBlock(blockIndex, blockSize, list);
                blockSum += delegate.value(block);
            }
            
            // Átlagérték és extrapoláció
            double averageBlockValue = blockSum / samplesToTake;
            double estimatedSumForBlockSize = averageBlockValue * numBlocks;
            
            // Határköltség-korrekció
            double correction = averageMarginalCost * (numBlocks - 1);
            totalEstimatedValue += estimatedSumForBlockSize - correction;
        }

        return totalEstimatedValue / blockSizes.size();
    }

    /**
     * Logaritmikus blokkméretek generálása.
     */
    private List<Integer> getLogarithmicBlockSizes(int totalSize) {
        List<Integer> blockSizes = new ArrayList<>();
        
        // Kezdjük 1024-gyel (1K) és duplázunk minden lépésben
        for (int blockSize = 1024; blockSize < totalSize; blockSize *= 2) {
            blockSizes.add(blockSize);
        }
        
        // Ha nincs megfelelő blokk, legalább a teljes méretet használjuk
        if (blockSizes.isEmpty()) {
            blockSizes.add(totalSize);
        }
        
        return blockSizes;
    }

    /**
     * Átlagos határköltség számítása byte[] típusra.
     */
    private double calculateAverageMarginalCostForBytes(byte[] bytes, List<Integer> blockSizes) {
        double totalMarginalCost = 0.0;
        int measurements = 0;
        Random random = new Random(42);
        
        for (int blockSize : blockSizes) {
            int numBlocks = bytes.length / blockSize;
            if (numBlocks < 2) continue;
            
            // Néhány véletlenszerű blokk-pár kiválasztása
            int pairsToTest = Math.min(3, numBlocks - 1);
            
            for (int i = 0; i < pairsToTest; i++) {
                int block1Index = random.nextInt(numBlocks - 1);
                int block2Index = block1Index + 1;
                
                byte[] block1 = getBlock(block1Index, blockSize, bytes);
                byte[] block2 = getBlock(block2Index, blockSize, bytes);
                
                totalMarginalCost += getMarginalCost(block1, block2);
                measurements++;
            }
        }
        
        return measurements > 0 ? totalMarginalCost / measurements : 0.0;
    }

    /**
     * Átlagos határköltség számítása Collection típusra.
     */
    private double calculateAverageMarginalCostForCollection(List<?> list, List<Integer> blockSizes) {
        double totalMarginalCost = 0.0;
        int measurements = 0;
        Random random = new Random(42);
        
        for (int blockSize : blockSizes) {
            int numBlocks = list.size() / blockSize;
            if (numBlocks < 2) continue;
            
            int pairsToTest = Math.min(3, numBlocks - 1);
            
            for (int i = 0; i < pairsToTest; i++) {
                int block1Index = random.nextInt(numBlocks - 1);
                int block2Index = block1Index + 1;
                
                List<?> block1 = getCollectionBlock(block1Index, blockSize, list);
                List<?> block2 = getCollectionBlock(block2Index, blockSize, list);
                
                totalMarginalCost += getMarginalCostForCollection(block1, block2);
                measurements++;
            }
        }
        
        return measurements > 0 ? totalMarginalCost / measurements : 0.0;
    }

    /**
     * Blokk kivágása Collection-ből.
     */
    private List<?> getCollectionBlock(int blockIndex, int blockSize, List<?> list) {
        int start = blockIndex * blockSize;
        int end = Math.min(start + blockSize, list.size());
        return list.subList(start, end);
    }

    /**
     * Határköltség számítása Collection blokk-párokra.
     */
    private double getMarginalCostForCollection(List<?> block1, List<?> block2) {
        double value1 = delegate.value(block1);
        double value2 = delegate.value(block2);
        
        // Egyesített lista létrehozása
        List<Object> merged = new ArrayList<>();
        merged.addAll(block1);
        merged.addAll(block2);
        
        double mergedValue = delegate.value(merged);
        return value1 + value2 - mergedValue;
    }


    public Value getBaseValue() {
        return delegate;
    }

    private int extractMinEstimationSize(Value delegate) {
        BaseValue annotation = delegate.getClass().getAnnotation(objectiveethics.values.structure.BaseValue.class);
        if (annotation != null) {
            return annotation.minEstimationSize();
        }
        return DEFAULT_MIN_ESTIMATION_SIZE;
    }

    public double value(Character[] values) {
        return value(Arrays.asList(values));
    }

    public double value(Byte[] values) {
        return value(Arrays.asList(values));
    }

    public double value(Short[] values) {
        return value(Arrays.asList(values));
    }

    public double value(Integer[] values) {
        return value(Arrays.asList(values));
    }

    public double value(Long[] values) {
        return value(Arrays.asList(values));
    }

    public double value(String[] values) {
        return value(Arrays.asList(values));
    }

    public double value(String values) {
        if (values == null) {
            return 0;
        }
        return value(values.getBytes());
    }

    public double value(boolean[] values) {
        if (values == null) {
            return 0;
        }
        byte[] byteArray = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            byteArray[i] = (byte) (values[i] ? 1 : 0);
        }
        return value(byteArray);
    }

    public double value(short[] values) {
        if (values == null) {
            return 0;
        }
        byte[] byteArray = new byte[values.length * 2];
        for (int i = 0; i < values.length; i++) {
            byteArray[i * 2] = (byte) (values[i] >> 8);
            byteArray[i * 2 + 1] = (byte) values[i];
        }
        return value(byteArray);
    }

    public double value(int[] values) {
        if (values == null) {
            return 0;
        }
        byte[] byteArray = new byte[values.length * 4];
        for (int i = 0; i < values.length; i++) {
            byteArray[i * 4] = (byte) (values[i] >> 24);
            byteArray[i * 4 + 1] = (byte) (values[i] >> 16);
            byteArray[i * 4 + 2] = (byte) (values[i] >> 8);
            byteArray[i * 4 + 3] = (byte) values[i];
        }
        return value(byteArray);
    }

    public double value(float[] values) {
        if (values == null) {
            return 0;
        }
        byte[] byteArray = new byte[values.length * 4];
        for (int i = 0; i < values.length; i++) {
            int intBits = Float.floatToIntBits(values[i]);
            byteArray[i * 4] = (byte) (intBits >> 24);
            byteArray[i * 4 + 1] = (byte) (intBits >> 16);
            byteArray[i * 4 + 2] = (byte) (intBits >> 8);
            byteArray[i * 4 + 3] = (byte) intBits;
        }
        return value(byteArray);
    }

    public double value(double[] values) {
        if (values == null) {
            return 0;
        }
        byte[] byteArray = new byte[values.length * 8];
        for (int i = 0; i < values.length; i++) {
            long longBits = Double.doubleToLongBits(values[i]);
            byteArray[i * 8] = (byte) (longBits >> 56);
            byteArray[i * 8 + 1] = (byte) (longBits >> 48);
            byteArray[i * 8 + 2] = (byte) (longBits >> 40);
            byteArray[i * 8 + 3] = (byte) (longBits >> 32);
            byteArray[i * 8 + 4] = (byte) (longBits >> 24);
            byteArray[i * 8 + 5] = (byte) (longBits >> 16);
            byteArray[i * 8 + 6] = (byte) (longBits >> 8);
            byteArray[i * 8 + 7] = (byte) longBits;
        }
        return value(byteArray);
    }

    public double value(char[] values) {
        if (values == null) {
            return 0;
        }
        byte[] byteArray = new byte[values.length * 2];
        for (int i = 0; i < values.length; i++) {
            byteArray[i * 2] = (byte) (values[i] >> 8);
            byteArray[i * 2 + 1] = (byte) values[i];
        }
        return value(byteArray);
    }

    public double value(Object object) {
        if (object == null) {
            return 0;
        }
        return value(object.toString());
    }
}