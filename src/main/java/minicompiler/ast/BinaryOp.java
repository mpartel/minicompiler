package minicompiler.ast;

public class BinaryOp implements Expr {
    public final Expr left;
    public final String opName;
    public final Expr right;

    public BinaryOp(Expr left, String opName, Expr right) {
        this.left = left;
        this.opName = opName;
        this.right = right;
    }
    
    public void accept(AstVisitor v) {
        v.visit(this);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BinaryOp) {
            BinaryOp that = (BinaryOp)obj;
            return
                    this.getClass().equals(that.getClass()) &&
                    this.left.equals(that.left) &&
                    this.opName.equals(that.opName) &&
                    this.right.equals(that.right);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (left.hashCode() << 16) ^ right.hashCode();
    }

    @Override
    public String toString() {
        return "(" + left + " " + opName + " " + right + ")";
    }
}
