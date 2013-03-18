package minicompiler.ast;

public class Var extends Node implements Expr {
    public final String name;

    public Var(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Var) {
            return this.name.equals(((Var)obj).name);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
