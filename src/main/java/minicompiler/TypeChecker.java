package minicompiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import minicompiler.ast.*;
import minicompiler.errors.TypeError;
import minicompiler.types.*;

public class TypeChecker {
    public static void checkTypes(Statement statement, Map<String, Type> knownTypes) {
        TypeCheckVisitor visitor = new TypeCheckVisitor(knownTypes);
        statement.accept(visitor);
    }
    
    private static class TypeCheckVisitor extends AstVisitor {
        private TypeSymTab symTab = new TypeSymTab();
        private Type lastType = VoidType.instance;

        public TypeCheckVisitor(Map<String, Type> initialTypes) {
            symTab.putAll(initialTypes);
        }

        @Override
        public void visit(Declaration decl) {
            if (symTab.containsKey(decl.varName)) {
                throw new TypeError("Variable " + decl.varName + " already declared.");
            }
            symTab.put(decl.varName, decl.type);
            
            decl.expr.accept(this);
            if (!canBeAssigned(lastType, decl.type)) {
                throw new TypeError("Variable '" + decl.varName + "' cannot be initialized with a " + lastType);
            }
            
            lastType = VoidType.instance;
        }

        @Override
        public void visit(Assignment assignment) {
            Type varTy = symTab.get(assignment.varName);
            if (varTy == null) {
                throw new TypeError("Assignment to undeclared variable: " + assignment.varName);
            }
            
            assignment.expr.accept(this);
            if (!canBeAssigned(lastType, varTy)) {
                throw new TypeError("Cannot assign " + lastType + " to '" + assignment.varName + "' (of type " + varTy + ")");
            }
            
            lastType = VoidType.instance;
        }

        @Override
        public void visit(Block block) {
            symTab.pushState();
            for (Statement stmt : block.statements) {
                stmt.accept(this);
            }
            symTab.popState();
            
            lastType = VoidType.instance;
        }

        @Override
        public void visit(IfStatement ifStmt) {
            ifStmt.condition.accept(this);
            if (!lastType.equals(BoolType.instance)) {
                throw new TypeError("If condition was " + lastType + " instead of bool");
            }
            
            symTab.pushState();
            ifStmt.thenClause.accept(this);
            symTab.popState();
            symTab.pushState();
            ifStmt.elseClause.accept(this);
            symTab.popState();
            
            lastType = VoidType.instance;
        }

        @Override
        public void visit(WhileLoop whileLoop) {
            whileLoop.head.accept(this);
            if (!lastType.equals(BoolType.instance)) {
                throw new TypeError("While loop condition was " + lastType + " instead of bool");
            }
            
            symTab.pushState();
            whileLoop.body.accept(this);
            symTab.popState();
            
            lastType = VoidType.instance;
        }

        @Override
        public void visit(FunctionCall call) {
            ArrayList<Type> argTypes = new ArrayList<Type>(call.arguments.size());
            for (Expr argExpr : call.arguments) {
               argExpr.accept(this);
               argTypes.add(lastType);
            }
            
            checkFunctionCall(call.functionName, argTypes);
        }

        @Override
        public void visit(UnaryOp unop) {
            unop.operand.accept(this);
            checkFunctionCall(unop.opName, lastType);
        }
        
        @Override
        public void visit(BinaryOp binop) {
            binop.left.accept(this);
            Type leftType = lastType;
            binop.right.accept(this);
            Type rightType = lastType;
            
            checkFunctionCall(binop.opName, leftType, rightType);
        }
        
        @Override
        public void visit(IntConst intConst) {
            lastType = IntType.instance;
        }

        @Override
        public void visit(BoolConst boolConst) {
            lastType = BoolType.instance;
        }
        
        @Override
        public void visit(Var var) {
            lastType = symTab.get(var.name);
            if (lastType == null) {
                throw new TypeError("Unknown variable: " + var.name);
            }
        }
        
        private void checkFunctionCall(String funcName, List<Type> givenArgTypes) {
            Type funcType = symTab.get(funcName);
            
            if (funcType instanceof FunctionType) {
                FunctionType ft = (FunctionType)funcType;
                
                if (ft.argTypes.size() != givenArgTypes.size()) {
                    throw new TypeError(funcName + " expects " + ft.argTypes.size() + " arguments but " + givenArgTypes.size() + " given");
                }
                
                for (int i = 0; i < givenArgTypes.size(); i++) {
                    Type given = givenArgTypes.get(i);
                    Type expected = ft.argTypes.get(i);
                    if (!canBeAssigned(given, expected)) {
                        throw new TypeError(funcName + " argument " + (i+1) + " expects " + expected + " but " + given + " given");
                    }
                }
                
                lastType = ft.returnType;
            } else {
                throw new TypeError(funcName + " is not a known function or operator");
            }
        }
        
        private void checkFunctionCall(String funcName, Type... givenArgTypes) {
            checkFunctionCall(funcName, Arrays.asList(givenArgTypes));
        }
        
        private boolean canBeAssigned(Type from, Type to) {
            return from.equals(to); // If we had subtyping then we'd check that from is a subtype of to.
        }
    }
}
