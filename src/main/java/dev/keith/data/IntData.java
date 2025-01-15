package dev.keith.data;

import dev.keith.IData;

/**
 * A data that's contain the integer value;
 */
@SuppressWarnings("unused")
public class IntData extends AbstractData<Integer> implements IData<Integer> {
    /**
     * The default constructor
     * @param value value
     */
    public IntData(Integer value) {
        super(value);
    }
}
