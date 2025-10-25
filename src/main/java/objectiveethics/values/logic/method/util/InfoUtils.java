/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectiveethics.values.logic.method.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

/**
 * Utility class for value-related operations.
 * This class provides common functionality for generating random sequences
 * and other value manipulation operations.
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class InfoUtils {

    /**
     * Generates a random byte array with the same length and unique values as the original.
     * The random array contains only the unique values found in the original array,
     * but in random order and distribution.
     * 
     * @param original The original byte array to base the random generation on
     * @return A random byte array with same length and unique values as original
     */
    public static byte[] generateRandomByteArray(byte[] original) {
        if (original == null || original.length == 0) {
            return new byte[0];
        }

        Set<Byte> uniqueValues = new HashSet<>();
        for (byte b : original) {
            uniqueValues.add(b);
        }

        byte[] uniqueArray = new byte[uniqueValues.size()];
        int index = 0;
        for (Byte value : uniqueValues) {
            uniqueArray[index++] = value;
        }

        Random random = new Random();
        byte[] result = new byte[original.length];

        for (int i = 0; i < result.length; i++) {
            int randomIndex = random.nextInt(uniqueArray.length);
            result[i] = uniqueArray[randomIndex];
        }

        return result;
    }

    /**
     * Generates a random object array with the same length and unique values as the original collection.
     * The random array contains only the unique values found in the original collection,
     * but in random order and distribution.
     * 
     * @param original The original collection to base the random generation on
     * @return A random object array with same length and unique values as original
     */
    public static Object[] generateRandomObjectArray(Collection<?> original) {
        if (original == null || original.isEmpty()) {
            return new Object[0];
        }

        Set<Object> uniqueValues = new HashSet<>(original);
        Object[] uniqueArray = uniqueValues.toArray();

        Random random = new Random();
        Object[] result = new Object[original.size()];

        for (int i = 0; i < result.length; i++) {
            int randomIndex = random.nextInt(uniqueArray.length);
            result[i] = uniqueArray[randomIndex];
        }

        return result;
    }

    /**
     * Performs Run-Length Encoding on a byte array.
     * 
     * @param input The byte array to encode
     * @return The RLE-encoded byte array
     */
    public static byte[] performRleEncoding(byte[] input) {
        if (input == null || input.length == 0) {
            return new byte[0];
        }
        
        byte[] rle = new byte[input.length * 2];
        int pos = 0;

        for (int i = 0; i < input.length; i++) {
            int count = 1;
            byte current = input[i];

            if (current == '\0') {
                rle[pos++] = current;
                rle[pos++] = current;
                continue;
            }

            while (i + 1 < input.length && current == input[i + 1] && count < 255) {
                 i++;
                count++;
            }

            if (count > 2) {
                rle[pos++] = '\0';
                rle[pos++] = (byte)count;
                rle[pos++] = current;
            } else {
                for (int j = 0; j < count; j++) {
                    rle[pos++] = current;
                }
            }
        }
        
        byte[] actualRle = new byte[pos];
        System.arraycopy(rle, 0, actualRle, 0, pos);
        return actualRle;
    }

    /**
     * Performs Run-Length Encoding on an object array.
     * 
     * @param input The object array to encode
     * @return The RLE-encoded list
     */
    public static List<Object> performRleEncoding(Object[] input) {
        if (input == null || input.length == 0) {
            return new ArrayList<>();
        }
        
        List<Object> rle = new ArrayList<>(input.length * 2);

        for (int i = 0; i < input.length; i++) {
            int count = 1;
            Object current = input[i];

            if (current == null) {
                rle.add(current);
                rle.add(current);
                continue;
            }

            while (i + 1 < input.length && Objects.equals(current, input[i + 1]) && count < 255) {
                 i++;
                count++;
            }

            if (count > 2) {
                rle.add(null);
                rle.add(count);
                rle.add(current);
            } else {
                for (int j = 0; j < count; j++) {
                    rle.add(current);
                }
            }
        }
        
        return rle;
    }
}
