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
        ArrayList<Token> tokens = Tokenizer.tokenize(sourceCode);
        Statement stmt = Parser.parseStatement(tokens);
        TypeChecker.checkTypes(stmt, StdlibTypes.getTypes());
        List<IrCommand> irCommands = IrGenerator.generate(stmt);
        List<String> asmLines = IA32CodeGen.generateAsmProgram(irCommands);
        StreamUtils.writeLines(asmLines, asmOutput);
    }
}
