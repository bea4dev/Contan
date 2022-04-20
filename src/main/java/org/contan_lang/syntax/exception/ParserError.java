package org.contan_lang.syntax.exception;

import org.contan_lang.syntax.tokens.Token;

public enum ParserError {

    E0000(InternalParseException.INTERNAL_ERROR, "Internal error %0."),
    E0001(InternalParseException.UNDEFINED_VARIABLE, "Undefined variable %0"),
    E0002(InternalParseException.UNEXPECTED_SYNTAX, "Does not match the expected token."
            + System.lineSeparator() + "Expected : %s"),
    E0003(InternalParseException.UNEXPECTED_SYNTAX, "The reserved word '%0' cannot be used in the argument name."),
    E0004(InternalParseException.UNEXPECTED_SYNTAX, "Classes can only be defined within the module scope."),
    E0005(InternalParseException.UNEXPECTED_SYNTAX, "Functions can only be defined within the module or class scope."),
    E0006(InternalParseException.UNEXPECTED_SYNTAX, "Initializers can only be defined within the module or class scope.");

    private final InternalParseException internalParseException;

    private final String reason;

    ParserError(InternalParseException internalParseException, String reason) {
        this.internalParseException = internalParseException;
        this.reason = reason;
    }

    public void throwError(String replace, Token... tokens) throws ContanParseException {
        internalParseException.throwException(("[" + this.name() + "] " + reason).replace("%s", replace), tokens);
    }

}
