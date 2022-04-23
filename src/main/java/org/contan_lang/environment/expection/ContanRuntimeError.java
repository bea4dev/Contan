package org.contan_lang.environment.expection;

import org.contan_lang.syntax.tokens.Token;

public enum ContanRuntimeError {
    
    E0000(ContanRuntimeExceptions.INTERNAL_ERROR, "Internal error %0."),
    E0001(ContanRuntimeExceptions.NOT_FOUND_VARIABLE, "Variable '%0' not found."),
    E0002(ContanRuntimeExceptions.INVALID_TYPE, "Only numerical values can be multiplied.%s"),
    E0003(ContanRuntimeExceptions.NOT_FOUND_VARIABLE, "The left-hand side must represent a reference to a variable.");
    
    
    private final ContanRuntimeExceptions contanRuntimeExceptions;
    
    private final String reason;
    
    ContanRuntimeError(ContanRuntimeExceptions contanRuntimeExceptions, String reason) {
        this.contanRuntimeExceptions = contanRuntimeExceptions;
        this.reason = reason;
    }
    
    public void throwError(String replace, Token... tokens) {
        contanRuntimeExceptions.throwException(("[" + this.name() + "] " + reason).replace("%s", replace), tokens);
    }
    
}
