package minicompiler.ast;

import com.google.common.base.Joiner;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Block extends Node implements Statement {
    public final List<Statement> statements;

    public Block(List<Statement> statements) {
        this.statements = Collections.unmodifiableList(statements);
    }
    
    public Block(Statement... statements) {
        this(Arrays.asList(statements));
    }
    
    public void accept(AstVisitor v) {
        v.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Block) {
            Block that = (Block)obj;
            return this.statements.equals(that.statements);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return statements.hashCode();
    }

    @Override
    public String toString() {
        return "{ " + Joiner.on("; ").join(statements) + " }";
    }
}
