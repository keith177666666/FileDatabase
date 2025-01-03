package dev.keith;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public final class Example extends AbstractFileDataBaseObserver<String, Example.ExampleSerializer> {
    public static void main(String[] args) {
        Example instance = new Example();
        FileDataBase.initial(instance);
        FileDataBase<String, ExampleSerializer> db =
                (FileDataBase<String, ExampleSerializer>)
                        DataBaseHelper.getInstance().getDefaultDataBase();
        db.clear();
        db.write(Pair.of("1", "oops"));
        System.out.println(db.read("1"));
    }
    private final ExampleSerializer serializer = new ExampleSerializer(this);
    @Override
    public ExampleSerializer getSerializer() {
        return serializer;
    }

    @Override
    public Function<String, StringData> getFactory() {
        return StringData::new;
    }
    public static class ExampleSerializer implements IDataBaseObserver.Serializer<String, StringData> {
        private final Example observer;

        public ExampleSerializer(Example observer) {
            this.observer = observer;
        }


        @Override
        @Nullable
        public StringData deserialize(String key, BufferedReader bufferedReader) {
            for (String line : bufferedReader.lines().toList()) {
                if (line.startsWith(key)) {
                    String toReturn = line.split(":")[1];
                    return observer.getFactory().apply(toReturn);
                }
            }
            return observer.getFactory().apply("null");
        }
        @NotNull
        @Override
        public ResultType serialize(Pair<String, StringData> pair, BufferedWriter bufferedWriter) {
            String key = pair.key();
            StringData value = pair.value();
            try {
                bufferedWriter.newLine();
                bufferedWriter.write(key);
                bufferedWriter.write(':');
                bufferedWriter.write(value.value());
                bufferedWriter.flush();
            } catch (IOException e) {
                return ResultType.EXCEPTION;
            }
            return ResultType.SUCCESS;
        }
    }
}
