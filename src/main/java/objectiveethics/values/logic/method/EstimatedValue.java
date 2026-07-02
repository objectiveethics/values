package objectiveethics.values.logic.method;

import objectiveethics.values.structure.BaseValue;
import objectiveethics.values.structure.Value;

import java.util.Arrays;
import java.util.Collection;

/**
 * EstimatedValue - intelligens becslő wrapper a delegate Value köré.
 * 
 * <p>
 * Adaptív mintavételezéssel becsüli meg a nagy adatsorozatok értékét:
 * <ul>
 * <li>Kis adatok (&lt; minEstimationSize): pontos számítás</li>
 * <li>Közepes adatok (&lt; 3 * minEstimationSize): első részlet alapján</li>
 * <li>Nagy adatok: exponenciális mintavételezés kompenzációval</li>
 * </ul>
 */
public final class EstimatedValue implements Value {

    private final Value delegate;
    private final int minEstimationSize;

    public EstimatedValue(Value delegate) {
        if (delegate == null)
            throw new IllegalArgumentException("delegate cannot be null");
        this.delegate = delegate;
        this.minEstimationSize = getMinEstimationSize(delegate);
    }

    private static int getMinEstimationSize(Value value) {
        BaseValue annotation = value.getClass().getAnnotation(BaseValue.class);
        return annotation != null ? annotation.minEstimationSize() : 10000;
    }

    @Override
    public double value(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return delegate.value(bytes);
        }

        int length = bytes.length;

        // 1. Ha rövidebb mint minEstimationSize, pontos számítás
        if (length < minEstimationSize) {
            return delegate.value(bytes);
        }

        // 2. Ha kisebb mint 3 * minEstimationSize, lineáris extrapoláció
        if (length < 3 * minEstimationSize) {
            byte[] sample = Arrays.copyOfRange(bytes, 0, minEstimationSize);
            return delegate.value(sample) * (double) length / minEstimationSize;
        }

        // 3. Nagy adatok: exponenciális mintavételezés páratlan blokkokkal
        // Kompenzációs faktor számítása
        byte[] halfPart1 = Arrays.copyOfRange(bytes, 0, minEstimationSize / 2);
        byte[] halfPart2 = Arrays.copyOfRange(bytes, minEstimationSize / 2, minEstimationSize);
        
        double halfPart1Value = delegate.value(halfPart1);
        double halfPart2Value = delegate.value(halfPart2);
        double sumHalves = halfPart1Value + halfPart2Value;
        
        if (sumHalves == 0) {
            return 0.0;
        }
        
        byte[] firstPart = Arrays.copyOfRange(bytes, 0, minEstimationSize);
        double firstPartValue = delegate.value(firstPart);
        double compensation = firstPartValue / sumHalves;

        // Páratlan blokkok mintavételezése: 1., 3., 5., 9., 17., 33...
        // Blokk indexek (0-based): 0, 2, 4, 8, 16, 32...
        double totalValue = firstPartValue;
        int samplesCount = 1;
        
        int blockIndex = 2;  // Következő: 3. blokk (0-based: index 2)
        int step = 2;        // Kezdeti lépésköz
        
        while (blockIndex * minEstimationSize < length) {
            int startPos = blockIndex * minEstimationSize;
            int endPos = Math.min(startPos + minEstimationSize, length);
            
            byte[] sample = Arrays.copyOfRange(bytes, startPos, endPos);
            totalValue += delegate.value(sample);
            samplesCount++;
            
            blockIndex += step;
            step *= 2;  // 2, 4, 8, 16, 32...
        }

        // Becslés: átlagérték per minta * teljes hossz / minta méret * kompenzáció
        return (totalValue / samplesCount) * (double) length / minEstimationSize * compensation;
    }


    @Override
    public double value(Collection<?> values) {
        if (values == null || values.isEmpty()) {
            return delegate.value(values);
        }

        int size = values.size();

        // 1. Ha kisebb mint minEstimationSize, pontos számítás
        if (size < minEstimationSize) {
            return delegate.value(values);
        }

        // Konvertálás listává az indexeléshez
        java.util.List<?> list = values instanceof java.util.List 
            ? (java.util.List<?>) values 
            : new java.util.ArrayList<>(values);

        // 2. Ha kisebb mint 3 * minEstimationSize, lineáris extrapoláció
        if (size < 3 * minEstimationSize) {
            java.util.List<?> sample = list.subList(0, minEstimationSize);
            return delegate.value(sample) * (double) size / minEstimationSize;
        }

        // 3. Nagy adatok: exponenciális mintavételezés páratlan blokkokkal
        // Kompenzációs faktor számítása
        java.util.List<?> halfPart1 = list.subList(0, minEstimationSize / 2);
        java.util.List<?> halfPart2 = list.subList(minEstimationSize / 2, minEstimationSize);
        
        double halfPart1Value = delegate.value(halfPart1);
        double halfPart2Value = delegate.value(halfPart2);
        double sumHalves = halfPart1Value + halfPart2Value;
        
        if (sumHalves == 0) {
            return 0.0;
        }
        
        java.util.List<?> firstPart = list.subList(0, minEstimationSize);
        double firstPartValue = delegate.value(firstPart);
        double compensation = firstPartValue / sumHalves;

        // Páratlan blokkok mintavételezése: 1., 3., 5., 9., 17., 33...
        // Blokk indexek (0-based): 0, 2, 4, 8, 16, 32...
        double totalValue = firstPartValue;
        int samplesCount = 1;
        
        int blockIndex = 2;  // Következő: 3. blokk (0-based: index 2)
        int step = 2;        // Kezdeti lépésköz
        
        while (blockIndex * minEstimationSize < size) {
            int startPos = blockIndex * minEstimationSize;
            int endPos = Math.min(startPos + minEstimationSize, size);
            
            java.util.List<?> sample = list.subList(startPos, endPos);
            totalValue += delegate.value(sample);
            samplesCount++;
            
            blockIndex += step;
            step *= 2;  // 2, 4, 8, 16, 32...
        }

        // Becslés: átlagérték per minta * teljes méret / minta méret * kompenzáció
        return (totalValue / samplesCount) * (double) size / minEstimationSize * compensation;
    }

    public Value getBaseValue() {
        return delegate;
    }

    public int getMinEstimationSize() {
        return minEstimationSize;
    }

}