/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectiveethics.test;

import java.util.zip.Deflater;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class DeflaterTest {
    
    public static void main(String[] args){
        byte[] input = "n programming, it’s often useful to reduce the amount of data that needs to be stored or transmitted. Compression is a technique used to do this. In Java, one way to compress data is with Java Compress String. This article will explain what Java Compress String is, how to use it, and the advantages and security considerations to keep in mind when working with it.".getBytes();
        byte[] output = new byte[input.length];

        Deflater deflater = new Deflater();
        deflater.setInput(input);
        deflater.finish();

        int size = deflater.deflate(output);
        
        System.out.println(new String(output));
        
        System.out.println(input.length + " -> " + size);
    }
    
}
