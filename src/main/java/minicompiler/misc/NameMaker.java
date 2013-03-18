package minicompiler.misc;

import java.util.HashMap;

/** Makes distinct temporary variable names like x1, x2, y1, ... */
public class NameMaker {
    private HashMap<String, Integer> prefixToNextNumber = new HashMap<String, Integer>();

    public String makeName(String prefix) {
        if (!prefixToNextNumber.containsKey(prefix)) {
            prefixToNextNumber.put(prefix, 1);
        }
        return prefix + prefixToNextNumber.get(prefix);
    }
}
