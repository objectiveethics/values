/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectiveethics.values.logic.method.entropy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import objectiveethics.commons.math.fast.FastLog;
import objectiveethics.values.structure.BaseValue;
import objectiveethics.values.structure.Entropy;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
@BaseValue(
    id = 5,
    category = "entropy",
    acronym = "EMAX",
    name = "Maximum Entropy",
    description = "Calculates the maximum possible entropy for a dataset with the same number of " +
                  "unique symbols. Represents the theoretical upper bound of randomness that could " +
                  "be achieved if all symbols were uniformly distributed. Returns log2(k) where " +
                  "k is the number of unique symbols in the dataset.",
    algorithm = "1. Count the number of unique elements in dataset;\n" +
             "2. Calculate log2(unique_count);\n" +
             "3. Return maximum entropy value;\n" +
             "4. This represents uniform distribution entropy for the same symbol set"
)
public class MaxEntropy implements Entropy {

    @Override
    public double value(Collection<?> values) {
        if (values.size() <= 1) {
            return 0;
        }
        Set<?> atomicSet = new HashSet<>(values);
        int K = atomicSet.size();
        if (K == 1) {
            return 0;
        }
        return FastLog.log2(K);
    }

    @Override
    public double value(byte[] values) {
        if (values.length <= 1) {
            return 0;
        }
        int K = countUniqueBytes(values);
        if (K == 1) {
            return 0;
        }
        return FastLog.log2(K);
    }

    public static int countUniqueBytes(byte[] byteArray) {
        boolean[] seen = new boolean[256];
        int uniqueCount = 0;

        for (byte b : byteArray) {
            int index = b & 0xFF;
            if (!seen[index]) {
                seen[index] = true;
                uniqueCount++;
            }
        }

        return uniqueCount;
    }
}
