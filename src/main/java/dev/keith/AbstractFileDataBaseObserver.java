package dev.keith;

import dev.keith.data.StringData;

public abstract class AbstractFileDataBaseObserver<K,
        S extends IDataBaseObserver.Serializer<K, StringData>>
        implements IDataBaseObserver<K, String, StringData, S> {
    protected abstract static class Serializer<K,
            THIS extends IDataBaseObserver.Serializer<K, StringData>,
            O extends AbstractFileDataBaseObserver<K, THIS>>
            implements IDataBaseObserver.Serializer<K, StringData> {
        protected final O observer;
        protected Serializer(O observer) {
            this.observer = observer;
        }
    }
}
