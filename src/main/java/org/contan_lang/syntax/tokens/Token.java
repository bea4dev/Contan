package org.contan_lang.syntax.tokens;

import org.contan_lang.syntax.Identifier;
import org.contan_lang.syntax.Lexer;
import org.jetbrains.annotations.Nullable;

public class Token {

    protected final Lexer lexer;
    protected final String text;
    protected final Identifier identifier;
    
    public Token(Lexer lexer, String text, @Nullable Identifier identifier) {
        this.lexer = lexer;
        this.text = text;
        this.identifier = identifier;
    }

    public Lexer getLexer() {return lexer;}

    public String getText() {return text;}

    public @Nullable Identifier getIdentifier() {return identifier;}
    
    public Token marge(Token... tokens) {
        return this;
    }

}
