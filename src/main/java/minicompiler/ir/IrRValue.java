package minicompiler.ir;

public abstract class IrRValue {
    public abstract void accept(IrVisitor visitor);
}
