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

import objectiveethics.commons.collection.CollectionUtils;
import objectiveethics.commons.math.fast.FastLog;
import objectiveethics.values.logic.method.entropy.ShannonEntropy;
import objectiveethics.values.structure.BaseValue;
import objectiveethics.values.structure.Information;

/**
 * Shannon Spectrum Minimum
 *
 * @author Volgyerdo Nonprofit Kft.
 */
@BaseValue(
    id = 16,
    category = "information",
    acronym = "ISSM",
    name = "SSM Information",
    description = "Shannon Spectrum Minimum information measure that analyzes data across multiple " +
                  "scales and dimensions. Finds the minimum Shannon information value across " +
                  "different spectral decompositions of the data. Useful for detecting the most " +
                  "compressed representation and identifying intrinsic information content.",
    algorithm = "1. Apply different spectral transformations to the data;\n" +
             "2. For each transformation, calculate Shannon information;\n" +
             "3. Analyze data at multiple scales and resolutions;\n" +
             "4. Find the transformation yielding minimum Shannon information;\n" +
             "5. Return the minimum information value across all transformations"
)
public class SSMInfo implements Information{
    
    private ShannonInfo shannonInfo = new ShannonInfo();
    private final ShannonEntropy shannonEntropy = new ShannonEntropy();


    @Override
    public double value(byte[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
        double minimumInfo = 0;
        double absoluteMax = 0;
        Set<Byte> atomicSet = new HashSet<>();
        for(byte b : values){
            atomicSet.add(b);
        }
        int K = atomicSet.size();
        if (K == 1) {
            return FastLog.log2(values.length);
        }
        int N = values.length;
        double atomicInfo = shannonEntropy.value(values);
        absoluteMax = maxInformation(N, K, 1);

        for (int r = 1; r <= N / 2; r++) {
            List<byte[]> parts = CollectionUtils.breakApart(values, r, false);
            double maxInfo = maxInformation(N, K, r);
            double actualInfo = shannonInfo.value(parts);
            actualInfo = maxInfo == 0 ? 0 : actualInfo / maxInfo * absoluteMax;
            if (actualInfo == 0) {
                actualInfo = atomicInfo * r + FastLog.log2(values.length / r);
            }
            if (minimumInfo == 0 || actualInfo < minimumInfo) {
                minimumInfo = actualInfo;
            }
        }
        return minimumInfo;
    }
    
    @Override
    public double value(Collection<?> values) {
        if (values.size() <= 1) {
            return 0;
        }
        double minimumInfo = 0;
        double absoluteMax = 0;
        Set<?> atomicSet = new HashSet<>(values);
        int K = atomicSet.size();
        if (K == 1) {
            return FastLog.log2(values.size());
        }
        int N = values.size();
        double atomicInfo = shannonEntropy.value(values);
        absoluteMax = maxInformation(N, K, 1);

        for (int r = 1; r <= N / 2; r++) {
            List<List> parts = CollectionUtils.breakApart(values, r, false);
            double maxInfo = maxInformation(N, K, r);
            double actualInfo = shannonInfo.value(parts);
            actualInfo = maxInfo == 0 ? 0 : actualInfo / maxInfo * absoluteMax;
            if (actualInfo == 0) {
                actualInfo = atomicInfo * r + FastLog.log2(values.size() / r);
            }
            if (minimumInfo == 0 || actualInfo < minimumInfo) {
                minimumInfo = actualInfo;
            }
        }
        return minimumInfo;
    }

    private double maxInformation(int N, int K, int r) {
        int m = N / r;
        if (Math.pow(K, r) <= m) {
            return N * FastLog.log2(K);
        } else {
            return m * FastLog.log2(m);
        }
    }


}
