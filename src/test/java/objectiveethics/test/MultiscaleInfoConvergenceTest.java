/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectiveethics.test;

import java.text.DecimalFormat;
import objectiveethics.values.logic.method.information.ShannonInfo;
import objectiveethics.values.logic.method.information.SSMInfo;
import objectiveethics.values.structure.Value;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class MultiscaleInfoConvergenceTest {

    private static DecimalFormat format = new DecimalFormat("0");
    
    private static ShannonInfo shannon = new ShannonInfo();
    
    private static Value SSM = new SSMInfo();

    public static void main(String[] args) {

        String s = "10011110110000111001101001010111011111101111000000110001010001111101111100111001101111010001101100110010011110101101010110101010101011101110010010110011011001000101100101110011100000111011110110010000";

        test(s, "0");
        test(s, "01");
        test(s, "0001");
        test(s, "00011011");
        test(s, "000001010011100101110111");
    }

    private static void test(String s, String rep) {
        while (containsDeviantPart(s, rep)) {
            information(s);
            s = replaceDeviantPart(s, rep);
        }
        information(s);
        System.out.println("---------------------------");
    }

    private static boolean containsDeviantPart(String s, String expected) {
        for (int i = 0; i < s.length() - expected.length(); i += expected.length()) {
            if (!s.substring(i, i + expected.length()).equals(expected)) {
                return true;
            }
        }
        return false;
    }

    private static String replaceDeviantPart(String s, String expected) {
        while (true) {
            int pos = (int) (Math.random() * (s.length() / expected.length())) * expected.length();
            if (!s.substring(pos, pos + expected.length()).equals(expected)) {
                return s.substring(0, pos) + expected + s.substring(pos + expected.length());
            }
        }
    }

    private static void information(String value) {
        System.out.println(format.format(shannon.value(value)) + ";"
                + format.format(SSM.value(value)) 
//                        + ";"
//                + format.format(SpectrumInformation1.information(value)) + ";"
//                + format.format(SpectrumInformation2.information(value))
        );
    }

}
