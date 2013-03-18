package minicompiler.ir;

public class IrIntConst extends IrRValue {
    public final int value;

    public IrIntConst(int value) {
        this.value = value;
    }

    @Override
    public void accept(IrVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IrIntConst) {
            IrIntConst that = (IrIntConst)obj;
            return this.value == that.value;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.value;
    }

    @Override
    public String toString() {
        return "" + value;
    }
}
