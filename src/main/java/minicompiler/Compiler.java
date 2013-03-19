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
import minicompiler.types.StdlibTypes;

public class Compiler {
    public static void compile(Reader sourceCodeReader, Writer asmOutput) throws IOException {
        String sourceCode = StreamUtils.readAll(sourceCodeReader);
        
        ArrayList<Token> tokens = Lexer.tokenize(sourceCode);
        
        Statement stmt = Parser.parseStatement(tokens);
        
        TypeChecker.checkTypes(stmt, StdlibTypes.getTypes());
        
        List<IrCommand> intermediateRepresentation = IrGenerator.generate(stmt);
        
        List<String> asmLines = new ArrayList<String>();
        // Add program entry point, call to main and exit with exit code 0.
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
        asmOutput.flush();
    }
}
