/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectiveethics.values.logic.method.information;

import java.util.Collection;
import objectiveethics.commons.primitive.ArrayUtils;
import objectiveethics.values.structure.Information;
import objectiveethics.values.structure.BaseValue;
import objectiveethics.values.logic.method.entropy.ShannonEntropy;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
@BaseValue(
    id = 15,
    category = "information",
    acronym = "IS",
    name = "Shannon Information",
    description = "Calculates Shannon information content of a dataset by multiplying Shannon entropy " +
                  "with the length of the data. Represents the total amount of information (in bits) " +
                  "contained in the entire dataset based on symbol probabilities.",
    algorithm = "1. Calculate frequency of each unique element;\n" +
             "2. Compute probability for each element (frequency / total_count);\n" +
             "3. Calculate Shannon entropy: -Σ(p * log2(p));\n" +
             "4. Multiply entropy by dataset length to get total information",
    article = "https://objectiveethics.com/values/docs/shannon-information/"
)
public class ShannonInfo implements Information {

    private final ShannonEntropy shannonEntropy = new ShannonEntropy();


    @Override
    public double value(Object object) {
        if (object == null) {
            return 0;
        }
        byte[] array = ArrayUtils.toByteArray(object);
        return shannonEntropy.value(array) * array.length;
    }

    @Override
    public double value(boolean[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
        return shannonEntropy.value(values) * values.length;
    }

    @Override
    public double value(byte[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
        return shannonEntropy.value(values) * values.length;
    }

    @Override
    public double value(short[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
        return shannonEntropy.value(values) * values.length;
    }

    @Override
    public double value(int[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
        return shannonEntropy.value(values) * values.length;
    }

    @Override
    public double value(float[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
        return shannonEntropy.value(values) * values.length;
    }

    @Override
    public double value(double[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
        return shannonEntropy.value(values) * values.length;
    }

    @Override
    public double value(char[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
        return shannonEntropy.value(values) * values.length;
    }

    @Override
    public double value(Collection<?> values) {
        if (values == null || values.size() <= 1) {
            return 0;
        }
        return shannonEntropy.value(values) * values.size();
    }

}
