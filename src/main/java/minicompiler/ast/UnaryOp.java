package minicompiler.ast;

public class UnaryOp extends Node implements Expr {
    public final String opName;
    public final Expr operand;

    public UnaryOp(String opName, Expr operand) {
        this.opName = opName;
        this.operand = operand;
    }
    
    public void accept(AstVisitor v) {
        v.visit(this);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UnaryOp) {
            UnaryOp that = (UnaryOp)obj;
            return
                    this.getClass().equals(that.getClass()) &&
                    this.operand.equals(that.operand);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return operand.hashCode();
    }

    @Override
    public String toString() {
        return "(" + opName + operand + ")";
    }
}
