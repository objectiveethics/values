/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package objectiveethics.test;

import java.text.DecimalFormat;
import objectiveethics.values.logic.method.information.ShannonInfo;
import objectiveethics.values.structure.Value;

/**
 *
 * @author zsolt
 */
public class BinaryTest2 {

    public static void main(String[] args) {

        DecimalFormat format = new DecimalFormat("0.0000");

        Value info = new ShannonInfo();

        double logInfoSum = 0;
        double binaryInfoSum = 0;

        double log2 = Math.log(2);

        String binary;

        for (int n = 10; n < 1000000000; n *= 10) {

            for (int x = 1; x < n; x++) {

                binary = Integer.toBinaryString(x);
//            
//            System.out.println(format.format(logInfo) + " " + x);
//            System.out.println(format.format(binaryInfo) + " " + binary);
//            System.out.println();

                logInfoSum += Math.log(x) / log2;
                binaryInfoSum += info.value(binary);
            }

//            System.out.println(n);
//            System.out.println("logInfo: " + format.format(logInfoSum / n));
//            System.out.println("binaryInfo: " + format.format(binaryInfoSum / n));
            System.out.println(n+";"+format.format(binaryInfoSum / logInfoSum));
//            System.out.println();
        }

    }

    private static String binaryConvert(String b) {
        if (b.length() < 3) {
            return b;
        }
        char first = b.charAt(0);
        for (int i = 0; i < b.length(); i++) {
            if (b.charAt(i) != first) {
                return b;
            }
        }
        return "2" + Integer.toBinaryString(b.length());
    }

}
