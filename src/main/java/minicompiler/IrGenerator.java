package minicompiler;

import java.util.ArrayList;
import java.util.List;
import minicompiler.ast.*;
import minicompiler.ir.*;
import minicompiler.misc.NameMaker;

public class IrGenerator extends AstVisitor {
    private List<IrCommand> output = new ArrayList<IrCommand>();
    private NameMaker nameMaker = new NameMaker();
    private IrRValue lastRValue;

    public List<IrCommand> getOutput() {
        return output;
    }

    @Override
    public void visit(Block block) {
        for (Statement s : block.statements) {
            s.accept(this);
        }
    }

    @Override
    public void visit(Declaration decl) {
        decl.expr.accept(this);
        emit(new IrCopy(decl.varName, lastRValue));
    }

    @Override
    public void visit(Assignment assignment) {
        assignment.expr.accept(this);
        emit(new IrCopy(assignment.varName, lastRValue));
    }

    @Override
    public void visit(IfStatement ifStmt) {
        String elseLabel = nameMaker.makeName("else");
        String ifEndLabel = nameMaker.makeName("ifEnd");
        
        ifStmt.condition.accept(this);
        if (ifStmt.hasElseClause()) {
            emit(new IrGotoIfNot(elseLabel, lastRValue));
        } else {
            emit(new IrGotoIfNot(ifEndLabel, lastRValue));
        }
        
        ifStmt.thenClause.accept(this);
        if (ifStmt.hasElseClause()) {
            emit(new IrGoto(ifEndLabel));
            
            emit(new IrLabel(elseLabel));
            ifStmt.elseClause.accept(this);
        }
        
        emit(new IrLabel(ifEndLabel));
    }

    @Override
    public void visit(WhileLoop whileLoop) {
        String whileHeadLabel = nameMaker.makeName("whileHead");
        String whileEndLabel = nameMaker.makeName("whileEnd");
        
        emit(new IrLabel(whileHeadLabel));
        
        whileLoop.head.accept(this);
        emit(new IrGotoIfNot(whileEndLabel, lastRValue));
        
        whileLoop.body.accept(this);
        emit(new IrGoto(whileHeadLabel));
        
        emit(new IrLabel(whileEndLabel));
    }

    @Override
    public void visit(BinaryOp binop) {
        binop.left.accept(this);
        IrRValue leftVal = lastRValue;
        
        binop.right.accept(this);
        IrRValue rightVal = lastRValue;
        
        String assignedVar = nameMaker.makeName("$resultOf_" + binop.opName + "_");
        emit(new IrCall(assignedVar, binop.opName, leftVal, rightVal));
    }

    @Override
    public void visit(UnaryOp unop) {
        unop.operand.accept(this);
        IrRValue operandVal = lastRValue;
        
        String assignedVar = nameMaker.makeName("$resultOf_" + unop.opName + "_");
        emit(new IrCall(assignedVar, unop.opName, operandVal));
    }

    @Override
    public void visit(FunctionCall call) {
        //TODO
    }

    @Override
    public void visit(IntConst intConst) {
        lastRValue = new IrIntConst(intConst.value);
    }

    @Override
    public void visit(BoolConst boolConst) {
        lastRValue = new IrIntConst(boolConst.value ? 1 : 0);
    }

    @Override
    public void visit(Var var) {
        lastRValue = new IrVar(var.name);
    }
    
    private void emit(IrCommand command) {
        output.add(command);
        if (command.getAssignedVar() != null) {
            lastRValue = new IrVar(command.getAssignedVar());
        }
    }
    
}
