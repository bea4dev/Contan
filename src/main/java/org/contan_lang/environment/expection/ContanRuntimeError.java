package org.contan_lang.environment.expection;

import org.contan_lang.syntax.tokens.Token;

public enum ContanRuntimeError {
    
    E0000(ContanRuntimeExceptions.INTERNAL_ERROR, "Internal error %0."),
    E0001(ContanRuntimeExceptions.NOT_FOUND_VARIABLE, "Variable '%0' not found."),
    E0002(ContanRuntimeExceptions.INVALID_TYPE, "Only numerical values can be multiplied.%s"),
    E0003(ContanRuntimeExceptions.NOT_FOUND_VARIABLE, "The left-hand side must represent a reference to a variable."),
    E0004(ContanRuntimeExceptions.JAVA_RUNTIME_ERROR, "Not fount java class constructor."),
    E0005(ContanRuntimeExceptions.JAVA_RUNTIME_ERROR, "A problem has occurred when executing a java method.%s"),
    E0006(ContanRuntimeExceptions.JAVA_RUNTIME_ERROR, "Not fount java class constructor.%s"),
    E0007(ContanRuntimeExceptions.JAVA_RUNTIME_ERROR, "Java method not found.%s"),
    E0008(ContanRuntimeExceptions.JAVA_RUNTIME_ERROR, "A problem has occurred when executing a java method."),
    E0009(ContanRuntimeExceptions.INVALID_TYPE, "This type does not support conversion to numeric."),
    E0010(ContanRuntimeExceptions.ARGUMENT_NOT_MATCH, "The number of arguments does not match."),
    E0011(ContanRuntimeExceptions.FUNCTION_NOT_FOUND, "Function not found."),
    E0012(ContanRuntimeExceptions.JAVA_RUNTIME_ERROR, "Failed to assign to a field in Java."),
    E0013(ContanRuntimeExceptions.JAVA_RUNTIME_ERROR, "Failed to retrieve Java field object."),
    E0014(ContanRuntimeExceptions.INVALID_TYPE, "Not class object.  Object : %s"),
    E0015(ContanRuntimeExceptions.JAVA_RUNTIME_ERROR, "Field not found.");
    
    
    private final ContanRuntimeExceptions contanRuntimeExceptions;
    
    private final String reason;
    
    ContanRuntimeError(ContanRuntimeExceptions contanRuntimeExceptions, String reason) {
        this.contanRuntimeExceptions = contanRuntimeExceptions;
        this.reason = reason;
    }
    
    public void throwError(String replace, Throwable cause, Token... tokens) {
        contanRuntimeExceptions.throwException(("[" + this.name() + "] " + reason).replace("%s", replace), cause, tokens);
    }
    
}
