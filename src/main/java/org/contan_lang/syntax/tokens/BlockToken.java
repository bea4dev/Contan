package org.contan_lang.syntax.tokens;

import org.contan_lang.syntax.Identifier;
import org.contan_lang.syntax.Lexer;

import java.util.List;

public class BlockToken extends Token {
    
    public final List<Token> tokens;
    public final Token originalToken;
    
    public BlockToken(Lexer lexer, List<Token> tokens, Token originalToken) {
        super(lexer, "", 0, null, null);
        this.tokens = tokens;
        this.originalToken = originalToken;
    }
    
    public BlockToken(Lexer lexer, List<Token> tokens, Identifier identifier, Token originalToken) {
        super(lexer, "", 0, null, identifier);
        this.tokens = tokens;
        this.originalToken = originalToken;
    }
    
    @Override
    public String toString() {
        return "BlockToken{" +
                "tokens=" + tokens +
                '}';
    }
}
