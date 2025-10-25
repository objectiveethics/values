/*
 * Copyright 2024 valyo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package objectiveethics.values.logic.method.entropy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import objectiveethics.commons.math.fast.FastLog;
import objectiveethics.values.structure.BaseValue;
import objectiveethics.values.structure.Entropy;

/**
 * Computes the min-entropy of data using the formula H = -log(max(p_i)).
 * Min-entropy measures the worst-case scenario for guessing the most likely outcome.
 *
 * @author valyo
 */
@BaseValue(
    id = 28,
    category = "entropy",
    acronym = "EMIN",
    name = "Min Entropy",
    description = "Calculates Min Entropy, which measures the worst-case predictability of a " +
                  "dataset by focusing on the most probable symbol. Returns -log2(max(p_i)) " +
                  "where max(p_i) is the highest probability among all symbols. Lower values " +
                  "indicate higher predictability, making it useful in cryptography and security analysis.",
    algorithm = "1. Count frequency of each unique element in dataset;\n" +
             "2. Calculate probability for each element (frequency / total_count);\n" +
             "3. Find the maximum probability max(p_i);\n" +
             "4. Calculate Min entropy: -log2(max(p_i));\n" +
             "5. Return the min-entropy value"
)
public class MinEntropy implements Entropy {

    @Override
    public double value(boolean[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
        Map<Boolean, Integer> map = new HashMap<>();
        for (boolean x : values) {
            Integer frequency = map.get(x);
            if (frequency == null) {
                frequency = 0;
            }
            map.put(x, frequency + 1);
        }
        return calculateMinEntropy(map, values.length);
    }

    @Override
    public double value(byte[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
        Map<Byte, Integer> map = new HashMap<>();
        for (byte x : values) {
            Integer frequency = map.get(x);
            if (frequency == null) {
                frequency = 0;
            }
            map.put(x, frequency + 1);
        }
        return calculateMinEntropy(map, values.length);
    }

    @Override
    public double value(short[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
        Map<Short, Integer> map = new HashMap<>();
        for (short x : values) {
            Integer frequency = map.get(x);
            if (frequency == null) {
                frequency = 0;
            }
            map.put(x, frequency + 1);
        }
        return calculateMinEntropy(map, values.length);
    }

    @Override
    public double value(int[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
        Map<Integer, Integer> map = new HashMap<>();
        for (int x : values) {
            Integer frequency = map.get(x);
            if (frequency == null) {
                frequency = 0;
            }
            map.put(x, frequency + 1);
        }
        return calculateMinEntropy(map, values.length);
    }

    @Override
    public double value(float[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
        Map<Float, Integer> map = new HashMap<>();
        for (float x : values) {
            Integer frequency = map.get(x);
            if (frequency == null) {
                frequency = 0;
            }
            map.put(x, frequency + 1);
        }
        return calculateMinEntropy(map, values.length);
    }

    @Override
    public double value(double[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
        Map<Double, Integer> map = new HashMap<>();
        for (double x : values) {
            Integer frequency = map.get(x);
            if (frequency == null) {
                frequency = 0;
            }
            map.put(x, frequency + 1);
        }
        return calculateMinEntropy(map, values.length);
    }

    @Override
    public double value(char[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
        Map<Character, Integer> map = new HashMap<>();
        for (char x : values) {
            Integer frequency = map.get(x);
            if (frequency == null) {
                frequency = 0;
            }
            map.put(x, frequency + 1);
        }
        return calculateMinEntropy(map, values.length);
    }

    @Override
    public double value(String[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
        Map<String, Integer> map = new HashMap<>();
        for (String x : values) {
            Integer frequency = map.get(x);
            if (frequency == null) {
                frequency = 0;
            }
            map.put(x, frequency + 1);
        }
        return calculateMinEntropy(map, values.length);
    }

    @Override
    public double value(Collection<?> values) {
        if (values == null || values.size() <= 1) {
            return 0;
        }
        Map<Object, Integer> map = new HashMap<>();
        for (Object x : values) {
            Integer frequency = map.get(x);
            if (frequency == null) {
                frequency = 0;
            }
            map.put(x, frequency + 1);
        }
        return calculateMinEntropy(map, values.size());
    }

    private <T> double calculateMinEntropy(Map<T, Integer> frequencyMap, int totalCount) {
        // Find maximum frequency
        int maxFrequency = frequencyMap.values().stream().max(Integer::compareTo).orElse(1);
        
        // Calculate maximum probability
        double maxProbability = (double) maxFrequency / totalCount;
        
        // Calculate min-entropy: H = -log2(max(p_i))
        return -FastLog.log2(maxProbability);
    }

    @Override
    public boolean usesNaturalLogarithm() {
        return false; // Uses base-2 logarithm
    }
}
