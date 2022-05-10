package org.contan_lang.environment.expection;

import org.contan_lang.syntax.exception.ExceptionUtil;
import org.contan_lang.syntax.tokens.Token;
import org.jetbrains.annotations.Nullable;

public enum ContanRuntimeExceptions {
    
    INTERNAL_ERROR(ContanRuntimeException::new),
    INVALID_TYPE(ContanInvalidTypeException::new),
    NOT_FOUND_VARIABLE(ContanNotFoundVariableException::new),
    JAVA_RUNTIME_ERROR(ContanJavaRuntimeException::new),
    ARGUMENT_NOT_MATCH(ContanJavaRuntimeException::new),
    FUNCTION_NOT_FOUND(ContanRuntimeException::new),
    ACCESS_ERROR(ContanJavaRuntimeException::new);
    
    private final CreateExceptionFunction createExceptionFunction;
    
    ContanRuntimeExceptions(CreateExceptionFunction createExceptionFunction) {
        this.createExceptionFunction = createExceptionFunction;
    }
    
    public void throwException(String reason, @Nullable Throwable cause, Token... tokens) {
        for (int i = 0; i < tokens.length; i++) {
            reason = reason.replace("%" + i, tokens[i].getText());
        }
    
        //Visual error message
        reason = ExceptionUtil.createVisualErrorMessage(reason, tokens);
        
        throw createExceptionFunction.apply(reason, cause);
    }
    
    
    @FunctionalInterface
    public interface CreateExceptionFunction {
        
        ContanRuntimeException apply(String message, Throwable cause);
        
    }
    
}
