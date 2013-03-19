package minicompiler.misc;

import java.io.IOException;
import java.io.Reader;

public class StreamUtils {
    public static String readAll(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[16384];
        while (true) {
            int amtRead = reader.read(buf);
            if (amtRead == -1) {
                break;
            }
            sb.append(buf, 0, amtRead);
        }
        return sb.toString();
    }
}
