/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectiveethics.values.logic.method.information;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import objectiveethics.commons.collection.CollectionUtils;
import objectiveethics.commons.math.fast.FastLog;
import objectiveethics.values.structure.Information;
import objectiveethics.values.structure.BaseValue;
import objectiveethics.values.logic.method.util.InfoNormalizer;
import objectiveethics.values.logic.method.util.InfoUtils;

/**
 * Shannon Composition Minimum Information
 *
 * @author Volgyerdo Nonprofit Kft.
 */
@BaseValue(id = 14, category = "information", acronym = "ISCM", name = "SCM Information", description = "Shannon Composition Minimum information measure that finds the optimal "
        +
        "compositional breakdown of data to minimize Shannon information. Analyzes " +
        "data by breaking it into different sized components and selecting the " +
        "composition that yields the minimum information content, revealing " +
        "the most efficient structural organization.", algorithm = "1. Try different composition sizes (1, 2, 3, ... up to data length);\n"
                +
                "2. For each size, break data into chunks of that size;\n" +
                "3. Calculate Shannon information for each composition;\n" +
                "4. Find composition size that yields minimum Shannon information;\n" +
                "5. Return the minimum information value found", minEstimationSize = 500,
                article = "https://objectiveethics.com/values/docs/scm-information/")
public class SCMInfo implements Information {

    private final ShannonInfo shannon = new ShannonInfo();

    @Override
    public double value(byte[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
        double scmInfo = calculateScmInfo(CollectionUtils.convertByteArrayToList(values));
        double minScmInfo = calculateScmInfo(CollectionUtils.convertByteArrayToList(new byte[values.length]));
        double maxScmInfo = calculateScmInfo(
                CollectionUtils.convertByteArrayToList(InfoUtils.generateRandomByteArray(values)));

        return InfoNormalizer.normalizeInfo(scmInfo, minScmInfo, maxScmInfo, values);
    }

    @Override
    public double value(Collection<?> values) {
        if (values == null || values.size() <= 1) {
            return 0;
        }
        double scmInfo = calculateScmInfo(values);

        // Minimális információ: homogén kollekció (minden elem ugyanaz)
        Object firstElement = values.iterator().next();
        List<Object> minValues = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            minValues.add(firstElement);
        }
        double minScmInfo = calculateScmInfo(minValues);

        // Maximális információ: random kollekció az eredeti egyedi értékekkel
        Object[] randomArray = InfoUtils.generateRandomObjectArray(values);
        List<Object> maxValues = new ArrayList<>();
        for (Object obj : randomArray) {
            maxValues.add(obj);
        }
        double maxScmInfo = calculateScmInfo(maxValues);

        return InfoNormalizer.normalizeInfo(scmInfo, minScmInfo, maxScmInfo, values);
    }

    private double calculateScmInfo(Collection<?> values) {
        if (values == null || values.size() <= 1) {
            return 0;
        }
        Set<?> atomicSet = new HashSet<>(values);
        int K = atomicSet.size();
        if (K == 1) {
            return 0;
        }
        double minimumInfo = Double.POSITIVE_INFINITY;
        int N = values.size();
        double absoluteMax = maxInformation(N, K, 1);
        for (int r = 1; r <= N / 2; r++) {
            @SuppressWarnings("rawtypes")
            List parts = CollectionUtils.breakApart(values, r, false);
            double maxInfo = maxInformation(N, K, r);
            @SuppressWarnings({ "rawtypes", "unchecked" })
            Set range = new HashSet(parts);
            double actualInfo = 0;
            for (Object obj : range) {
                @SuppressWarnings("rawtypes")
                List element = (List) obj;
                actualInfo += shannon.value(element);
            }
            actualInfo += shannon.value(parts);
            actualInfo = maxInfo == 0 ? 0 : actualInfo / maxInfo * absoluteMax;
            if (actualInfo < minimumInfo) {
                minimumInfo = actualInfo;
            }
        }
        return minimumInfo;
    }

    private double maxInformation(int N, int K, int r) {
        int m = N / r;
        return m * (r * FastLog.log2(Math.min(r, K))
                + FastLog.log2(Math.min(m, Math.pow(K, r))));
    }

}
