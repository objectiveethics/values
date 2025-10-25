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
public class MultiscaleInfoConvergenceTest1 {

    private static DecimalFormat format = new DecimalFormat("0");
    
    private static ShannonInfo shannon = new ShannonInfo();
    
    private static Value SSM = new SSMInfo();

    public static void main(String[] args) {

        String s1 = "1011110010011111010110110011111010110101010100101101001011100001010110000111100111101011110010111011";
        String s2 = "1100000110110110111011111111011010110101011011001010010001011111010001001101001111100100001001100100";

        String a = "0";
        String b = "1";

        while (containsDeviantPart(s1, a) || containsDeviantPart(s2, b)) {
            information(s1 + s2);
            s1 = replaceDeviantPart(s1, a);
            s2 = replaceDeviantPart(s2, b);
        }
        information(s1 + s2);
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
            if(!containsDeviantPart(s, expected)){
                return s;
            }
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
