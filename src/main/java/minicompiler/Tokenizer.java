package minicompiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static minicompiler.Token.Type.*;
import minicompiler.errors.TokenizerError;

/**
 * Converts source code into a list of tokens.
 * 
 * <p>
 * A token a single syntactical unit such as ':=', 'then', 'myVariable', '123', '(', etc.
 * 
 * <p>
 * This tokenizer works by repeatedly looking at the beginning of remaining input,
 * discarding any whitespace and then seeing if the input starts with any known token.
 * Most tokens are constant strings like 'then' or '(', but some tokens, such
 * as integer constants, are recognized by regular expressions.
 */
public class Tokenizer {
    public static ArrayList<Token> tokenize(String input) {
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();
        return tokenizer.result;
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

    private Tokenizer(String input) {
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
                    tryTok("*", TIMES) || tryTok("/", DIV) || tryTok("%", MOD) ||
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
                throw new TokenizerError("Cannot tokenize at line " + line + " col " + col);
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
