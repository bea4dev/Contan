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
    
    public Token left;
    public Token right;
    
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
    
    public Token getLeft(int shift) {
        Token current = this;
        for (int i = 0; i < shift; i++) {
            Token temp = current.left;
            if (temp == null) {
                return current;
            }
            
            current = temp;
        }
        
        return current;
    }
    
    public Token getRight(int shift) {
        Token current = this;
        for (int i = 0; i < shift; i++) {
            Token temp = current.right;
            if (temp == null) {
                return current;
            }
            
            current = temp;
        }
        
        return current;
    }

    @Override
    public String toString() {
        return identifier == null ? text : identifier.toString();
    }
}
