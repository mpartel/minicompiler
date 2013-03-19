package minicompiler;

import java.util.List;
import static minicompiler.Token.Type.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class LexerTest {
    private Lexer lexer;
    
    @Before
    public void setUp() {
        lexer = new Lexer();
    }
    
    @Test
    public void testLexer() {
        List<Token> result = lexer.tokenize(
                "x: int := 35;\n" +
                "while (x <= 123)\n" +
                "{ iffy }\n"
                );
        Token[] expected = {
            new Token(IDENTIFIER, "x", 1, 1),
            new Token(COLON, ":", 1, 2),
            new Token(IDENTIFIER, "int", 1, 4),
            new Token(ASSIGN, ":=", 1, 8),
            new Token(INTCONST, "35", 1, 11),
            new Token(SEMICOLON, ";", 1, 13),
            new Token(WHILE, "while", 2, 1),
            new Token(LPAREN, "(", 2, 7),
            new Token(IDENTIFIER, "x", 2, 8),
            new Token(LTE, "<=", 2, 10),
            new Token(INTCONST, "123", 2, 13),
            new Token(RPAREN, ")", 2, 16),
            new Token(LBRACE, "{", 3, 1),
            new Token(IDENTIFIER, "iffy", 3, 3),
            new Token(RBRACE, "}", 3, 8)
        };
        Token[] actual = result.toArray(new Token[result.size()]);
        assertArrayEquals(expected, actual);
   }
}
