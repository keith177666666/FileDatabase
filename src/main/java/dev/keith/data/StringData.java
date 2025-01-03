package dev.keith.data;


import dev.keith.IData;

public class StringData extends AbstractData<String> implements IData<String> {
    public StringData(String value) {
        super(value);
    }
}
