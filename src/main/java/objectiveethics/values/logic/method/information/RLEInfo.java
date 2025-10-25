/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectiveethics.values.logic.method.information;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import objectiveethics.commons.math.fast.FastLog;
import objectiveethics.values.logic.method.util.InfoNormalizer;
import objectiveethics.values.logic.method.util.InfoUtils;
import objectiveethics.values.structure.Information;
import objectiveethics.values.structure.BaseValue;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
@BaseValue(
    id = 12,
    category = "information",
    acronym = "IRLE",
    name = "RLE Information",
    description = "Calculates information content using Run-Length Encoding (RLE) compression. " +
                  "Measures how much data can be compressed by encoding consecutive identical " +
                  "values as (value, count) pairs. Effective for detecting repetitive patterns " +
                  "and sequences in data, with better compression for highly repetitive content.",
    algorithm = "1. Scan input data sequentially;\n" +
             "2. Count consecutive identical elements (runs);\n" +
             "3. Encode each run as (value, count) pair;\n" +
             "4. Calculate total bits needed for RLE encoding;\n" +
             "5. Normalize against random and minimum compression baselines",
    article = "https://objectiveethics.com/values/docs/rle-information/"
)
public class RLEInfo implements Information {


    @Override
    public double value(byte[] input) {
        if (input == null || input.length < 1) {
            return 0;
        }

        if( input.length == 1 ) {
            return 1;
        }
        
        double rleInfo = calculateRleInfo(input);
        
        // Calculate min and max RLE info for normalization
        double minRleInfo = calculateRleInfo(new byte[input.length]); // All zeros
        double maxRleInfo = calculateRleInfo(InfoUtils.generateRandomByteArray(input)); // Random sequence
        
        // Normalize the RLE information between MinInfo and MaxInfo
        return InfoNormalizer.normalizeInfo(rleInfo, minRleInfo, maxRleInfo, input);
    }

    private static double calculateRleInfo(byte[] input) {
        
        byte[] rle = InfoUtils.performRleEncoding(input);
        Set<Byte> set = new HashSet<>();
        for (byte b : rle) {
            set.add(b);
        }
        
        return rle.length * FastLog.log2(set.size());
    }

    @Override
    public double value(Collection<?> values) {
        if (values == null || values.size() < 1) {
            return 0;
        }

        if( values.size() == 1 ) {
            return 1;
        }
        
        Object[] input = values.toArray();
        double rleInfo = calculateRleInfo(input);
        
        // Calculate min and max RLE info for normalization
        double minRleInfo = calculateRleInfo(new Object[values.size()]); // All nulls
        double maxRleInfo = calculateRleInfo(InfoUtils.generateRandomObjectArray(values)); // Random sequence
        
        // Normalize the RLE information between MinInfo and MaxInfo
        return InfoNormalizer.normalizeInfo(rleInfo, minRleInfo, maxRleInfo, values);
    }

    private static double calculateRleInfo(Object[] input) {
        
        List<Object> rle = InfoUtils.performRleEncoding(input);
        Set<Object> set = new HashSet<>(rle);

        return rle.size() * FastLog.log2(set.size());
    }
}
