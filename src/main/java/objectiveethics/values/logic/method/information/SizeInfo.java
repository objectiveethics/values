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

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
@BaseValue(
    id = 17,
    category = "information",
    acronym = "ISIZE",
    name = "Size",
    description = "Calculates the bit length of input data. Provides the total number of bits " +
                  "required to represent the input in its binary form. This is a fundamental " +
                  "measurement that gives the raw size of data without considering compression " +
                  "or information content patterns.",
    algorithm = "1. Convert input data to byte array representation;\n" +
             "2. Multiply the byte array length by 8 to get total bits;\n" +
             "3. Return the bit length as the size information",
    article = "https://en.wikipedia.org/wiki/Information_theory"
)
public class SizeInfo implements Information {

    @Override
    public double value(Object object) {
        if (object == null) {
            return 0;
        }
        byte[] array = ArrayUtils.toByteArray(object);
        return array.length * 8.0;
    }

    @Override
    public double value(boolean[] values) {
        if (values == null) {
            return 0;
        }
        return values.length;  // Each boolean is 1 bit
    }

    @Override
    public double value(byte[] values) {
        if (values == null) {
            return 0;
        }
        return values.length * 8.0;  // Each byte is 8 bits
    }

    @Override
    public double value(short[] values) {
        if (values == null) {
            return 0;
        }
        return values.length * 16.0;  // Each short is 16 bits
    }

    @Override
    public double value(int[] values) {
        if (values == null) {
            return 0;
        }
        return values.length * 32.0;  // Each int is 32 bits
    }

    @Override
    public double value(float[] values) {
        if (values == null) {
            return 0;
        }
        return values.length * 32.0;  // Each float is 32 bits
    }

    @Override
    public double value(double[] values) {
        if (values == null) {
            return 0;
        }
        return values.length * 64.0;  // Each double is 64 bits
    }

    @Override
    public double value(char[] values) {
        if (values == null) {
            return 0;
        }
        return values.length * 16.0;  // Each char is 16 bits in Java
    }

    public double value(String values) {
        if (values == null) {
            return 0;
        }
        return values.length() * 16.0;  // Each char is 16 bits in Java
    }

    @Override
    public double value(Collection<?> values) {
        if (values == null) {
            return 0;
        }
        byte[] array = ArrayUtils.toByteArray(values);
        return array.length * 8.0;
    }
}
