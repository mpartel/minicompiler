package minicompiler.backend.ia32;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import minicompiler.errors.CompilerError;
import minicompiler.ir.IrIntConst;
import minicompiler.ir.IrRValue;
import minicompiler.ir.IrVar;
import minicompiler.misc.NameMaker;

class IA32SymbolTable {
    public final NameMaker nameMaker = new NameMaker();
    
    public TreeSet<String> externalSymbols = new TreeSet<String>();
    
    private HashMap<String, Integer> localVars = new HashMap<String, Integer>(); // Name -> stack offset
    private int stackSpaceForLocals = 0;
    
    public void reserveSpaceForLocalVar(String var) {
        if (localVars.containsKey(var)) {
            throw new CompilerError("Local variable already in symtab: " + var);
        }
        int size = 4;
        int addr = stackSpaceForLocals;
        localVars.put(var, addr);
        stackSpaceForLocals += size;
    }

    public int getStackSpaceForLocals() {
        return stackSpaceForLocals;
    }
    
    public boolean hasLocalVar(String var) {
        return localVars.containsKey(var);
    }
    
    public String rvalueToAsm(IrRValue rv) {
        if (rv instanceof IrIntConst) {
            return "$" + ((IrIntConst)rv).value;
        } else if (rv instanceof IrVar) {
            String varName = ((IrVar)rv).name;
            return localVarToAsm(varName);
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    public String localVarToAsm(String varName) {
        Integer offset = localVars.get(varName);
        if (offset == null) {
            throw new CompilerError("Reading unknown variable: " + varName);
        }
        return "-" + offset + "(%ebp)";
    }
    
    public void addExternalSymbol(String symbol) {
        externalSymbols.add(symbol);
    }
    
    public Set<String> getExternalSymbols() {
        return Collections.unmodifiableSet(externalSymbols);
    }
    
}
