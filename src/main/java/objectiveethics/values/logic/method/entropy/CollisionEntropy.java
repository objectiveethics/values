/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectiveethics.values.logic.method.entropy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import objectiveethics.commons.math.fast.FastLog;
import objectiveethics.values.structure.BaseValue;
import objectiveethics.values.structure.Entropy;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
@BaseValue(
    id = 27,
    category = "entropy",
    acronym = "EC",
    name = "Collision Entropy",
    description = "Calculates Collision Entropy, which measures the probability of collision " +
                  "(finding two identical symbols) in the dataset. This entropy is particularly " +
                  "useful in cryptography and information theory for analyzing randomness quality " +
                  "and estimating the difficulty of guessing attacks.",
    algorithm = "1. Count frequency of each unique element in dataset;\n" +
             "2. Calculate probability for each element (frequency / total_count);\n" +
             "3. For each element, compute p² (probability squared);\n" +
             "4. Sum all p² values to get collision probability;\n" +
             "5. Calculate Collision entropy: -log2(sum of p²)"
)
public class CollisionEntropy implements Entropy {
    
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
        return calculateCollisionEntropy(map, values.length);
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
        return calculateCollisionEntropy(map, values.length);
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
        return calculateCollisionEntropy(map, values.length);
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
        return calculateCollisionEntropy(map, values.length);
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
        return calculateCollisionEntropy(map, values.length);
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
        return calculateCollisionEntropy(map, values.length);
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
        return calculateCollisionEntropy(map, values.length);
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
        return calculateCollisionEntropy(map, values.size());
    }

    private double calculateCollisionEntropy(Map<?, Integer> frequencyMap, int totalCount) {
        double collisionProbability = 0.0;
        for (Integer frequency : frequencyMap.values()) {
            double probability = (double) frequency / totalCount;
            collisionProbability += probability * probability; // p²
        }
        
        if (collisionProbability == 0 || collisionProbability == 1) {
            return 0; // No entropy when all elements are the same or different
        }
        
        return -FastLog.log2(collisionProbability);
    }
}
