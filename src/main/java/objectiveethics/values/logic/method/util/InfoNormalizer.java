/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectiveethics.values.logic.method.util;

import objectiveethics.values.logic.method.information.MinInfo;
import objectiveethics.values.logic.method.information.MaxInfo;

/**
 * Utility class for normalizing information values to a specific range.
 * This class provides functionality to scale values from one range to another,
 * typically used to normalize information metrics between MinInfo and MaxInfo values.
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class InfoNormalizer {

    private static final MinInfo minInfo = new MinInfo();
    private static final MaxInfo maxInfo = new MaxInfo();

    /**
     * Scales a value from an original range to a new range.
     * 
     * @param value The value to be scaled
     * @param originalMin The minimum value of the original range
     * @param originalMax The maximum value of the original range
     * @param newMin The minimum value of the new range
     * @param newMax The maximum value of the new range
     * @return The scaled value within the new range
     */
    public static double scaleToRange(double value, double originalMin, double originalMax, double newMin, double newMax) {
        if (originalMax == originalMin) {
            // Ha az eredeti tartomány szélessége nulla, minden érték az új tartomány közepére kerül
            return (newMin + newMax) / 2;
        }
        return Math.min(newMax, newMin + ((value - originalMin)
            / (originalMax - originalMin))
            * (newMax - newMin));
    }

    /**
     * Normalizes an information value between MinInfo and MaxInfo values.
     * 
     * @param infoValue The information value to normalize
     * @param minInfoValue The minimum information value
     * @param maxInfoValue The maximum information value
     * @return The normalized information value between minInfoValue and maxInfoValue
     */
    public static double normalizeInfo(double infoValue, double minInfoValue, double maxInfoValue) {
        return scaleToRange(infoValue, 0, Double.MAX_VALUE, minInfoValue, maxInfoValue);
    }

    /**
     * Normalizes an information value with custom original range between MinInfo and MaxInfo values.
     * 
     * @param infoValue The information value to normalize
     * @param originalMin The minimum value of the original range
     * @param originalMax The maximum value of the original range
     * @param minInfoValue The minimum information value
     * @param maxInfoValue The maximum information value
     * @return The normalized information value between minInfoValue and maxInfoValue
     */
    public static double normalizeInfoWithRange(double infoValue, double originalMin, double originalMax, 
                                               double minInfoValue, double maxInfoValue) {
        return scaleToRange(infoValue, originalMin, originalMax, minInfoValue, maxInfoValue);
    }

    /**
     * Normalizes an information value between calculated MinInfo and MaxInfo values for byte array.
     * 
     * @param infoValue The information value to normalize
     * @param originalMin The minimum value of the original range
     * @param originalMax The maximum value of the original range
     * @param input The byte array to calculate MinInfo and MaxInfo for
     * @return The normalized information value between calculated MinInfo and MaxInfo
     */
    public static double normalizeInfo(double infoValue, double originalMin, double originalMax, byte[] input) {
        double min = minInfo.value(input);
        double max = maxInfo.value(input);
        return scaleToRange(infoValue, originalMin, originalMax, min, max);
    }

    /**
     * Normalizes an information value between calculated MinInfo and MaxInfo values for collection.
     * 
     * @param infoValue The information value to normalize
     * @param originalMin The minimum value of the original range
     * @param originalMax The maximum value of the original range
     * @param input The collection to calculate MinInfo and MaxInfo for
     * @return The normalized information value between calculated MinInfo and MaxInfo
     */
    public static double normalizeInfo(double infoValue, double originalMin, double originalMax, java.util.Collection<?> input) {
        double min = minInfo.value(input);
        double max = maxInfo.value(input);
        return scaleToRange(infoValue, originalMin, originalMax, min, max);
    }
}
