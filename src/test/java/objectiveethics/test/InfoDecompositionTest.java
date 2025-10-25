/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectiveethics.test;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import objectiveethics.commons.string.StringUtils;
import objectiveethics.values.logic.method.information.SCMInfo;
import objectiveethics.values.logic.method.information.SSMInfo;
import objectiveethics.values.logic.method.information.ShannonInfo;
import objectiveethics.values.structure.Value;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class InfoDecompositionTest {

    private static DecimalFormat format = new DecimalFormat("0");
    
    private static ShannonInfo shannon = new ShannonInfo();
    
    private static SCMInfo SCM = new SCMInfo();

    public static void main(String[] args) {

        testWithEqualR();
        testWithNonEqualR();
        testWithSimilarR();
        testWithSimilarR2();
        testWithSeparateR();
        testWithRepetitiveSequence();
        testWithMonotoneSequence();
    }

    private static void testWithEqualR() {
        String R = "ACGT";
        int M = 20;
        int N = 10;

        List<String> X = new ArrayList<>();

        String concatenatedX = "";

        for (int i = 0; i < M; i++) {
            String newX;
            do {
                newX = StringUtils.randomString(N, R);
            } while (!conainsAll(newX, R));
            X.add(newX);
            concatenatedX += newX;
        }

        test(X, concatenatedX, "Equal R");
    }

    private static void testWithNonEqualR() {
        String R1 = "ACGT";
        String R2 = "ACGE";
        int M = 20;
        int N = 10;

        List<String> X = new ArrayList<>();

        String concatenatedX = "";

        for (int i = 0; i < M; i++) {
            String newX;
            if (i == 1) {
                do {
                    newX = StringUtils.randomString(N, R1);
                } while (!conainsAll(newX, R1));
            } else {
                do {
                    newX = StringUtils.randomString(N, R2);
                } while (!conainsAll(newX, R2));
            }
            X.add(newX);
            concatenatedX += newX;
        }

        test(X, concatenatedX, "Different R");
    }

    private static void testWithSimilarR() {
        String[] R = new String[]{
            "ABCDEFGH",
            "ABCDEFGH",
            "ABCDEFGH",
            "ABCDEFGH",
            "ABCDEFGX",
            "ABCDEFGH",
            "ABCDEFGH",
            "ABCDEFGH",
            "ABCDEFGH",
            "ABCDEFGH"};
        int M = 10;
        int N = 32;

        List<String> X = new ArrayList<>();

        String concatenatedX = "";

        for (int i = 0; i < M; i++) {
            String newX;
            do {
                newX = StringUtils.randomString(N, R[i]);
            } while (!conainsAll(newX, R[i]));
            X.add(newX);
            concatenatedX += newX;
        }

        test(X, concatenatedX, "Similar R");
    }
    
    private static void testWithSimilarR2() {
        String[] R = new String[]{
            "A",
            "A",
            "A",
            "A",
            "B",
            "A",
            "A",
            "A",
            "A",
            "A"};
        int M = 10;
        int N = 10;

        List<String> X = new ArrayList<>();

        String concatenatedX = "";

        for (int i = 0; i < M; i++) {
            String newX;
            do {
                newX = StringUtils.randomString(N, R[i]);
            } while (!conainsAll(newX, R[i]));
            X.add(newX);
            concatenatedX += newX;
        }

        test(X, concatenatedX, "Similar R (2)");
    }

    private static void testWithSeparateR() {
        String[] R = new String[]{
            "ABCD",
            "EFGH",
            "IJKL",
            "MNOP",
            "QRST",
            "UVWZ",
            "1234",
            "5678",
            "-+/-",
            "@$ß¤"};
        int M = 10;
        int N = 10;

        List<String> X = new ArrayList<>();

        String concatenatedX = "";

        for (int i = 0; i < M; i++) {
            String newX;
            do {
                newX = StringUtils.randomString(N, R[i]);
            } while (!conainsAll(newX, R[i]));
            X.add(newX);
            concatenatedX += newX;
        }

        test(X, concatenatedX, "Separate R");
    }

    private static void testWithRepetitiveSequence() {

        List<String> X = new ArrayList<>();
        X.add("010101");
        X.add("010101");
        X.add("010101");
        X.add("010101");
        X.add("010101");

        String concatenatedX = "";

        for (int i = 0; i < X.size(); i++) {
            concatenatedX += X.get(i);
        }

        test(X, concatenatedX, "Repetitive sequence");
    }

    private static void testWithMonotoneSequence() {
        String[] R = new String[]{
            "1",
            "1",
            "1",
            "1",
            "1",
            "1",
            "1",
            "1",
            "1",
            "1"};
        int M = 10;
        int N = 10;

        List<String> X = new ArrayList<>();

        String concatenatedX = "";

        for (int i = 0; i < M; i++) {
            String newX;
            do {
                newX = StringUtils.randomString(N, R[i]);
            } while (!conainsAll(newX, R[i]));
            X.add(newX);
            concatenatedX += newX;
        }

        test(X, concatenatedX, "Monotone sequence");
    }

    private static void test(List<String> X, String concatenatedX, String message) {
        double separateShannonInfo = 0;
        double separateSMInfo = 0;
        double separateSHMInfo = 0;
        Value SSM = new SSMInfo();
        for (String x : X) {
            System.out.println(x + " > " + format.format(SCM.value(x)));
            separateShannonInfo += shannon.value(x);
            separateSMInfo += SSM.value(x);
            separateSHMInfo += SCM.value(x);
        }

        System.out.println(concatenatedX);

        double concatenatedShannonInfo = shannon.value(concatenatedX);
        double concatenatedSMInfo = SSM.value(concatenatedX);
        double concatenatedSHMInfo = SCM.value(concatenatedX);

        System.out.println();
        System.out.println(message + ":");
        System.out.println("SHANNON # Separated: " + format.format(separateShannonInfo)
                + ", concatenated: " + format.format(concatenatedShannonInfo)
                + ", " + comparisonString(separateShannonInfo, concatenatedShannonInfo));
        System.out.println("SFSM # Separated: " + format.format(separateSMInfo)
                + ", concatenated: " + format.format(concatenatedSMInfo)
                + ", " + comparisonString(separateSMInfo, concatenatedSMInfo));
        System.out.println("SMC # Separated: " + format.format(separateSHMInfo)
                + ", concatenated: " + format.format(concatenatedSHMInfo)
                + ", " + comparisonString(separateSHMInfo, concatenatedSHMInfo));
        System.out.println();
        System.out.println();
    }

    private static boolean conainsAll(String newX, String R) {
        for (char c : R.toCharArray()) {
            if (!newX.contains(String.valueOf(c))) {
                return false;
            }
        }
        return true;
    }

    private static String comparisonString(double separate, double concatenated) {
        if (separate < concatenated) {
            return "I(|Xi) > ΣI(Xi)";
        } else {
            return "I(|Xi) < ΣI(Xi)";
        }
    }
}
