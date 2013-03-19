package minicompiler.types;

import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Set;

/**
 * A map of variable names to types.
 * 
 * The map's state can be pushed on a stack.
 * When type checking enters a subscope, such as the body of a while-loop,
 * it pushes the current symbol table on the stack. This way any
 * new variables introduced inside the while loop are forgotten when
 * the previous symbol table is popped back at the end of the loop.
 */
public class TypeSymTab extends AbstractMap<String, Type> {

    private Deque<HashMap<String, Type>> stack = new ArrayDeque<HashMap<String, Type>>();
    
    public TypeSymTab() {
        stack.push(new HashMap<String, Type>());
    }
    
    // This is not the most efficient implementation, but it's simple.
    
    public void pushState() {
        HashMap<String, Type> newTable = new HashMap<String, Type>(stack.peek().size());
        newTable.putAll(stack.peek());
        stack.push(newTable);
    }
    
    public void popState() {
        if (stack.size() == 1) {
            throw new IllegalStateException("TypeSymTab: cannot pop last symbol table");
        }
        stack.pop();
    }
    
    @Override
    public Set<Entry<String, Type>> entrySet() {
        return stack.peek().entrySet();
    }

    @Override
    public Type put(String key, Type value) {
        return stack.peek().put(key, value);
    }
}
