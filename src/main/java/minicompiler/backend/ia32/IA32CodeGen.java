package minicompiler.backend.ia32;

import java.util.ArrayList;
import java.util.List;
import minicompiler.ir.IrCommand;

/**
 * Generates GNU assembly output from IR code.
 *
 * A pretty good introduction to IA32 assembly with the GNU Assembler is
 * <a href="http://nongnu.uib.no/pgubook/">this book</a>.
 *
 * This implementation is very basic and produces quite inefficient code.
 * Specifically, it makes no attempt to use registers efficiently.
 */
public class IA32CodeGen {
    public static List<String> generateAsmProgram(List<IrCommand> mainFunctionBody) {
        List<String> asmLines = new ArrayList<String>();
        
        // Add program entry point, call to main and exit with exit code 0.
        asmLines.add(".globl _start");
        asmLines.add(".type _start, @function");
        asmLines.add(".text");
        asmLines.add("_start:");
        asmLines.add("    call main");
        asmLines.add("    movl $1, %eax");
        asmLines.add("    movl $0, %ebx");
        asmLines.add("    int $0x80");
        asmLines.add(".type main, @function");
        asmLines.add("main:");
        
        asmLines.addAll(generateAsmFunctionBody(mainFunctionBody));
        
        return asmLines;
    }
    
    public static List<String> generateAsmFunctionBody(List<IrCommand> functionBody) {
        return new IA32FuncVisitor(functionBody).generate();
    }
}
