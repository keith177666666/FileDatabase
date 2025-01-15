package dev.keith.data;


import dev.keith.IData;

/**
 * A data that's contain the string value;
 */
public class StringData extends AbstractData<String> implements IData<String> {
    /**
     * The default constructor
     * @param value value
     */
    public StringData(String value) {
        super(value);
    }
}
