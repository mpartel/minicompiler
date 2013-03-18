package minicompiler.ast;

public class IntConst extends Node implements Expr {
    public final int value;

    public IntConst(int value) {
        this.value = value;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IntConst) {
            return this.value == ((IntConst)obj).value;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return ""+value;
    }
}
