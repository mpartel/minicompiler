package minicompiler;

import minicompiler.ast.*;
import minicompiler.errors.ParseError;
import minicompiler.types.IntType;
import static org.junit.Assert.*;
import org.junit.Test;

public class ParserTest {
    @Test
    public void testSimpleMultiplication() {
        Expr actual = tokenizeAndParseExpr("3 * 4");
        Expr expected = new BinaryOp(new IntConst(3), "*", new IntConst(4));
        assertEquals(expected, actual);
    }
    
    @Test
    public void testMathOperationNesting1() {
        Expr actual = tokenizeAndParseExpr("x + 3 * -y");
        Expr left = new Var("x");
        Expr right = new BinaryOp(new IntConst(3), "*", new UnaryOp("-", new Var("y")));
        Expr expected = new BinaryOp(left, "+", right);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testMathOperationNesting2() {
        Expr actual = tokenizeAndParseExpr("x * 3 - -y");
        Expr left = new BinaryOp(new Var("x"), "*", new IntConst(3));
        Expr right = new UnaryOp("-", new Var("y"));
        Expr expected = new BinaryOp(left, "-", right);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testMathAndComparison() {
        Expr actual = tokenizeAndParseExpr("x * 3 < x + 3");
        Expr left = new BinaryOp(new Var("x"), "*", new IntConst(3));
        Expr right = new BinaryOp(new Var("x"), "+", new IntConst(3));
        Expr expected = new BinaryOp(left, "<", right);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testWhileStatement() {
        Statement actual = tokenizeAndParseStatement(
                "{\n" +
                "  x : int := 0;\n" +
                "  while x <= 3 do { x := x + 1; }\n" +
                "}"
                );
        Statement init = new Declaration("x", IntType.instance, new IntConst(0));
        Expr head = new BinaryOp(new Var("x"), "<=", new IntConst(3));
        Statement body = new Block(new Assignment("x", new BinaryOp(new Var("x"), "+", new IntConst(1))));
        Statement whileLoop = new WhileLoop(head, body);
        Statement expected = new Block(init, whileLoop);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testWhileStatementWithParens() {
        Statement actual = tokenizeAndParseStatement("while (x < 3) do {}");
        Expr head = new BinaryOp(new Var("x"), "<", new IntConst(3));
        Statement body = new Block();
        Statement expected = new WhileLoop(head, body);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testSimpleIfStatement() {
        Statement actual = tokenizeAndParseStatement("if x <> 0 then print(x);");
        Expr condition = new BinaryOp(new Var("x"), "<>", new IntConst(0));
        Statement thenClause = new FunctionCall("print", new Var("x"));
        Statement expected = new IfStatement(condition, thenClause);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testIfElse() {
        Statement actual = tokenizeAndParseStatement("if x <> 0 then print(x); else print(42);");
        Expr condition = new BinaryOp(new Var("x"), "<>", new IntConst(0));
        Statement thenClause = new FunctionCall("print", new Var("x"));
        Statement elseClause = new FunctionCall("print", new IntConst(42));
        Statement expected = new IfStatement(condition, thenClause, elseClause);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testNestedIfElse() {
        Statement actual = tokenizeAndParseStatement("if x then if y then 1; else 2; else 3;");
        Statement inner = new IfStatement(new Var("y"), new IntConst(1), new IntConst(2));
        Statement outer = new IfStatement(new Var("x"), inner, new IntConst(3));
        assertEquals(outer, actual);
    }
    
    @Test(expected=ParseError.class)
    public void testJunkAtEndOfInput() {
        tokenizeAndParseStatement("x := 3; foobar x y z");
    }
    
    private Statement tokenizeAndParseStatement(String input) {
        return Parser.parseStatement(Tokenizer.tokenize(input));
    }
    
    private Expr tokenizeAndParseExpr(String input) {
        return Parser.parseExpr(Tokenizer.tokenize(input));
    }
}
