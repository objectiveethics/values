/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package objectiveethics.test;

import java.text.DecimalFormat;

import objectiveethics.commons.math.fast.FastLog;
import objectiveethics.values.logic.method.information.GZIPInfo;
import objectiveethics.values.structure.Value;

/**
 *
 * @author zsolt
 */
public class BinaryTest {
    
    public static void main(String[] args){
        
        DecimalFormat format = new DecimalFormat("0.0000");
        
        Value info = new GZIPInfo();
        
        double logInfo = 0;
        double binaryInfo = 0;
        
        int n = 10000;
        
        String binary;
        
        long start = System.currentTimeMillis();
        
        for(int x = 1; x<n; x++){

            binary = Integer.toBinaryString(x);
            
            logInfo += FastLog.log2(x);
            binaryInfo += info.value(binary);
            
            if(x%10000 == 0){
                double t = System.currentTimeMillis() - start;
                System.out.println(x + " - left: " + Math.ceil(t / x * n / 1000 / 60) + " min");
            }
        }
        
        System.out.println("logInfo: " + format.format(logInfo / n));
        System.out.println("binaryInfo: " + format.format(binaryInfo / n));
        System.out.println("ratio: " + format.format(binaryInfo / logInfo));
        
    }
    
}
