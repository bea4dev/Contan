package org.contan_lang.environment.expection;

import org.contan_lang.syntax.tokens.Token;

public enum ContanRuntimeExceptions {
    
    INTERNAL_ERROR(ContanRuntimeException::new),
    INVALID_TYPE(ContanInvalidTypeException::new),
    NOT_FOUND_VARIABLE(ContanNotFoundVariableException::new);
    
    private final CreateExceptionFunction createExceptionFunction;
    
    ContanRuntimeExceptions(CreateExceptionFunction createExceptionFunction) {
        this.createExceptionFunction = createExceptionFunction;
    }
    
    public void throwException(String reason, Token... tokens) {
        for (int i = 0; i < tokens.length; i++) {
            reason = reason.replace("%" + i, tokens[i].getText());
        }
        throw createExceptionFunction.apply(reason);
    }
    
    
    @FunctionalInterface
    public interface CreateExceptionFunction {
        
        ContanRuntimeException apply(String message);
        
    }
    
}
