/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectiveethics.values.structure;

import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public interface Value {

    default double value(Character[] values) {
        return value(Arrays.asList(values));
    }

    default double value(Byte[] values) {
        return value(Arrays.asList(values));
    }

    default double value(Short[] values) {
        return value(Arrays.asList(values));
    }

    default double value(Integer[] values) {
        return value(Arrays.asList(values));
    }

    default double value(Long[] values) {
        return value(Arrays.asList(values));
    }

    default double value(String[] values) {
        return value(Arrays.asList(values));
    }

    default double value(String values) {
        if (values == null || values.length() < 1) {
            return 0;
        }
        return value(values.getBytes());
    }

    double value(Collection<?> values);

    default double value(boolean[] values) {
        if (values == null) {
            return 0;
        }
        byte[] byteArray = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            byteArray[i] = (byte) (values[i] ? 1 : 0);
        }
        return value(byteArray);
    }

    default double value(short[] values) {
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

    default double value(int[] values) {
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

    default double value(float[] values) {
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

    default double value(double[] values) {
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

    default double value(char[] values) {
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

    default double value(Object object) {
        if (object == null) {
            return 0;
        }
        return value(object.toString());
    }

    double value(byte[] values);
}
