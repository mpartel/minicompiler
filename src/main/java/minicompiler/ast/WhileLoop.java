package minicompiler.ast;

public class WhileLoop implements Statement {
    public final Expr head;
    public final Statement body;

    public WhileLoop(Expr head, Statement body) {
        this.head = head;
        this.body = body;
    }
    
    public void accept(AstVisitor v) {
        v.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WhileLoop) {
            WhileLoop that = (WhileLoop)obj;
            return
                    this.head.equals(that.head) &&
                    this.body.equals(that.body);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (head.hashCode() << 16) | body.hashCode();
    }

    @Override
    public String toString() {
        return "while " + head + " do " + body;
    }
}
