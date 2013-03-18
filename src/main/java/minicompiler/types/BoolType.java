package minicompiler.types;

public class BoolType extends Type {
    public static BoolType instance = new BoolType();
    
    private BoolType() {
    }

    @Override
    public String toString() {
        return "bool";
    }
}
