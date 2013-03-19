package minicompiler;

import java.util.ArrayList;
import java.util.List;
import minicompiler.ast.*;
import static minicompiler.Token.Type.*;
import minicompiler.errors.ParseError;
import minicompiler.types.BoolType;
import minicompiler.types.IntType;
import minicompiler.types.Type;

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
        Token second = peekAtOffset(1);
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
        Expr left = parseSubfactor();
        Token second = peek();
        while (!looksLikeExprEnd(second)) {
            left = parseTerm(left);
            second = peek();
        }
        return left;
    }
    
    private boolean looksLikeExprEnd(Token t) {
        switch (t.type) {
            case RPAREN:
            case SEMICOLON:
            case COMMA:
            case THEN:
            case ELSE:
            case DO:
            case EOF:
                return true;
            default:
                return false;
        }
    }
    
    private Expr parseTerm(Expr left) {
        Token op = peek();
        Expr right;
        switch (op.type) {
            case AND:
            case OR:
            case PLUS:
            case MINUS:
                consume();
                right = parseExpr();
                break;
            default:
                return parseFactor(left);
        }
        return new BinaryOp(left, op.text, right);
    }
    
    private Expr parseFactor(Expr left) {
        Token op = peek();
        Expr right;
        switch (op.type) {
            case EQ:
            case NEQ:
            case LT:
            case GT:
            case LTE:
            case GTE:
            case TIMES:
            case DIV:
                consume();
                right = parseSubfactor();
                break;
            default:
                return parseSubfactor();
        }
        return new BinaryOp(left, op.text, right);
    }
    
    private Expr parseSubfactor() {
        Token t = consume();
        if (t.type == LPAREN) {
            Expr e = parseExpr();
            consume(RPAREN);
            return e;
        } else {
            switch (t.type) {
                case MINUS: return new UnaryOp("-", parseSubfactor());
                case NOT: return new UnaryOp("!", parseSubfactor());
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
