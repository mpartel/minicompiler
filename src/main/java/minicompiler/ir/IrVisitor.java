package minicompiler.ir;

public abstract class IrVisitor {
    public void visit(IrCall call) {}
    public void visit(IrCopy copy) {}
    
    public void visit(IrLabel g) {}
    
    public void visit(IrGoto g) {}
    public void visit(IrGotoIf g) {}
    public void visit(IrGotoIfNot g) {}
}
