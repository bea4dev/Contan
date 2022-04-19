package org.contan_lang.syntax.exception;

import org.contan_lang.syntax.tokens.Token;

public class ContanParseException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    public ContanParseException(String reason) {
        super(reason);
    }
    
}
