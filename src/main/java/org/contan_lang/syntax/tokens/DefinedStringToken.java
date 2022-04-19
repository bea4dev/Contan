package org.contan_lang.syntax.tokens;

import org.contan_lang.syntax.Identifier;
import org.contan_lang.syntax.Lexer;
import org.jetbrains.annotations.Nullable;

public class DefinedStringToken extends Token {

    private final String definedText;

    public DefinedStringToken(Lexer lexer, String text, @Nullable Identifier identifier) {
        super(lexer, text, identifier);
        this.definedText = text.substring(1, text.length() - 1);
    }

    @Override
    public String getText() {return super.getText();}

}
