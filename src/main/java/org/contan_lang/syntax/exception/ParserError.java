package org.contan_lang.syntax.exception;

import org.contan_lang.syntax.tokens.Token;

public enum ParserError {

    E0000(ParserExceptions.INTERNAL_ERROR, "Internal error %0."),
    E0001(ParserExceptions.UNDEFINED_VARIABLE, "Undefined variable : %0"),
    E0002(ParserExceptions.UNEXPECTED_SYNTAX, "Does not match the expected token."
            + System.lineSeparator() + "Expected : %s"),
    E0003(ParserExceptions.UNEXPECTED_SYNTAX, "The reserved word '%0' cannot be used in the argument name."),
    E0004(ParserExceptions.UNEXPECTED_SYNTAX, "Classes can only be defined within the module scope."),
    E0005(ParserExceptions.UNEXPECTED_SYNTAX, "Functions can only be defined within the module or class scope."),
    E0006(ParserExceptions.UNEXPECTED_SYNTAX, "Initializers can only be defined within the module or class scope."),
    E0007(ParserExceptions.UNEXPECTED_SYNTAX, "A variable name was expected, but a reserved word was specified."),
    E0008(ParserExceptions.UNEXPECTED_SYNTAX, "Invalid syntax. Variable names are contiguous."),
    E0009(ParserExceptions.UNEXPECTED_SYNTAX, "Expressions cannot be written to the left of variable declarations."),
    E0010(ParserExceptions.UNEXPECTED_SYNTAX, "Reserved words are not allowed in variable names."),
    E0011(ParserExceptions.UNEXPECTED_SYNTAX, "It is an incomplete substitution expression."),
    E0012(ParserExceptions.UNEXPECTED_SYNTAX, "The expression is required on both sides."),
    E0013(ParserExceptions.LINK_FAILURE, "Class not found."),
    E0014(ParserExceptions.LINK_FAILURE, "Function not found."),
    E0015(ParserExceptions.UNEXPECTED_SYNTAX, "The import statement is invalid. " +
            "It should be in the form  'import ClassName = \"org.example.ExampleClass\"'"),
    E0016(ParserExceptions.UNEXPECTED_SYNTAX, "The module import statement is invalid. " +
            "It should be in the form  'module ModuleName = \"ExampleModule.cntn\"'"),
    E0017(ParserExceptions.UNEXPECTED_SYNTAX, "The right side of the lambda expression must be an expression."),
    E0018(ParserExceptions.UNEXPECTED_SYNTAX, "To the right of return we need an expression."),
    E0019(ParserExceptions.UNEXPECTED_SYNTAX, "Cannot write an expression on the left side of return.");

    private final ParserExceptions parserExceptions;

    private final String reason;

    ParserError(ParserExceptions parserExceptions, String reason) {
        this.parserExceptions = parserExceptions;
        this.reason = reason;
    }

    public void throwError(String replace, Token... tokens) throws ContanParseException {
        parserExceptions.throwException(("[" + this.name() + "] " + reason).replace("%s", replace), tokens);
    }

}
