package dev.keith.data;

import dev.keith.IData;


/**
 * An abstract template for custom initialing the IData.
 * @param <V> value
 */
@SuppressWarnings("unused")
public abstract class AbstractData<V> implements IData<V> {
    private V value;

    /**
     * The default constructor
     * @param value The default value.
     */
    public AbstractData(V value) {
        this.value = value;
    }

    /**
     * Set the value.
     * @param value value to be set
     */
    public void setValue(V value) {
        this.value = value;
    }

    /**
     * Get the value
     * @return value
     */
    public V value() {
        return value;
    }

    /**
     * Inherit this method to simplify not to get the value and print.
     * @return value.toString()
     */
    @Override
    public String toString() {
        return value.toString();
    }
}
