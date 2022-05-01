package org.contan_lang.syntax.tokens;

import org.contan_lang.syntax.Lexer;

import java.util.List;

public class BlockToken extends Token {
    
    public final List<Token> tokens;
    
    public BlockToken(Lexer lexer, List<Token> tokens) {
        super(lexer, "", 0, null, null);
        this.tokens = tokens;
    }
    
    @Override
    public String toString() {
        return "BlockToken{" +
                "tokens=" + tokens +
                '}';
    }
}
