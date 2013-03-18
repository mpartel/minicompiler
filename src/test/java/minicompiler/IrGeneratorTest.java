package minicompiler;

import java.util.ArrayList;
import minicompiler.ast.Statement;
import minicompiler.ir.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class IrGeneratorTest {
    
    @Test
    public void testWhileLoop() {
        IrCommand[] actual = generate(
                "{\n" +
                "  x : int := 0;\n" +
                "  while (x < 3) do {\n" +
                "    x := x + 1;\n" +
                "  }\n" +
                "}"
                );
        IrCommand[] expected = new IrCommand[] {
            new IrCopy("x", new IrIntConst(0)),
            
            new IrLabel("whileHead1"),
            new IrCall("$resultOf_<_1", "<", new IrVar("x"), new IrIntConst(3)),
            new IrGotoIfNot("whileEnd1", new IrVar("$resultOf_<_1")),
            
            new IrCall("$resultOf_+_1", "+", new IrVar("x"), new IrIntConst(1)),
            new IrCopy("x", new IrVar("$resultOf_+_1")),
            
            new IrGoto("whileHead1"),
            
            new IrLabel("whileEnd1")
        };
        assertArrayEquals(expected, actual);
    }
    
    //TODO: more tests!

    private IrCommand[] generate(String sourceCode) {
        ArrayList<Token> tokens = new Lexer().tokenize(sourceCode);
        Statement stmt = new Parser(tokens).parseStatement();
        IrGenerator gen = new IrGenerator();
        stmt.accept(gen);
        return gen.getOutput().toArray(new IrCommand[0]);
    }
}
