package minicompiler;

import minicompiler.ast.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class ParserTest {
    @Test
    public void testSimpleMultiplication() {
        Expr actual = parser("3 * 4").parseExpr();
        Expr expected = new BinaryOp(new IntConst(3), "*", new IntConst(4));
        assertEquals(expected, actual);
    }
    
    @Test
    public void testMathOperationNesting1() {
        Expr actual = parser("x + 3 * -y").parseExpr();
        Expr left = new Var("x");
        Expr right = new BinaryOp(new IntConst(3), "*", new UnaryOp("-", new Var("y")));
        Expr expected = new BinaryOp(left, "+", right);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testMathOperationNesting2() {
        Expr actual = parser("x * 3 - -y").parseExpr();
        Expr left = new BinaryOp(new Var("x"), "*", new IntConst(3));
        Expr right = new UnaryOp("-", new Var("y"));
        Expr expected = new BinaryOp(left, "-", right);
        assertEquals(expected, actual);
    }
    
    private Parser parser(String input) {
        return new Parser(new Lexer().tokenize(input));
    }
}
