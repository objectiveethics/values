/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectiveethics.values.logic.method.information;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import objectiveethics.values.structure.Information;
import objectiveethics.commons.math.fast.FastLog;
import objectiveethics.values.structure.BaseValue;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
@BaseValue(
    id = 10,
    category = "information",
    acronym = "IMAX",
    name = "Maximum Information",
    description = "Calculates the maximum possible information content for a given dataset. " +
                  "This metric represents the theoretical upper bound of information that can be " +
                  "contained in a collection of values with a specific number of unique elements.",
    algorithm = "1. Check if input is null or empty;\n" +
             "2. Count unique elements in the dataset;\n" +
             "3. Calculate log2(unique_count) as maximum information;\n" +
             "4. Maximum information is achieved when all elements are equally probable",
    article = "https://objectiveethics.com/values/docs/maximum-information/"
)
public class MaxInfo implements Information {


    @Override
    public double value(Collection<?> values) {
        if (values == null) {
            return 0;
        }
        if (values.isEmpty()) {
            return 0;
        }
        if (values.size() == 1) {
            return 1;
        }
        Set<?> atomicSet = new HashSet<>(values);
        int k = atomicSet.size();
        int n = values.size();
        double v = n * FastLog.log2(k);
        if (v > 500) {
            return v;
        }
        return value(n, k);
    }

    @Override
    public double value(byte[] values) {
        if (values == null) {
            return 0;
        }
        if (values.length == 0) {
            return 0;
        }
        if (values.length == 1) {
            return 1;
        }
        int k = countUniqueBytes(values);
        int n = values.length;
        double v = n * FastLog.log2(k);
        if (v > 500) {
            return v;
        }
        return value(n, k);
    }

    private double value(double n, double k) {
        if (k == 1) {
            return FastLog.log2(n + 1);
        }
        return FastLog.log2((Math.pow(k, n + 1) - 1) / (k - 1));
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
