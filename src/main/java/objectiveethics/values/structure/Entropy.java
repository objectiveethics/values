/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectiveethics.values.structure;

/**
 * Marker interface for entropy-based value measurements.
 * Entropy measures quantify the randomness or disorder in a dataset.
 * These measurements typically return values between 0 (completely ordered) 
 * and log2(n) (maximum disorder) where n is the number of unique symbols.
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public interface Entropy extends Value {
    
    /**
     * Indicates whether this entropy measure uses natural logarithm (ln) or log base 2.
     * 
     * @return true if using natural logarithm, false if using log base 2 (default)
     */
    default boolean usesNaturalLogarithm() {
        return false;
    }
}
