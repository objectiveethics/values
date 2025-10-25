/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectiveethics.values.structure;

/**
 * Marker interface for information-based value measurements.
 * Information measures quantify the amount of information content in a dataset.
 * These measurements can be raw information content or normalized between
 * minimum and maximum theoretical values.
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public interface Information extends Value {
    
    /**
     * Indicates whether this information measure is normalized between 
     * minimum and maximum theoretical information values.
     * 
     * @return true if the measure is normalized, false if it returns raw information content
     */
    default boolean isNormalized() {
        return false;
    }
    
    /**
     * Indicates whether this information measure uses compression-based techniques.
     * 
     * @return true if the measure is based on compression algorithms
     */
    default boolean isCompressionBased() {
        return false;
    }
}
