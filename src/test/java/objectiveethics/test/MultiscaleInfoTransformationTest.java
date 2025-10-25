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
public class MultiscaleInfoTransformationTest {

    private static DecimalFormat format = new DecimalFormat("0");
    
    private static ShannonInfo shannon = new ShannonInfo();
    
    private static Value SSM = new SSMInfo();

    public static void main(String[] args) {

        int[][] data = new int[][]{
            {0, 1, 8, 9, 3, 4, 5, 6, 7, 2},
            {9, 1, 5, 8, 3, 2, 5, 9, 0, 2},
            {7, 2, 9, 0, 4, 2, 4, 4, 5, 7},
            {5, 4, 3, 5, 3, 3, 8, 9, 2, 2},
            {2, 2, 3, 4, 0, 1, 9, 2, 5, 0},
            {2, 4, 0, 9, 4, 3, 2, 3, 8, 0},
            {4, 6, 2, 5, 5, 1, 0, 6, 7, 3},
            {9, 1, 4, 2, 1, 7, 4, 2, 4, 6},
            {4, 9, 0, 4, 2, 7, 4, 2, 4, 5},
            {3, 7, 5, 1, 5, 6, 2, 3, 5, 6},};

        information("Source", toStringForward(data));
        information("Forward", toStringForward(data));
        information("Backward", toStringBackward(data));
        information("Flipped", toStringFlipped(data));
        information("Double flipped", toStringFlippedTwice(data));
    }

    private static String toStringForward(int[][] data) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                s.append(data[i][j]);
            }
        }
        return s.toString();
    }

    private static String toStringBackward(int[][] data) {
        StringBuilder s = new StringBuilder();
        for (int i = data.length - 1; i >= 0; i--) {
            for (int j = data[i].length - 1; j >= 0; j--) {
                s.append(data[i][j]);
            }
        }
        return s.toString();
    }

    private static String toStringFlipped(int[][] data) {
        StringBuilder s = new StringBuilder();
        for (int i = data.length - 1; i >= 0; i--) {
            for (int j = 0; j < data[i].length; j++) {
                s.append(data[i][j]);
            }
        }
        return s.toString();
    }

    private static String toStringFlippedTwice(int[][] data) {
        StringBuilder s = new StringBuilder();
        for (int j = 0; j < data[0].length; j++) {
            for (int i = 0; i < data.length; i++) {
                s.append(data[i][j]);
            }
        }
        return s.toString();
    }

    private static void information(String note, String list) {
        System.out.println(note + ";"
                + format.format(shannon.value(list)) + ";"
                + format.format(SSM.value(list)));
    }
    
    private static void information(String note, Object list) {
 
        System.out.println(note + ";"
                + format.format(shannon.value(list)) + ";"
                + format.format(SSM.value(list)));
    }

}
