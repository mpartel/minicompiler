package minicompiler.backend.ia32;

import java.util.List;
import minicompiler.ir.IrCommand;

public class IA32CodeGen {
    public static List<String> generateFunctionBody(List<IrCommand> functionBody) {
        return new IA32FuncVisitor(functionBody).generate();
    }
}
