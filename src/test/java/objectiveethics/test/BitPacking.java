/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectiveethics.test;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class BitPacking {


    private static int neededBits(int number) {
        return number == 0 ? 1 : (int) (Math.floor(Math.log(number) / Math.log(2)) + 1);
    }

    public static byte[] encode(char[] input) {
        int maxNumber = 0;
        for (char ch : input) {
            maxNumber = Math.max(maxNumber, ch);
        }

        int bitsPerChar = neededBits(maxNumber);
        List<Byte> packedData = new ArrayList<>();
        int buffer = 0;
        int bitCount = 0;

        for (char ch : input) {
            buffer |= (ch << bitCount);
            bitCount += bitsPerChar;

            while (bitCount >= 8) {
                packedData.add((byte) buffer);
                buffer >>>= 8;
                bitCount -= 8;
            }
        }

        if (bitCount > 0) {
            packedData.add((byte) buffer);
        }

        byte[] result = new byte[packedData.size()];
        for (int i = 0; i < packedData.size(); i++) {
            result[i] = packedData.get(i);
        }

        return result;
    }

    public static void main(String[] args) {
        char[] input = "Hello, World!".toCharArray();
        byte[] encodedData = encode(input);

        System.out.print("Tömörített adatok: ");
        for (byte b : encodedData) {
            System.out.print(String.format("%02X ", b));
        }
    }
}
