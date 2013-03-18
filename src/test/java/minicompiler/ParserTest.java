package minicompiler;

import minicompiler.ast.*;
import minicompiler.types.IntType;
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
    
    @Test
    public void testWhileStatement() {
        Statement actual = parser(
                "{\n" +
                "  x : int := 0;\n" +
                "  while x <= 3 do { x := x + 1; }\n" +
                "}"
                ).parseCompletely();
        Statement init = new Declaration("x", IntType.instance, new IntConst(0));
        Expr head = new BinaryOp(new Var("x"), "<=", new IntConst(3));
        Statement body = new Block(new Assignment("x", new BinaryOp(new Var("x"), "+", new IntConst(1))));
        Statement whileLoop = new WhileLoop(head, body);
        Statement expected = new Block(init, whileLoop);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testWhileStatementWithParens() {
        Statement actual = parser("while (x < 3) do {}").parseCompletely();
        Expr head = new BinaryOp(new Var("x"), "<", new IntConst(3));
        Statement body = new Block();
        Statement expected = new WhileLoop(head, body);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testSimpleIfStatement() {
        Statement actual = parser("if x <> 0 then print(x);").parseCompletely();
        Expr condition = new BinaryOp(new Var("x"), "<>", new IntConst(0));
        Statement thenClause = new FunctionCall("print", new Var("x"));
        Statement expected = new IfStatement(condition, thenClause);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testIfElse() {
        Statement actual = parser("if x <> 0 then print(x); else print(42);").parseCompletely();
        Expr condition = new BinaryOp(new Var("x"), "<>", new IntConst(0));
        Statement thenClause = new FunctionCall("print", new Var("x"));
        Statement elseClause = new FunctionCall("print", new IntConst(42));
        Statement expected = new IfStatement(condition, thenClause, elseClause);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testNestedIfElse() {
        Statement actual = parser("if x then if y then 1; else 2; else 3;").parseCompletely();
        Statement inner = new IfStatement(new Var("y"), new IntConst(1), new IntConst(2));
        Statement outer = new IfStatement(new Var("x"), inner, new IntConst(3));
        assertEquals(outer, actual);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testJunkAtEndOfInput() {
        parser("x := 3; foobar x y z").parseCompletely();
    }
    
    private Parser parser(String input) {
        return new Parser(new Lexer().tokenize(input));
    }
}
