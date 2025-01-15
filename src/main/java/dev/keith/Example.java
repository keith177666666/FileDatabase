package dev.keith;

import dev.keith.data.StringData;
import dev.keith.event.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.function.Function;

/**
 * An example class for reference.
 */
@SuppressWarnings({"unchecked"})
public final class Example extends AbstractFileDataBaseObserver<Integer, Example.Serializer> {
    /**
     * Default main method to test
     * @param args args
     */
    public static void main(String[] args) {
        // initial the observer
        Example instance = new Example();
        // create database
        FileDataBase.initial(instance);
        // get the database through the DataBaseHelper
        FileDataBase<Integer, Serializer> db =
                (FileDataBase<Integer, Serializer>)
                        DataBaseHelper.getInstance().getDefaultDataBase();
        // WRONG
        //      \/
        //      /\
        // db.addProxy(null);
        // add proxy
        // CORRECT
        //        /
        //      \/
        db.getManger().addProxyTo(event -> {
            // check to see whether it is an "event" or not
            if (!(event instanceof Event)) {
                // return;
                Permission.CUSTOM.setAction(parameters -> Permission.CUSTOM.setAction(Permission.ALLOWED.getAction()));
                return Permission.CUSTOM;
            }
            switch (((Event) event).type()) {
                case WRITE -> System.out.println("message received by write method: " + event.message());
                case READ -> System.out.println("message received by read method: " + event.message());
                case CLEAR -> System.out.println("message received by clear method: " + event.message());
                case DELETE -> System.out.println("message received by delete method: " + event.message());
            }
            return Permission.ALLOWED;
        });
        // Example for writing into the database
        db.write(Pair.of(1, "GUYS"));
    }

    /**
     * The default Constructor
     */
    public Example() {}
    // initial the serializer
    private final Serializer serializer = new Serializer(this);

    /**
     * Get the Serializer
     * @return the serializer
     */
    @Override
    public Serializer getSerializer() {
        return serializer;
    }

    /**
     * Get the factory to create a "data"
     * @return factory
     */
    @Override
    public Function<String, StringData> getFactory() {
        return StringData::new;
    }

    /**
     * The example Serializer for reference.
     */
    public static class Serializer extends
            AbstractFileDataBaseObserver.Serializer<Integer, Serializer, Example>
            implements IDataBaseObserver.Serializer<Integer, StringData> {
        private FileDataBase<Integer, Serializer> db = null;

        /**
         * The default constructor.
         * @param observer observer (class example instance)
         */
        public Serializer(Example observer) {
            super(observer);
        }

        /**
         * Set the db if it has not been initialed.
         * @param db db
         */
        public void setDataBase(FileDataBase<Integer, Serializer> db) {
            if (this.db == null) {
                this.db = db;
            }
        }
        // VERY IMPORTANT
        // Automatic initial the db if not initialed and start doing action
        private void initialDataBase() {
            setDataBase((FileDataBase<Integer, Serializer>)
                    DataBaseHelper.getInstance().getDefaultDataBase());
        }

        /**
         * To deserialize and read the database;
         * @param key key
         * @param bufferedReader reader
         * @return a String Data (might be null!)
         */
        @Override
        @Nullable
        public StringData deserialize(Integer key, BufferedReader bufferedReader) {
            initialDataBase();
            for (String line : bufferedReader.lines().toList()) {
                if (line.startsWith(key.toString())) {
                    String toReturn = line.split(":")[1];
                    return observer.getFactory().apply(toReturn);
                }
            }
            return null;
        }

        /**
         * To serialize and write in to the database;
         * @param pair the key and the value
         * @param bufferedWriter writer
         * @return a Result Type shows that is it successful or not.
         */
        @NotNull
        @Override
        public ResultType serialize(Pair<Integer, StringData> pair, BufferedWriter bufferedWriter) {
            initialDataBase();
            Integer key = pair.key();
            StringData value = pair.value();
            try {
                bufferedWriter.newLine();
                bufferedWriter.write(key.toString());
                bufferedWriter.write(':');
                bufferedWriter.write(value.value());
                bufferedWriter.flush();
            } catch (IOException e) {
                return ResultType.EXCEPTION;
            }
            return ResultType.SUCCESS;
        }

        /**
         * Remove the specific key and value.
         * @param integer key
         * @param bufferedReader reader
         * @param bufferedWriter writer
         * @return a Result Type shows that is it successful or not.
         */
        @Override
        public ResultType remove(Integer integer, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
            initialDataBase();
            File file = new File(
                    FileDataBase.currentFile(db)
                            .getAbsolutePath()
                            .replace(".txt", "_temp.txt"));
            try {
                if (!file.createNewFile()) {
                    return ResultType.FAIL;
                }
                BufferedReader reader = new BufferedReader(new FileReader(file));
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                bufferedReader.lines() //Stream<String>
                        .filter(s -> !s.startsWith(integer.toString())) // Stream<String>
                        .forEach(s -> {
                            try {
                                writer.write(s);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }); // Void
                reader.lines()
                        .forEach(s -> {
                            try {
                                bufferedWriter.write(s);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                if (!file.delete()) {
                    return ResultType.EXCEPTION;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return ResultType.SUCCESS;
        }
    }
}
