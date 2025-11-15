package objectiveethics.values.logic.method;

import objectiveethics.values.structure.Value;

import java.util.Collection;

/**
 * EstimatedValue - egyszerű wrapper a delegate Value köré.
 * Egyszerűen továbbítja az összes hívást a delegált Value objektumnak.
 */
public final class EstimatedValue implements Value {

    private final Value delegate;

    public EstimatedValue(Value delegate) {
        if (delegate == null)
            throw new IllegalArgumentException("delegate cannot be null");
        this.delegate = delegate;
    }

    @Override
    public double value(byte[] bytes) {
        return delegate.value(bytes);
    }

    @Override
    public double value(Collection<?> values) {
        return delegate.value(values);
    }

    public Value getBaseValue() {
        return delegate;
    }

}