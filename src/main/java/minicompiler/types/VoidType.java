package minicompiler.types;

public class VoidType extends Type {
    public static VoidType instance = new VoidType();
    
    private VoidType() {
    }

    @Override
    public String toString() {
        return "void";
    }
}
