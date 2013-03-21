package minicompiler;

import java.util.ArrayList;
import java.util.List;
import minicompiler.ast.*;
import static minicompiler.Token.Type.*;
import minicompiler.errors.ParseError;
import minicompiler.types.BoolType;
import minicompiler.types.IntType;
import minicompiler.types.Type;

/**
 * Converts a list of tokens into an abstract syntax tree (AST).
 * 
 * <p>
 * This parser is a hand-written recursive descent parser.
 * It parses statements by looking at the first few tokens.
 * For instance, if the first token is a 'while' then it knows that what
 * follows must be 'while [some-expression] do [some-statement]'.
 * It consumes the 'while' token, calls 'parseExpr', consumes the 'do'
 * token and then calls 'parseStatement'.
 * 
 * <p>
 * Parsing math expressions is a little trickier because of operator precedence.
 * Let's see how parsing '3 + 4 * 5 + 6' works.
 * 
 * <p>
 * Let's define 'mathexpr', 'term' and 'factor' (recall polynomials).
 * <ul>
 *   <li>A <em>factor</em>
 *       can be (among other things) an integer constant.
 *   <li>A <em>term</em>
 *       can be a factor,
 *       or a smaller term followed by a '*' or '/', and then a factor.
 *   <li>A <em>mathexpr</em>
 *       can be either a term,
 *       or a smaller mathexpr followed by a '+' or '-', and then a term.
 * </ul>
 * 
 * <p>
 * We can say the same thing with the following notation:<br>
 * <pre>
 *   factor ::= [0-9]+
 *   term ::= factor | term '*' factor | term '/' factor
 *   mathexpr ::= term | mathexpr '+' term | mathexpr '-' term
 * </pre>
 * 
 * <p>
 * To parse a mathexpr we first recursively parse its left term.
 * Then we check to see if the next token is + or -.
 * If it's not, we return just the left term.
 * If it is + or -, we parse the right side as a factor to get the
 * AST 'left +/- right'.
 * 
 * <p>
 * Parsing a term works the same way,
 * except with * and /, and it recursively parses factors.
 */
public class Parser {
    public static Statement parseStatement(ArrayList<Token> input) {
        Parser parser = new Parser(input);
        Statement stmt = parser.parseStatement();
        parser.consume(EOF);
        return stmt;
    }
    
     public static Expr parseExpr(ArrayList<Token> input) {
        Parser parser = new Parser(input);
        Expr stmt = parser.parseExpr();
        parser.consume(EOF);
        return stmt;
    }
    
    private ArrayList<Token> input;
    private int inputIndex;
    private Token eof;

    private Parser(ArrayList<Token> input) {
        this.input = input;
        this.inputIndex = 0;
        if (input.isEmpty()) {
            this.eof = new Token(EOF, "<EOF>", 0, 0);
        } else {
            Token last = input.get(input.size() - 1);
            this.eof = new Token(EOF, "<EOF>", last.line, last.endCol);
        }
    }
    
    private Statement parseStatement() {
        Token first = peek();
        Token second = peekSecond();
        if (first.type == LBRACE) {
            return parseBlock();
        } else if (first.type == WHILE) {
            return parseWhile();
        } else if (first.type == IF) {
            return parseIf();
        } else if (first.type == IDENTIFIER && second.type == COLON) {
            return parseDeclaration();
        } else if (first.type == IDENTIFIER && second.type == ASSIGN) {
            return parseAssignment();
        } else {
            Expr expr = parseExpr();
            consume(SEMICOLON);
            return expr;
        }
    }
    
    private Block parseBlock() {
        consume(LBRACE);
        ArrayList<Statement> statements = new ArrayList<Statement>();
        while (true) {
            Token t = peek();
            if (t.type == RBRACE) {
                break;
            } else {
                statements.add(parseStatement());
            }
        }
        consume(RBRACE);
        return new Block(statements);
    }
    
    private WhileLoop parseWhile() {
        consume(WHILE);
        Expr head = parseExpr();
        consume(DO);
        Statement body = parseStatement();
        return new WhileLoop(head, body);
    }
    
