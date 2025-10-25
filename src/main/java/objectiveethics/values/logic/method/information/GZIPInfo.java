/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectiveethics.values.logic.method.information;

import java.util.Collection;
import objectiveethics.commons.object.ObjectUtils;
import objectiveethics.commons.primitive.ArrayUtils;
import objectiveethics.values.logic.method.util.InfoNormalizer;
import objectiveethics.values.logic.method.util.InfoUtils;
import objectiveethics.values.structure.Information;
import objectiveethics.values.structure.BaseValue;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
@BaseValue(id = 7, category = "information", acronym = "IGZIP", name = "GZIP Information", description = "Calculates information content using GZIP compression algorithm. Measures the "
        +
        "compressed size of data in bits, representing the minimum information needed " +
        "to reconstruct the original data. Effective for identifying patterns, repetitions, " +
        "and redundancy in datasets through real-world compression techniques.", algorithm = "1. Convert input data to byte array if needed;\n"
                +
                "2. Apply GZIP compression algorithm (LZ77 + Huffman coding);\n" +
                "3. Calculate compressed size in bits;\n" +
                "4. Generate random data baseline for normalization;\n" +
                "5. Generate minimum compression baseline (all zeros);\n" +
                "6. Normalize result between min and max compression ratios", article = "https://objectiveethics.com/values/docs/gzip-information/")
public class GZIPInfo implements Information {

    @Override
    public double value(byte[] values) {
        if (values == null || values.length < 1) {
            return 0;
        }

        if (values.length == 1) {
            return 1;
        }
        double gzipInfo = ArrayUtils.toGZIP(values).length * 8;
        double minGzipInfo = ArrayUtils.toGZIP(new byte[values.length]).length * 8;
        double maxGzipInfo = ArrayUtils.toGZIP(InfoUtils.generateRandomByteArray(values)).length * 8;

        return InfoNormalizer.normalizeInfo(gzipInfo, minGzipInfo, maxGzipInfo, values);
    }

    @Override
    public double value(Collection<?> input) {
        if (input == null || input.size() < 1) {
            return 0;
        }

        if (input.size() == 1) {
            return 1;
        }
        byte[] values = ObjectUtils.serialize(input);
        double gzipInfo = ArrayUtils.toGZIP(values).length * 8;
        double minGzipInfo = ArrayUtils.toGZIP(new byte[values.length]).length * 8;
        double maxGzipInfo = ArrayUtils.toGZIP(InfoUtils.generateRandomByteArray(values)).length * 8;

        return InfoNormalizer.normalizeInfo(gzipInfo, minGzipInfo, maxGzipInfo, input);
    }

}
