package minicompiler.backend.ia32;

import java.util.ArrayList;
import java.util.List;
import minicompiler.ir.*;

/**
 * Generates GNU assembly output from IR code.
 *
 * A pretty good introduction to IA32 assembly with the GNU Assembler is
 * <a href="http://nongnu.uib.no/pgubook/">this book</a>.
 *
 * This implementation is very basic and produces quite inefficient code.
 * Specifically, it makes no attempt to use registers efficiently.
 */
class IA32FuncVisitor extends IrVisitor {
    private List<IrCommand> input;
    private List<String> output;
    
    private IA32SymbolTable symTab;

    public IA32FuncVisitor(List<IrCommand> input) {
        this.input = input;
        this.output = new ArrayList<String>();
        
        this.symTab = new IA32SymbolTable();
        
        for (IrCommand cmd : input) {
            String var = cmd.getAssignedVar();
            if (var != null && !symTab.hasLocalVar(var)) {
                symTab.reserveSpaceForLocalVar(var);
            }
        }
    }
    
    public List<String> generate() {
        output.clear();
        generateFunctionStart();
        for (IrCommand cmd : input) {
            cmd.accept(this);
        }
        generateFunctionEnd();
        return output;
    }
    
    private void generateFunctionStart() {
        emit("pushl %ebp");      // Save old base pointer used by caller
        emit("movl %esp, %ebp"); // Set new base pointer
        emit("subl $" + symTab.getStackSpaceForLocals() + ", %esp"); // Reserve stack space.
    }
    
    private void generateFunctionEnd() {
        emit("movl %ebp, %esp"); // Discard local stack frame
        emit("popl %ebp");       // Retrieve old base pointer
        emit("ret");             // Return to return address
    }

    @Override
    public void visit(IrCopy copy) {
        emit("movl " + symTab.rvalueToAsm(copy.rvalue) + ", %eax");
        emit("movl %eax, " + symTab.localVarToAsm(copy.assignedVar));
    }

    @Override
    public void visit(IrCall call) {
        String func = call.functionName;
        if (IA32Builtins.isBuiltin(func)) {
            compileBuiltin(call);
        } else {
            compileCallToFunction(call);
        }
    }
    
    private void compileBuiltin(IrCall call) {
        String func = call.functionName;
        IA32Builtin builtin = IA32Builtins.getBuiltin(func);
        if (builtin.argCount != call.args.size()) {
            throw new IllegalArgumentException("Call to builtin " + func + " takes " + builtin.argCount + " args but " + call.args.size() + " given.");
        }
        
        String[] lines = builtin.generate(symTab, call.args.toArray(new IrRValue[call.args.size()]));
        for (String line : lines) {
            emit(line);
        }
        
        emit("movl %eax, " + symTab.localVarToAsm(call.assignedVar));
    }
    
    private void compileCallToFunction(IrCall call) {
        symTab.addExternalSymbol(call.functionName);
        
        int stackSpaceForArgs = 0;
        for (int i = call.args.size() - 1; i >= 0; i--) {
            IrRValue arg = call.args.get(i);
            emit("pushl " + symTab.rvalueToAsm(arg));
            stackSpaceForArgs += 4;
        }
        
        emit("call " + call.functionName);
        emit("addl $" + stackSpaceForArgs + ", %esp"); // Discard arguments from stack
        emit("movl %eax, " + symTab.localVarToAsm(call.assignedVar));
    }

    @Override
    public void visit(IrGoto g) {
        emit("jmp " + g.labelName);
    }

    @Override
    public void visit(IrGotoIf g) {
        emit("movl " + symTab.rvalueToAsm(g.condition) + ", %eax");
        emit("cmpl $0, %eax");
        emit("jne " + g.labelName);
    }

    @Override
    public void visit(IrGotoIfNot g) {
        emit("movl " + symTab.rvalueToAsm(g.condition) + ", %eax");
        emit("cmpl $0, %eax");
        emit("je " + g.labelName);
    }

    @Override
    public void visit(IrLabel g) {
        emit(g.name + ":");
    }
    
    private void emit(String line) {
        output.add(line);
    }
}
