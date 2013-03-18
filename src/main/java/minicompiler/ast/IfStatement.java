package minicompiler.ast;

public class IfStatement implements Statement {
    public final Expr condition;
    public final Statement thenClause;
    public final Statement elseClause;

    public IfStatement(Expr condition, Statement thenClause, Statement elseClause) {
        this.condition = condition;
        this.thenClause = thenClause;
        this.elseClause = elseClause;
    }
    
    public IfStatement(Expr condition, Statement thenClause) {
        this.condition = condition;
        this.thenClause = thenClause;
        this.elseClause = new EmptyStatement();
    }
    
    public void accept(AstVisitor v) {
        v.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IfStatement) {
            IfStatement that = (IfStatement)obj;
            return
                    this.condition.equals(that.condition) &&
                    this.thenClause.equals(that.thenClause) &&
                    this.elseClause.equals(that.elseClause);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (condition.hashCode() << 16) | (thenClause.hashCode() ^ elseClause.hashCode());
    }

    @Override
    public String toString() {
        if (elseClause instanceof EmptyStatement) {
            return "if (" + condition + ") " + thenClause;
        } else {
            return "if (" + condition + ") " + thenClause + " else " + elseClause;
        }
    }
}
