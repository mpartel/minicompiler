package minicompiler.ir;

public class IrGoto extends IrCommand {
    public final String labelName;

    public IrGoto(String labelName) {
        this.labelName = labelName;
    }
    
    @Override
    public String getAssignedVar() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IrGoto) {
            IrGoto that = (IrGoto)obj;
            return this.labelName.equals(that.labelName);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return labelName.hashCode();
    }

    @Override
    public String toString() {
        return "goto @" + labelName;
    }
}
