package minicompiler.types;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StdlibTypes {
    private static final HashMap<String, Type> types = new HashMap<String, Type>();
    
    static {
        for (String op : new String[] { "+", "-", "*", "/", "%" }) {
            Type ty = new FunctionType(tyList(IntType.instance, IntType.instance), IntType.instance);
            types.put(op, ty);
        }
        
        for (String op : new String[] { "<", ">", "<=", ">=", "==", "!=" }) {
            Type ty = new FunctionType(tyList(IntType.instance, IntType.instance), BoolType.instance);
            types.put(op, ty);
        }
        
        types.put("!", new FunctionType(tyList(BoolType.instance), BoolType.instance));
        
        types.put("printInt", new FunctionType(tyList(IntType.instance), VoidType.instance));
        types.put("readInt", new FunctionType(tyList(), IntType.instance));
    }
    
    private static List<Type> tyList(Type... types) {
        return Arrays.asList(types);
    }

    public static Map<String, Type> getTypes() {
        return Collections.unmodifiableMap(types);
    }
}
