package org.contan_lang.syntax.tokens;

import org.contan_lang.syntax.Identifier;

public class IdentifierToken extends Token {
    
    private final Identifier identifier;
    
    public IdentifierToken(String text, Identifier identifier) {
        super(text);
        this.identifier = identifier;
    }
    
    public Identifier getIdentifier() {return identifier;}
    
}
