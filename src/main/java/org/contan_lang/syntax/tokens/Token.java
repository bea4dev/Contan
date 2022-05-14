package org.contan_lang.syntax.tokens;

import org.contan_lang.syntax.Identifier;
import org.contan_lang.syntax.Lexer;
import org.jetbrains.annotations.Nullable;

public class Token {

    protected final Lexer lexer;
    protected final String text;
    protected final Identifier identifier;
    public final int startColumnIndex;
    public final int endColumnIndex;
    public final LineToken lineToken;
    
    public Token(Lexer lexer, String text, int endColumnIndex, LineToken lineToken, @Nullable Identifier identifier) {
        this.lexer = lexer;
        this.text = text;
        this.identifier = identifier;
        this.startColumnIndex = Math.max(0, endColumnIndex - text.length());
        this.endColumnIndex = endColumnIndex;
        this.lineToken = lineToken;
    }
    
    public Lexer getLexer() {return lexer;}

    public String getText() {return text;}

    public @Nullable Identifier getIdentifier() {return identifier;}
    
    public Token getLeft() {
        int index = lexer.tokens.indexOf(this);
        if (index == 0) {
            return this;
        }
        
        return lexer.tokens.get(index - 1);
    }
    
    public Token getRight() {
        int index = lexer.tokens.indexOf(this);
        if (index == lexer.tokens.size() - 1) {
            return this;
        }
    
        return lexer.tokens.get(index + 1);
    }

    @Override
    public String toString() {
        return identifier == null ? text : identifier.toString();
    }

    public boolean isLabelToken() {return text.charAt(0) == '#';}

}