    private IfStatement parseIf() {
        consume(IF);
        Expr condition = parseExpr();
        consume(THEN);
        Statement thenClause = parseStatement();
        if (peek().type == ELSE) {
            consume(ELSE);
            Statement elseClause = parseStatement();
            return new IfStatement(condition, thenClause, elseClause);
        } else {
            return new IfStatement(condition, thenClause);
        }
    }
    
    private Statement parseDeclaration() {
        String varName = consume(IDENTIFIER).text;
        consume(COLON);
        Type type = parseType();
        consume(ASSIGN);
        Expr expr = parseExpr();
        Statement decl = new Declaration(varName, type, expr);
        consume(SEMICOLON);
        return decl;
    }

    private Statement parseAssignment() {
        String varName = consume(IDENTIFIER).text;
        consume(ASSIGN);
        Expr expr = parseExpr();
        Statement assignment = new Assignment(varName, expr);
        consume(SEMICOLON);
        return assignment;
    }
    
    private Expr parseExpr() {
        Expr left = parseMathexpr();
        Token op = peek();
        switch (op.type) {
            case EQ:
            case NEQ:
            case LT:
            case GT:
            case LTE:
            case GTE:
                consume();
                Expr right = parseMathexpr();
                return new BinaryOp(left, op.text, right);
            default:
                return left;
        }
    }
    
    private Expr parseMathexpr() {
        Expr left = parseTerm();
        while (true) {
            Token op = peek();
            switch (op.type) {
                case PLUS:
                case MINUS:
                    consume();
                    Expr right = parseTerm();
                    left = new BinaryOp(left, op.text, right);
                    break;
                default:
                    return left;
            }
        }
    }
    
    private Expr parseTerm() {
        Expr left = parseFactor();
        while (true) {
            Token op = peek();
            switch (op.type) {
                case TIMES:
                case DIV:
                case MOD:
                    consume();
                    Expr right = parseFactor();
                    left = new BinaryOp(left, op.text, right);
                    break;
                default:
                    return left;
            }
        }
    }
    
    private Expr parseFactor() {
        Token t = consume();
        if (t.type == LPAREN) {
            Expr e = parseExpr();
            consume(RPAREN);
            return e;
        } else {
            switch (t.type) {
                case MINUS: return new UnaryOp("-", parseFactor());
                case NOT: return new UnaryOp("!", parseFactor());
                case INTCONST: return new IntConst(Integer.parseInt(t.text));
                case BOOLCONST: return new BoolConst(Boolean.parseBoolean(t.text));
                case IDENTIFIER:
                    if (peek().type == LPAREN) {
                        String functionName = t.text;
                        consume(LPAREN);
                        List<Expr> args = parseArguments();
                        consume(RPAREN);
                        return new FunctionCall(functionName, args);
                    } else {
                        return new Var(t.text);
                    }
                default: return fail("integer or boolean or variable expected instead of '" + t.text + "'");
            }
        }
    }
    
    private List<Expr> parseArguments() {
        ArrayList<Expr> result = new ArrayList<Expr>();
        while (peek().type != RPAREN) {
            result.add(parseExpr());
            if (peek().type == COMMA) {
                consume(COMMA);
            }
        }
        return result;
    }
    
    private Type parseType() {
        Token t = consume(IDENTIFIER);
        if (t.text.equals("int")) {
            return IntType.instance;
        } else if (t.text.equals("bool")) {
            return BoolType.instance;
        } else {
            return fail(t.text + " is not a known type");
        }
    }
    
    private Token peek() {
        return peekAtOffset(0);
    }
    
    private Token peekSecond() {
        return peekAtOffset(1);
    }
    
    private Token peekAtOffset(int offset) {
        if (inputIndex + offset < input.size()) {
            return input.get(inputIndex + offset);
        } else {
            return eof;
        }
    }
    
    private Token consume(Token.Type expected) {
        Token actual = peek();
        if (actual.type == expected) {
            inputIndex++;
            return actual;
        } else {
            return fail(expected + " expected");
        }
    }
    
    private Token consume() {
        Token tok = peek();
        inputIndex++;
        return tok;
    }
    
    private <T> T fail(String error) {
        Token t = peek();
        throw new ParseError("Parse error near line " + t.line + " col " + t.col + ": " + error);
    }
}
