/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package objectiveethics.test;

import objectiveethics.values.logic.method.information.ShannonInfo;
import objectiveethics.values.structure.Value;

/**
 *
 * @author zsolt
 */
public class BinaryTest1 {
    
    public static void main(String[] args){

        Value info = new ShannonInfo();

        double x = 1023;
        String binary = "1111111111";  
        
  
        System.out.println("logInfo: " + (Math.log(x) / Math.log(2)));
        System.out.println("binaryInfo: " + info.value(binary));

        
    }
    
}
