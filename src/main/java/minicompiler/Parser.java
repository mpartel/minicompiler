package minicompiler;

import java.util.ArrayList;
import minicompiler.ast.*;
import static minicompiler.Token.Type.*;
import minicompiler.types.BoolType;
import minicompiler.types.IntType;
import minicompiler.types.Type;

public class Parser {
    private ArrayList<Token> input;
    private int inputIndex;
    private Token eof;

    public Parser(ArrayList<Token> input) {
        this.input = input;
        this.inputIndex = 0;
        if (input.isEmpty()) {
            this.eof = new Token(EOF, "<EOF>", 0, 0);
        } else {
            Token last = input.get(input.size() - 1);
            this.eof = new Token(EOF, "<EOF>", last.line, last.endCol);
        }
    }
    
    public Statement parseStatement() {
        Token first = peek();
        if (first.type == LBRACE) {
            return parseBlock();
        } else {
            Token second = peekAtOffset(1);
            Statement result;
            if (first.type == IDENTIFIER && second.type == COLON) {
                String varName = consume(IDENTIFIER).text;
                Type type = parseType();
                consume(ASSIGN);
                Expr expr = parseExpr();
                result = new Declaration(varName, type, expr);
            } else if (first.type == IDENTIFIER && second.type == ASSIGN) {
                String varName = consume(IDENTIFIER).text;
                consume(ASSIGN);
                Expr expr = parseExpr();
                result = new Assignment(varName, expr);
            } else {
                result = parseExpr();
            }
            consume(SEMICOLON);
            return result;
        }
    }
    
    public Block parseBlock() {
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
    
    public Expr parseExpr() {
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
            case LT:
            case GT:
            case LTE:
            case GTE:
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
            consume(LPAREN);
            Expr e = parseExpr();
            consume(RPAREN);
            return e;
        } else {
            switch (t.type) {
                case MINUS: return new UnaryOp("-", parseSubfactor());
                case NOT: return new UnaryOp("!", parseSubfactor());
                case INTCONST: return new IntConst(Integer.parseInt(t.text));
                case BOOLCONST: return new BoolConst(Boolean.parseBoolean(t.text));
                case IDENTIFIER: return new Var(t.text);
                default: return fail("integer or boolean or variable expected");
            }
        }
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
        if (inputIndex < input.size()) {
            return input.get(inputIndex);
        } else {
            return eof;
        }
    }
    
    private Token peekAtOffset(int offset) {
        if (inputIndex + offset - 1 < input.size()) {
            return input.get(inputIndex + offset - 1);
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
        throw new IllegalArgumentException("Parse error near line " + t.line + " col " + t.col + ": " + error);
    }
}
