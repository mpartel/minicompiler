package minicompiler.ast;

public abstract class Node {
    public abstract void accept(AstVisitor v);
}
