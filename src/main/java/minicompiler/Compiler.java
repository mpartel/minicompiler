package minicompiler;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import minicompiler.ast.Statement;
import minicompiler.backend.ia32.IA32CodeGen;
import minicompiler.ir.IrCommand;
import minicompiler.misc.StreamUtils;

public class Compiler {
    public static void compile(Reader sourceCodeReader, Writer asmOutput) throws IOException {
        String sourceCode = StreamUtils.readAll(sourceCodeReader);
        
        //TODO: clean up the APIs of the compiler phases.
        
        ArrayList<Token> tokens = new Lexer().tokenize(sourceCode);
        
        Parser parser = new Parser(tokens);
        Statement stmt = parser.parseCompletely();
        
        IrGenerator irGen = new IrGenerator();
        stmt.accept(irGen);
        
        List<IrCommand> intermediateRepresentation = irGen.getOutput();
        
        List<String> asmLines = new ArrayList<String>();
        asmLines.add(".globl _start");
        asmLines.add(".type _start, @function");
        asmLines.add(".text");
        asmLines.add("_start:");
        asmLines.add("  call main");
        asmLines.add("  movl $1, %eax");
        asmLines.add("  movl $0, %ebx");
        asmLines.add("  int $0x80");
        asmLines.add(".type main, @function");
        asmLines.add("main:");
        
        asmLines.addAll(IA32CodeGen.generateFunctionBody(intermediateRepresentation));
        
        for (String line : asmLines) {
            if (!line.endsWith(":") && !line.startsWith(".")) {
                asmOutput.write("    ");
            }
            asmOutput.write(line);
            asmOutput.write('\n');
        }
    }
}
