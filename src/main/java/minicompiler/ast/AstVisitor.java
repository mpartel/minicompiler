package minicompiler.ast;

public abstract class AstVisitor {
    public void visit(Block block) {}
    public void visit(Declaration decl) {}
    public void visit(Assignment assignment) {}
    public void visit(EmptyStatement empty) {}
    
    public void visit(IfStatement ifStmt) {}
    public void visit(WhileLoop whileLoop) {}
    
    public void visit(BinaryOp binop) {}
    public void visit(UnaryOp unop) {}
    public void visit(FunctionCall call) {}
    public void visit(BoolConst boolConst) {}
    public void visit(IntConst intConst) {}
    public void visit(Var var) {}
}
