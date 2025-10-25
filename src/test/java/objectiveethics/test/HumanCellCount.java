/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package objectiveethics.test;

import java.util.Random;

/**
 *
 * @author zsolt
 */
public class HumanCellCount {

    // A fuggveny amit illesztunk:
    // f(x) = A * ln( x/B + 1 ) / ( x/B + 1 )
    static double f(double x, double A, double B) {
        if (B <= 0) {
            // B-nek muszaj pozitivnak lennie
            return Double.NaN;
        }
        double ratio = x / B;
        double denom = ratio + 1.0;
        if (denom <= 0) {
            // ln(denom) ervenytelen, ha denom <= 0
            return Double.NaN;
        }
        double val = Math.log(denom) / denom;
        return A * val;
    }

    // A negyzetes hibaosszeg:
    // cost(A,B) = sum_i [f(x_i; A,B) - y_i]^2
    static double computeCost(double A, double B, double[][] data) {
        double sumSq = 0.0;
        for (double[] row : data) {
            double x = row[0];
            double y = row[1];
            double fx = f(x, A, B);
            double diff = fx - y;
            sumSq += Math.abs(diff);
        }
        return sumSq;
    }

    public static void main(String[] args) {
        // Fiktiv peldadatok: (x,y) a tablazatban
        // Tetszolegesen modosithatod a sajat adataidra
        double[][] data = {
            // { Másodperc, Sejtszám }
            {0, 1.0e0},
            {604800, 1.0e4},
            {1512000, 1.0e6},
            {5140800, 1.0e8},
            {24192000, 2.50e12},
            {55728000, 5.00e12},
            {181872000, 1.00e13},
            {339552000, 2.00e13},
            {497232000, 3.00e13},
            {654912000, 3.70e13},
            {1916352000.0, 3.70e13},
            {2547072000.0, 3.40e13},
            {3177792000.0, 3.00e13}
        };

        // Kiindulasi ertekek (feladat szerint):
        double A0 = 2.3e14;
        double B0 = 7.0e8;

        // Allitsuk be a kezdo parametereket a keresben:
        double bestA = A0;
        double bestB = B0;
        double bestCost = computeCost(bestA, bestB, data);

        System.out.printf("Kezdeti: A=%.6g, B=%.6g, cost=%.6g%n", bestA, bestB, bestCost);

        // 10% intervallum a KIINDULASI ertekek korul
        double rangeA = 0.1 * A0;
        double rangeB = 0.1 * B0;

        // Iteracios beallitasok
        int maxIterations = 10000000;
        Random rand = new Random();

        for (int i = 1; i <= maxIterations; i++) {
            // 1) Veletlenszeru lepes:
            //    ±10% a kezdőertekekhez kepest
            double trialA = bestA + (rand.nextDouble() * 2.0 - 1.0) * rangeA;
            double trialB = bestB + (rand.nextDouble() * 2.0 - 1.0) * rangeB;

            // 2) Ellenorizzuk, hogy B pozitiv maradjon
            if (trialB <= 0.0) {
                continue; // Egyszeruen kihagyjuk ezt a probat
            }

            // 3) Szamitsuk ki az uj cost-ot
            double trialCost = computeCost(trialA, trialB, data);

            // 4) Ha javit, elfogadjuk
            if (trialCost < bestCost) {
                bestA = trialA;
                bestB = trialB;
                bestCost = trialCost;
            }

            // 5) Par szaz iteracionkent kiirunk infot
            if (i % 1000 == 0) {
                System.out.printf("Iter=%d: A=%.6g, B=%.6g, cost=%.6g%n",
                        i, bestA, bestB, bestCost);
            }
        }

        System.out.println("=== Vegeredmeny ===");
        System.out.printf("A=%.6g%n", bestA);
        System.out.printf("B=%.6g%n", bestB);
        System.out.printf("Cost=%.6g%n", bestCost);
    }
}
