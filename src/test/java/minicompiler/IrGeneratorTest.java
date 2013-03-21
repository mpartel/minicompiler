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
    
    @Test
    public void testIfThenStatement() {
        IrCommand[] actual = generate(
                "if x < 3 then print(true);"
                );
        IrCommand[] expected = new IrCommand[] {
            new IrCall("$resultOf_<_1", "<", new IrVar("x"), new IrIntConst(3)),
            new IrGotoIfNot("ifEnd1", new IrVar("$resultOf_<_1")),
            
            new IrCall("$resultOf_print_1", "print", new IrIntConst(1)),
            new IrLabel("ifEnd1")
        };
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void testIfThenElseStatement() {
        IrCommand[] actual = generate(
                "if !x then print(10); else print(20);"
                );
        IrCommand[] expected = new IrCommand[] {
            new IrCall("$resultOf_!_1", "!", new IrVar("x")),
            new IrGotoIfNot("else1", new IrVar("$resultOf_!_1")),
            
            new IrCall("$resultOf_print_1", "print", new IrIntConst(10)),
            new IrGoto("ifEnd1"),
            
            new IrLabel("else1"),
            new IrCall("$resultOf_print_2", "print", new IrIntConst(20)),
            
            new IrLabel("ifEnd1")
        };
        assertArrayEquals(expected, actual);
    }

    private IrCommand[] generate(String sourceCode) {
        ArrayList<Token> tokens = Tokenizer.tokenize(sourceCode);
        Statement stmt = Parser.parseStatement(tokens);
        return IrGenerator.generate(stmt).toArray(new IrCommand[0]);
    }
}
