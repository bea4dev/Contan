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
    E0015(ContanRuntimeExceptions.JAVA_RUNTIME_ERROR, "Field not found."),
    E0016(ContanRuntimeExceptions.FUNCTION_NOT_FOUND, "The arguments specified do not match the arguments of the function."),
    E0017(ContanRuntimeExceptions.ARGUMENT_NOT_MATCH, "Arguments were expected to be function or lambda expressions, but others were specified."),
    E0018(ContanRuntimeExceptions.INVALID_TYPE, "The data is in an unexpected format." +
                                                        System.lineSeparator() + "Expected : %s"),
    E0019(ContanRuntimeExceptions.FUNCTION_NOT_FOUND, "You tried to call an internal function of a lambda or function expression. Are you sure you are describing it correctly?"),
    E0020(ContanRuntimeExceptions.ARGUMENT_NOT_MATCH, "Right next to the sync keyword, an expression representing the thread must be written."),
    E0021(ContanRuntimeExceptions.INVALID_TYPE, "Non-numeric values cannot be sign-reversed."),
    E0022(ContanRuntimeExceptions.JAVA_RUNTIME_ERROR, "Java class not found."),
    E0023(ContanRuntimeExceptions.ACCESS_ERROR, "Variables defined as const cannot be reassigned."),
    E0024(ContanRuntimeExceptions.INVALID_TYPE, "Conditional expressions of if must return a boolean type."),
    E0025(ContanRuntimeExceptions.INVALID_TYPE, "No other number than an integer may be specified for the number of repeats."),
    E0026(ContanRuntimeExceptions.INVALID_TYPE, "Cannot specify a non-Java class."),
    E0027(ContanRuntimeExceptions.ARGUMENT_NOT_MATCH, "Failed to insert into java array.%s"),
    E0028(ContanRuntimeExceptions.INVALID_TYPE, "Cannot use anything other than an integer to specify the array index."),
    E0029(ContanRuntimeExceptions.ARGUMENT_NOT_MATCH, "Array length cannot have negative values."),
    E0030(ContanRuntimeExceptions.ARGUMENT_NOT_MATCH, "The index must be less than or equal to '" + Integer.MAX_VALUE + "'."),
    E0031(ContanRuntimeExceptions.ARGUMENT_NOT_MATCH, "The task delay time must be specified as an integer."),
    E0032(ContanRuntimeExceptions.CONTAN_RUNTIME_ERROR, "Task delay is valid only for tick-based threads."),
    E0033(ContanRuntimeExceptions.ACCESS_ERROR, "The specified module cannot be found."),
    E0034(ContanRuntimeExceptions.CONTAN_RUNTIME_ERROR, "Module initialization failed."),
    E0035(ContanRuntimeExceptions.INVALID_TYPE, "It is trying to extend a non-class."),
    E0036(ContanRuntimeExceptions.INVALID_TYPE, ""),
    E0037(ContanRuntimeExceptions.INVALID_TYPE, ""),
    E0038(ContanRuntimeExceptions.INVALID_INDEX_OR_KEY, "%s"),
    E0039(ContanRuntimeExceptions.INVALID_REFERENCE_VALUE, "%s");
    
    
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
