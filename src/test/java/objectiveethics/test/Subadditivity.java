/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package objectiveethics.test;

/**
 *
 * @author zsolt
 */
public class Subadditivity {

    public static void main(String[] s) {

        for (int k = 1; k < 10; k++) {
            for (int n = 0; n < 10; n++) {
                for (int m = 0; m < 10; m++) {
                    double sum1 = 0;
                    for (int i = 1; i <= n; i++) {
                        sum1 += Math.pow(k, i);
                    }
                    double sum2 = 0;
                    for (int i = 1; i <= m; i++) {
                        sum2 += Math.pow(k, i);
                    }
                    double sum3 = 0;
                    for (int i = 1; i <= n + m; i++) {
                        sum3 += Math.pow(k, i);
                    }
                    double logSum1 = sum1 == 0 ? 0 : Math.log(sum1);
                    double logSum2 = sum2 == 0 ? 0 : Math.log(sum2);
                    double logSum3 = sum3 == 0 ? 0 : Math.log(sum3);
                    System.out.println(k + ";" + n + ";" + m + ";" + (logSum1 + logSum2 - logSum3));
                }
            }
        }

    }

}
