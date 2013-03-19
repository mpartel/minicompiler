package minicompiler;

public class Token {
    public static enum Type {
        LPAREN, RPAREN,
        LBRACE, RBRACE,
        SEMICOLON,
        COMMA,
        PLUS, MINUS, TIMES, DIV,
        NOT,
        ASSIGN,
        COLON,
        EQ, NEQ, // ==, <>
        LT, GT, LTE, GTE, // <, >, <=, >=
        IF, THEN, ELSE, WHILE, DO,
        INTCONST, BOOLCONST,
        IDENTIFIER,
        EOF
    }
    
    public final Type type;
    public final String text;
    public final int line;
    public final int col;
    public final int endCol;
    
    public Token(Type type, String text, int line, int col) {
        this.type = type;
        this.text = text;
        this.line = line;
        this.col = col;
        this.endCol = col + text.length();
    }

    @Override
    public int hashCode() {
        return type.hashCode() + text.hashCode() + line + col;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Token) {
            Token that = (Token)obj;
            return
                    this.type.equals(that.type) &&
                    this.text.equals(that.text) &&
                    this.line == that.line &&
                    this.col == that.col;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        switch (type) {
            case INTCONST:
            case BOOLCONST:
            case IDENTIFIER:
                return type.toString() + "(" + text + ")@" + line + ":" + col;
            default:
                return type.toString() + "@" + line + ":" + col;
        }
    }
}
