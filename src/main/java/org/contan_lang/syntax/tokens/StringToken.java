package org.contan_lang.syntax.tokens;

import org.contan_lang.syntax.Identifier;
import org.contan_lang.syntax.Lexer;
import org.jetbrains.annotations.Nullable;

public class StringToken extends Token {

    public StringToken(Lexer lexer, String text, int endIndex, LineToken lineToken, @Nullable Identifier identifier) {
        super(lexer, text, endIndex, lineToken, identifier);
    }
    
    @Override
    public String toString() {
        return "\"" + text + "\"";
    }
}
