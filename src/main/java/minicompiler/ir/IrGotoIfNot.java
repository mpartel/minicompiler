package minicompiler.ir;

public class IrGotoIfNot extends IrCommand {
    public final String labelName;
    public final IrRValue condition;

    public IrGotoIfNot(String labelName, IrRValue condition) {
        this.labelName = labelName;
        this.condition = condition;
    }
    
    @Override
    public void accept(IrVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public String getAssignedVar() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IrGotoIfNot) {
            IrGotoIfNot that = (IrGotoIfNot)obj;
            return
                    this.labelName.equals(that.labelName) &&
                    this.condition.equals(that.condition);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (labelName.hashCode() << 16) | condition.hashCode();
    }

    @Override
    public String toString() {
        return "goto @" + labelName + " if not " + condition;
    }
}
