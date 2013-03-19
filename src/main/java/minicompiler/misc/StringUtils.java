package minicompiler.misc;

import java.util.Iterator;

public class StringUtils {
    public static <T> String join(Iterable<T> stuff, String sep) {
        StringBuilder sb = new StringBuilder();
        Iterator<T> i = stuff.iterator();
        while (i.hasNext()) {
            sb.append(i.next().toString());
            if (i.hasNext()) {
                sb.append(sep);
            }
        }
        return sb.toString();
    }
}
