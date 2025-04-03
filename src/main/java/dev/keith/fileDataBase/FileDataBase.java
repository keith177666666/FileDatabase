package dev.keith.fileDataBase;

import dev.keith.*;
import dev.keith.fileDataBase.data.StringData;
import dev.keith.event.DataBaseManger;
import dev.keith.event.Parameters;
import dev.keith.event.Proxy;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.*;
import java.util.*;

/**
 * A DataBase based on a file.
 * @param <K> key
 * @param <S> serializer
 */
@SuppressWarnings("unchecked")
public class FileDataBase<K,
        S extends IDataBaseObserver.Serializer<K, StringData>>
        implements IDataBase<K, String, StringData> {
    static {
        ServiceLoader<AbstractFileDataBaseObserver> loader =
                ServiceLoader.load(AbstractFileDataBaseObserver.class);
        AbstractFileDataBaseObserver<?, ?> observer = loader.stream()
                .toList()
                .getFirst()
                .get();
        FileDataBase.initial(observer);
    }
    private final AbstractFileDataBaseObserver<K, S> observer;
    private BufferedReader input = null;
    private BufferedWriter output = null;
    private static FileDataBase<?, ?> instance = null;
    private final File file;
    private final List<Proxy> proxyList = new ArrayList<>();
    private final DataBaseManger<K, String, StringData, FileDataBase<K, S>> manger;

    private FileDataBase(AbstractFileDataBaseObserver<K, S> observer, File file) {
        if(instance == null) {
            instance = this;
            new DataBaseHelper(this, observer);
            this.file = file;
            this.observer = observer;
            try {
                this.input = new BufferedReader(new FileReader(file));
                this.output = new BufferedWriter(new FileWriter(file, true));
            } catch (FileNotFoundException e) {
                try {
                    if (!file.createNewFile()) {
                        throw new IllegalStateException("File cannot be either found or created.");
                    }
                    if (this.input == null) {
                        this.input = new BufferedReader(new FileReader(file));
                    }
                    if (this.output == null) {
                        this.output = new BufferedWriter(new FileWriter(file));
                    }
                } catch (IOException e1) {
                    throw new RuntimeException(e1);
                }
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
            manger = new DataBaseManger<>(this);
        } else {
            throw new IllegalStateException("The database has be initialed, please use DataBaseHelper to get the instance.");
        }
    }

    /**
     * read the database and get the specific value by key
     * @param key key
     * @return value
     */
    @Nullable
    @Override
    public StringData read(K key) {
        proxyList.forEach(proxy -> proxy.callOnMethod(
                new Event("The Data Base has been read, key: " + key, Event.Type.READ,
                        new Parameters(input, output, this))));
        return observer.getSerializer().deserialize(key, input);
    }

    /**
     * read all the value in the database
     * @return List of the value
     */
    @NotNull
    @Override
    @Unmodifiable
    public List<StringData> readAll() {
        proxyList.forEach(proxy -> proxy.callOnMethod(
                new Event("The Data Base has been read", Event.Type.READ,
                        new Parameters(input, output, this))));
        List<StringData> l = new ArrayList<>();
        ((AbstractFileDataBaseObserver.Serializer<K, ?, ?> ) observer.getSerializer())
                .readAll(input).forEach(entry ->
                        l.add(observer.getFactory().apply(entry.getValue())));
        return l;
    }

    /**
     * read all the key & value in the database
     * @return set of the key and value
     */
    @NotNull
    @Unmodifiable
    public Set<Map.Entry<K, String>> readAll(Set<Map.Entry<K, String>> set) {
        proxyList.forEach(proxy -> proxy.callOnMethod(
                new Event("The Data Base has been read", Event.Type.READ,
                        new Parameters(input, output, this))));
        Set<Map.Entry<K, String>> l = new LinkedHashSet<>();
        return ((AbstractFileDataBaseObserver.Serializer<K, ?, ?> ) observer.getSerializer()).readAll(input);
    }

    /**
     * Write the database.
     * @param pair the key and the value
     * @return a Result Type shows that is it successful or not.
     */
    @Override
    @NotNull
    public ResultType write(Pair<K, String> pair) {
        proxyList.forEach(proxy -> proxy.callOnMethod(
                new Event("The database has been write, key: " + pair.key() +
                " value: " + pair.value(), Event.Type.DELETE,
                new Parameters(input, output, this))));
        if (observer.getSerializer().deserialize(pair.key(), input) != null) {
            observer.getSerializer().remove(pair.key(), input, output);
        }

        return observer.getSerializer().serialize(Pair.of(pair.key(),
                observer.getFactory().apply(pair.value())), output);
    }

    /**
     * Add a proxy.
     * This should be handled by the Database manager
     * @param proxy proxy
     */
    @Override
    @ApiStatus.Internal
    public void addProxy(Proxy proxy) {
        proxyList.add(proxy);
    }

    /**
     * Get the manager for this database.
     * @return Database Manager
     * @param <THIS> This
     */
    @Override
    public <THIS extends IDataBase<K, String, StringData>> DataBaseManger<K, String, StringData, THIS>
    getManger() {
        return (DataBaseManger<K, String, StringData, THIS>) manger;
    }

    /**
     * Clear the Database.
     */
    public void clear() {
        proxyList.forEach(proxy -> proxy.callOnMethod(
                new Event("The Data Base has been cleared.", Event.Type.CLEAR, new Parameters(input, output, this))));
        try (PrintWriter printWriter = new PrintWriter(file)) {
            printWriter.print("");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * remove the specific key and value
     * @param key key
     * @return a Result Type shows that is it successful or not.
     */
    @Override
    public ResultType remove(K key) {
        proxyList.forEach(proxy -> proxy.callOnMethod(
                new Event("The Data of Data Base has been deleted, key: " + key,
                        Event.Type.DELETE, new Parameters(input, output, this))
        ));
        return observer.getSerializer().remove(key, input, output);
    }

    /**
     * initial the Database
     * @param observer observer
     * @param file database file
     * @param <K> key
     * @param <S> serializer
     */
    public static <K, S extends IDataBaseObserver.Serializer<K, StringData>>
    void initial(AbstractFileDataBaseObserver<K, S> observer, File file) {
        if (observer != null) {
            new FileDataBase<>(observer, file);
        }
    }

    /**
     * initial the Database
     * @param observer observer
     * @param directory database file's directory
     * @param <K> key
     * @param <S> serializer
     */
    public static <K, S extends IDataBaseObserver.Serializer<K, StringData>>
    void initial(AbstractFileDataBaseObserver<K, S> observer, String directory) {
        initial(observer, new File(directory));
    }

    /**
     * initial the Database
     * @param observer observer
     * @param <K> key
     * @param <S> serializer
     */
    public static <K, S extends IDataBaseObserver.Serializer<K, StringData>>
    void initial(AbstractFileDataBaseObserver<K, S> observer) {
        initial(observer, "./filedb.txt");
    }

    /**
     * Get the current file for a database
     * @param db database
     * @return current file of a database
     */
    public static File currentFile(FileDataBase<?, ?> db) {
        return db.file;
    }

    /**
     * To auto initial the database and use this class.
     */
    public static class Provider implements IDataBaseProvider<FileDataBase> {
        /**
         * Default Constructor.
         */
        public Provider() {}

        /**
         * Internal method
         * @return FileDataBase.class
         */
        @Override
        @ApiStatus.Internal
        public Class<FileDataBase> provide() {
            return FileDataBase.class;
        }
    }
}
