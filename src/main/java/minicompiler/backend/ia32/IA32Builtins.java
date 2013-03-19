package minicompiler.backend.ia32;

import java.util.HashMap;
import minicompiler.ir.IrRValue;

/** Emits code for IrCalls to builtin operations like '+' and '-'. */
class IA32Builtins {
    private static final HashMap<String, IA32Builtin> builtins = new HashMap<String, IA32Builtin>();
    
    static {
        builtins.put("+", binaryArithmeticBuiltin("addl"));
        builtins.put("-", binaryArithmeticBuiltin("subl"));
        builtins.put("*", binaryArithmeticBuiltin("imull"));
        builtins.put("/", binaryArithmeticBuiltin("idivl")); // FIXME: works differently
        
        builtins.put("<", binaryComparisonBuiltin("cmovl"));
        builtins.put(">", binaryComparisonBuiltin("cmovg"));
        builtins.put("<=", binaryComparisonBuiltin("cmovle"));
        builtins.put(">=", binaryComparisonBuiltin("cmovge"));
        builtins.put("==", binaryComparisonBuiltin("cmove"));
        builtins.put("!=", binaryComparisonBuiltin("cmovne"));
        
        builtins.put("!", notOperatorBuiltin());
    }
    
    private static IA32Builtin binaryArithmeticBuiltin(final String asmOp) {
        return new IA32Builtin(2) {
            @Override
            public String[] generate(IA32SymbolTable symTab, IrRValue[] args) {
                return new String[] {
                    "movl " + symTab.rvalueToAsm(args[0]) + ", %eax",
                    asmOp + " " + symTab.rvalueToAsm(args[1]) + ", %eax"
                };
            }
        };
    }
    
    private static IA32Builtin binaryComparisonBuiltin(final String cmovOp) {
        return new IA32Builtin(2) {
            @Override
            public String[] generate(IA32SymbolTable symTab, IrRValue[] args) {
                return new String[] {
                    // Probably more inefficient than cmp+jump, but keeps the compiler simple
                    "movl $0, %eax",
                    "movl $1, %ebx",
                    "movl " + symTab.rvalueToAsm(args[0]) + ", %ecx",
                    "cmpl " + symTab.rvalueToAsm(args[1]) + ", %ecx",
                    cmovOp + " %ebx, %eax",
                };
            }
        };
    }
    
    private static IA32Builtin notOperatorBuiltin() {
        return new IA32Builtin(1) {
            @Override
            public String[] generate(IA32SymbolTable symTab, IrRValue[] args) {
                return new String[] {
                    "movl " + symTab.rvalueToAsm(args[0]) + ", %eax",
                    "xorl %eax, %eax",
                    "movl %eax, " + symTab.rvalueToAsm(args[0]),
                };
            }
        };
    }
    
    public static boolean isBuiltin(String name) {
        return builtins.containsKey(name);
    }
    
    public static IA32Builtin getBuiltin(String name) {
        return builtins.get(name);
    }
}
