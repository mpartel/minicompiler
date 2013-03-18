package minicompiler.misc;

import java.util.HashMap;

/** Makes distinct temporary variable names like x1, x2, y1, ... */
public class NameMaker {
    private HashMap<String, Integer> prefixToNextNumber = new HashMap<String, Integer>();

    public String makeName(String prefix) {
        if (!prefixToNextNumber.containsKey(prefix)) {
            prefixToNextNumber.put(prefix, 1);
        }
        int n = prefixToNextNumber.get(prefix);
        prefixToNextNumber.put(prefix, n + 1);
        return prefix + n;
    }
}
