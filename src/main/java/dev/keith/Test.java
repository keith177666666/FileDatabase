package dev.keith;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class Test extends AbstractFileDataBaseObserver<String, Test.Serializer> {
    public static void main(String[] args) {
        Test t = new Test();
        FileDataBase.initial(t);
        FileDataBase<String, Serializer> db =
                (FileDataBase<String, Serializer>)
                        DataBaseHelper.getInstance().getDefaultDataBase();
        db.clear();
        db.write(Pair.of("1", "oops"));
        System.out.println(db.read("1"));
    }
    private final Serializer serializer = new Serializer(this);
    @Override
    public Serializer getSerializer() {
        return serializer;
    }

    @Override
    public Function<String, StringData> getFactory() {
        return StringData::new;
    }
    public static class Serializer implements IDataBaseObserver.Serializer<String, StringData> {
        private final Test t;

        public Serializer(Test t) {
            this.t = t;
        }


        @Override
        @Nullable
        public StringData deserialize(String key, BufferedReader bufferedReader) {
            for (String line : bufferedReader.lines().toList()) {
                if (line.startsWith(key)) {
                    String toReturn = line.split(":")[1];
                    return t.getFactory().apply(toReturn);
                }
            }
            return t.getFactory().apply("null");
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
