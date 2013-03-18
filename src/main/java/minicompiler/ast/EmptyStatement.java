package minicompiler.ast;

public class EmptyStatement extends Node implements Statement {

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof EmptyStatement);
    }
    
    @Override
    public int hashCode() {
        return 2098345;
    }

    @Override
    public String toString() {
        return "{}";
    }
}
