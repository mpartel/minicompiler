package minicompiler;

import minicompiler.ast.Statement;
import minicompiler.errors.TypeError;
import minicompiler.types.StdlibTypes;
import org.junit.Test;
import static org.junit.Assert.*;

public class TypeCheckerTest {
    @Test
    public void testMathExpr() {
        shouldCheck("1;");
        shouldCheck("1 + 1;");
        shouldCheck("1 + 3 * 2;");
        shouldCheck("(1 - 3) / 2;");
        shouldFail("1 + true;");
        shouldFail("(1 - true) / 2;");
    }
    
    @Test
    public void testCallToPrintInt() {
        shouldCheck("printInt(1+1);");
        shouldFail("printInt(true);");
    }
    
    @Test
    public void testAssignments() {
        shouldCheck("{ x : int := 1; x := 2; }");
        shouldFail("{ x := 2; }");
    }
    
    @Test
    public void testIfStatement() {
        shouldCheck("if 1 < 2 then printInt(1); else printInt(2);");
        shouldFail("if 1 then printInt(1); else printInt(2);");
        
        shouldCheck("{ if 1 < 2 then { x : int := 1; } }");
        shouldFail("{ if 1 < 2 then { x : int := 1; } x := 5; }");
        
        shouldCheck("{ if 1 < 2 then {} else { x : int := 1; } }");
        shouldFail("{ if 1 < 2 then {} else { x : int := 1; } x := 5; }");
    }
    
    @Test
    public void testWhileLoop() {
        shouldCheck("{ x : int := 3; while x > 0 do x := x - 1; }");
        shouldFail("{ x : int := 3; while x - 0 do x := x - 1; }");
        shouldCheck("{ x : int := 3; while x > 0 do x := x - 1; }");
        shouldCheck("{ x : int := 3; while x > 0 do { y : int := 1; x := x - y; } }");
        shouldFail("{ x : int := 3; while x > 0 do { y : int := 1; x := x - y; } y := y + 5; }");
        shouldCheck("{ x : int := 3; while x > 0 do { y : int := 1; x := x - y; } x := x + 5; }");
    }
    
    private void shouldCheck(String code) {
        Statement stmt = Parser.parseStatement(Lexer.tokenize(code));
        TypeChecker.checkTypes(stmt, StdlibTypes.getTypes());
    }
    
    private void shouldFail(String code) {
        Statement stmt = Parser.parseStatement(Lexer.tokenize(code));
        try {
            TypeChecker.checkTypes(stmt, StdlibTypes.getTypes());
        } catch (TypeError e) {
            return;
        }
        fail("Did not fail type check: " + code);
    }
}
