package minicompiler.backend.ia32;

import minicompiler.ir.IrRValue;

abstract class IA32Builtin {
    public final int argCount;

    public IA32Builtin(int argCount) {
        this.argCount = argCount;
    }
    
    public abstract String[] generate(IA32SymbolTable symTab, IrRValue[] args);
}
