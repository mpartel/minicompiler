package minicompiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static minicompiler.Token.Type.*;
import minicompiler.errors.LexerError;

public class Lexer {
    public static ArrayList<Token> tokenize(String input) {
        Lexer lexer = new Lexer(input);
        lexer.tokenize();
        return lexer.result;
    }
    
    private static final Pattern rIntConst = Pattern.compile("[0-9]+");
    private static final Pattern rBoolConst = Pattern.compile("true|false");
    private static final Pattern rIdentifier = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");

    private static final HashMap<String, Token.Type> keywords = new HashMap<String, Token.Type>();
    
    static {
        keywords.put("if", IF);
        keywords.put("then", THEN);
        keywords.put("else", ELSE);
        keywords.put("while", WHILE);
        keywords.put("do", DO);
    }
    
    private ArrayList<Token> result;
    private String input;
    private int line;
    private int col;

    private Lexer(String input) {
        this.result = new ArrayList<Token>();
        this.input = input;
        this.line = 1;
        this.col = 1;
    }
    
    private void tokenize() {
        skipWhitespace();
        while (!input.isEmpty()) {
            boolean ok =
                    tryTok("(", LPAREN) || tryTok(")", RPAREN) ||
                    tryTok("{", LBRACE) || tryTok("}", RBRACE) ||
                    tryTok(";", SEMICOLON) ||
                    tryTok(",", COMMA) ||
                    tryTok("+", PLUS) || tryTok("-", MINUS) ||
                    tryTok("*", TIMES) || tryTok("/", DIV) ||
                    tryTok("!", NOT) ||
                    tryTok(":=", ASSIGN) ||
                    tryTok(":", COLON) ||
                    tryTok("==", EQ) || tryTok("<>", NEQ) ||
                    tryTok("<=", LTE) || tryTok(">=", GTE) ||
                    tryTok("<", LT) || tryTok(">", GT) ||
                    tryRegex(rIntConst, INTCONST) ||
                    tryRegex(rBoolConst, BOOLCONST) ||
                    tryKeywordOrIdentifier();
            if (!ok) {
                throw new LexerError("Cannot tokenize at line " + line + " col " + col);
            }
            
            skipWhitespace();
        }
    }
    
    private void skipWhitespace() {
        int i = 0;
        while (i < input.length() && Character.isWhitespace(input.charAt(i))) {
            i++;
        }
        consumeInput(i);
    }
    
    private boolean tryTok(String expected, Token.Type ty) {
        if (input.startsWith(expected)) {
            result.add(new Token(ty, expected, line, col));
            consumeInput(expected.length());
            return true;
        } else {
            return false;
        }
    }
    
    private boolean tryRegex(Pattern p, Token.Type ty) {
        Matcher m = p.matcher(input);
        if (m.lookingAt()) {
            result.add(new Token(ty, m.group(), line, col));
            consumeInput(m.end());
            return true;
        } else {
            return false;
        }
    }
    
    private boolean tryKeywordOrIdentifier() {
        if (tryRegex(rIdentifier, IDENTIFIER)) {
            Token tok = result.get(result.size() - 1);
            Token.Type kwType = keywords.get(tok.text);
            if (kwType != null) {
                tok = new Token(kwType, tok.text, tok.line, tok.col);
                result.set(result.size() - 1, tok);
            }
            return true;
        } else {
            return false;
        }
    }

    private void consumeInput(int amount) {
        for (int i = 0; i < amount; ++i) {
            char c = input.charAt(i);
            if (c == '\n') {
                line++;
                col = 1;
            } else if (c == '\r') {
                // Ignore
            } else {
                col++;
            }
        }
        input = input.substring(amount);
    }
}
