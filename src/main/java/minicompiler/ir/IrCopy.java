package minicompiler.ir;

public class IrCopy extends IrCommand {
    public final String assignedVar;
    public final IrRValue rvalue;

    public IrCopy(String assignedVar, IrRValue rvalue) {
        this.assignedVar = assignedVar;
        this.rvalue = rvalue;
    }
    
    @Override
    public void accept(IrVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getAssignedVar() {
        return assignedVar;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IrCopy) {
            IrCopy that = (IrCopy)obj;
            return
                    this.assignedVar.equals(that.assignedVar) &&
                    this.rvalue.equals(that.rvalue);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (assignedVar.hashCode() << 16) | rvalue.hashCode();
    }

    @Override
    public String toString() {
        return assignedVar + " := " + rvalue;
    }
}
