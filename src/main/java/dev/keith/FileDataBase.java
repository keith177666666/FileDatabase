package dev.keith;

import dev.keith.data.StringData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;

public class FileDataBase<K,
        S extends IDataBaseObserver.Serializer<K, StringData>>
        implements IDataBase<K, String, StringData> {
    private final AbstractFileDataBaseObserver<K, S> observer;
    private BufferedReader input = null;
    private BufferedWriter output = null;
    private static FileDataBase<?, ?> instance = null;

    private FileDataBase(AbstractFileDataBaseObserver<K, S> observer, File file) {
        if(instance == null) {
            instance = this;
            new DataBaseHelper(this, observer);
            this.observer = observer;
            try {
                this.input = new BufferedReader(new FileReader(file));
                this.output = new BufferedWriter(new FileWriter(file, true));
            } catch (FileNotFoundException e) {
                File DBfile = new File("./filedb.txt");
                try {
                    if (!DBfile.createNewFile()) {
                        throw new IllegalStateException("File cannot be either found or created.");
                    }
                    if (this.input == null) {
                        this.input = new BufferedReader(new FileReader("./filedb.txt"));
                    }
                    if (this.output == null) {
                        this.output = new BufferedWriter(new FileWriter("./filedb.txt"));
                    }
                } catch (IOException e1) {
                    throw new RuntimeException(e1);
                }
            } catch (IOException e1) {
                throw new RuntimeException(e1);
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
        return observer.getSerializer().serialize(Pair.of(pair.key(),
                observer.getFactory().apply(pair.value())), output);
    }
    public void clear() {
        try (PrintWriter printWriter = new PrintWriter("./filedb.txt")) {
            printWriter.print("");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static <K, S extends IDataBaseObserver.Serializer<K, StringData>>
    void initial(AbstractFileDataBaseObserver<K, S> observer, File file) {
        if (observer != null) {
            new FileDataBase<>(observer, file);
        }
    }
    public static <K, S extends IDataBaseObserver.Serializer<K, StringData>>
    void initial(AbstractFileDataBaseObserver<K, S> observer, String directory) {
        initial(observer, new File(directory));
    }
    public static <K, S extends IDataBaseObserver.Serializer<K, StringData>>
    void initial(AbstractFileDataBaseObserver<K, S> observer) {
        initial(observer, "./filedb.txt");
    }
}
