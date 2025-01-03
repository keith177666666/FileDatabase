package dev.keith.data;

import dev.keith.IData;

public abstract class AbstractData<V> implements IData<V> {
    private V value;

    public AbstractData(V value) {
        this.value = value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public V value() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
