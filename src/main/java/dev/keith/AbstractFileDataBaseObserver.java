package dev.keith;

public abstract class AbstractFileDataBaseObserver<K,
        S extends IDataBaseObserver.Serializer<K, StringData>>
        implements IDataBaseObserver<K, String, StringData, S> {
}
