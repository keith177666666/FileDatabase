package dev.keith;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;

@SuppressWarnings({"unchecked", "ignored"})
public class FileDataBase<K,
        S extends IDataBaseObserver.Serializer<K, StringData>>
        implements IDataBase<K, String, StringData> {
    private final AbstractFileDataBaseObserver<K, S> observer;
    private final BufferedReader input;
    private final BufferedWriter output;
    private static FileDataBase<?, ?> instance = null;

    public FileDataBase(AbstractFileDataBaseObserver<?, ?> observer) {
        if(instance == null) {
            instance = this;
            new DataBaseHelper(this, observer);
            this.observer = (AbstractFileDataBaseObserver<K, S>) observer;
            File DBfile = new File("./filedb.txt");
            if (!DBfile.exists()) {
                try {
                    DBfile.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                this.input = new BufferedReader(new FileReader(DBfile));
                this.output = new BufferedWriter(new FileWriter(DBfile));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalStateException("The database has be initialed, please use DataBaseHelper to get the instance.");
        }
    }

    @Nullable
    @Override
    public StringData read(K key) {
        return observer.getSerializer().deserialize(key, input);
    }

    @Override
    @NotNull
    public ResultType write(Pair<K, String> pair) {
        return observer.getSerializer().serialize(new Pair<>(pair.key(),
                observer.getFactory().apply(pair.value())), output);
    }
    public static <K, S extends IDataBaseObserver.Serializer<K, StringData>>
    void initial(AbstractFileDataBaseObserver<?, ?> observer) {
        if (observer != null) {
            new FileDataBase<K, S>(observer);
        }
    }
}
