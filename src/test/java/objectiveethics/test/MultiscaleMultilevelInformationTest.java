/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectiveethics.test;

import java.text.DecimalFormat;
import objectiveethics.values.logic.method.information.SCMInfo;
import objectiveethics.values.logic.method.information.SSMInfo;
import objectiveethics.values.logic.method.information.ShannonInfo;
import objectiveethics.values.structure.Value;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class MultiscaleMultilevelInformationTest {

    private static DecimalFormat format = new DecimalFormat("0");
    
    private static ShannonInfo shannon = new ShannonInfo();
    
    private static Value SSM = new SSMInfo();
    
    private static SCMInfo SCM = new SCMInfo();

    public static void main(String[] args) {

        information("Random DNS", "cagtttctagctatattagcgggcacgactccactgcgcctatgcggaagcttgatcaaattttgaccagatcttaggtaacctgaacaagtcagttcgtaggcgtcgattggccgacgggtgcgaagaaaaaagtgatcgttgtccaacatctctagtacccaccgttgtgatgtacgttatacggacacgagcatatt");

        information("Random DNS (BINARY)", "1000011111111011000110110011001111000110010101100010010010111010001011011001101011001101100101000001101111010011100000001111111101001010000100111011110001011100001010110100001000000111100001111110011100010110011110010011110101101001001001010111011001000001000000000000011101001110011111011110100000100011101110110001110010101000101001111101110100110111001001111100110010010100100010010001100011001111");

        information("Cyclic DNS", "agctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagctagct");

        information("Cyclic DNS (BINARY)", "0001101100011011000110110001101100011011000110110001101100011011000110110001101100011011000110110001101100011011000110110001101100011011000110110001101100011011000110110001101100011011000110110001101100011011000110110001101100011011000110110001101100011011000110110001101100011011000110110001101100011011000110110001101100011011000110110001101100011011000110110001101100011011000110110001101100011011");

    }

    private static void information(String note, String value) {
        System.out.println(note 
                + ": " + format.format(shannon.value(value))
                + ", " + format.format(SSM.value(value))
                + ", " + format.format(SCM.value(value)));
    }

}
