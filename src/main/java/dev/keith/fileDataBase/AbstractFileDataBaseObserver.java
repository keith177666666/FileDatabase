package dev.keith.fileDataBase;

import dev.keith.IDataBaseObserver;
import dev.keith.fileDataBase.data.StringData;


/**
 * An abstract template for custom initialing the observer.
 * @param <K> key
 * @param <S> serializer
 */
public abstract class AbstractFileDataBaseObserver<K,
        S extends IDataBaseObserver.Serializer<K, StringData>>
        implements IDataBaseObserver<K, String, StringData, S> {
    /**
     * The default Constructor
     */
    protected AbstractFileDataBaseObserver() {}
    /**
     * An abstract template for custom initialing the serializer.
     * @param <K> key same as the observer
     * @param <THIS> the inherited class
     * @param <O> the observer
     */
    protected abstract static class Serializer<K,
            THIS extends IDataBaseObserver.Serializer<K, StringData>,
            O extends AbstractFileDataBaseObserver<K, THIS>>
            implements IDataBaseObserver.Serializer<K, StringData> {
        /**
         * The observer to use,
         */
        protected final O observer;

        /**
         * The default Constructor
         * @param observer observer
         */
        protected Serializer(O observer) {
            this.observer = observer;
        }
    }
}
