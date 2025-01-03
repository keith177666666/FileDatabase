package dev.keith.data;

import dev.keith.IData;

public class IntData extends AbstractData<Integer> implements IData<Integer> {
    public IntData(Integer value) {
        super(value);
    }
}
