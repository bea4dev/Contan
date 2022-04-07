package org.contan_lang.syntax.tokens;

public abstract class Token {
    
    protected final String text;
    
    public Token(String text) {
        this.text = text;
    }
    
    public String getText() {return text;}
    
}
