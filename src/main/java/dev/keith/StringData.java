package dev.keith;


public record StringData(String value) implements IData<String> {
    @Override
    public String toString() {
        return value;
    }
}
