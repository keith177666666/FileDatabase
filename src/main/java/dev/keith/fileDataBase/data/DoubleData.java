package dev.keith.fileDataBase.data;

import dev.keith.IData;

/**
 * A data that's contain the double value;
 */
@SuppressWarnings("unused")
public class DoubleData extends AbstractData<Double> implements IData<Double> {
    /**
     * The default constructor
     * @param value value
     */
    public DoubleData(Double value) {
        super(value);
    }
}
