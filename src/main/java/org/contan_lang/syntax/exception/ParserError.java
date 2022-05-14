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
    E0008(ParserExceptions.UNEXPECTED_SYNTAX, "Invalid expression syntax."),
    E0009(ParserExceptions.UNEXPECTED_SYNTAX, "Expressions cannot be written to the left of variable declarations."),
    E0010(ParserExceptions.UNEXPECTED_SYNTAX, "Reserved words are not allowed in variable names."),
    E0011(ParserExceptions.UNEXPECTED_SYNTAX, "It is an incomplete substitution expression."),
    E0012(ParserExceptions.UNEXPECTED_SYNTAX, "The expression is required on both sides."),
    E0013(ParserExceptions.LINK_FAILURE, "Class not found."),
    E0014(ParserExceptions.LINK_FAILURE, "Function not found."),
    E0015(ParserExceptions.UNEXPECTED_SYNTAX, "The 'import' or 'const' statement is invalid. "),
    E0017(ParserExceptions.UNEXPECTED_SYNTAX, "The right side of the lambda expression must be an expression."),
    E0018(ParserExceptions.UNEXPECTED_SYNTAX, "To the right of 'return', 'stop' or 'skip' we need an expression."),
    E0019(ParserExceptions.UNEXPECTED_SYNTAX, "Cannot write an expression on the left side of return."),
    E0020(ParserExceptions.UNEXPECTED_SYNTAX, "The control expression or the expression around it is incorrect."),
    E0021(ParserExceptions.UNEXPECTED_SYNTAX, "To the right of the 'function' keyword must be an argument and an expression block."),
    E0022(ParserExceptions.UNEXPECTED_SYNTAX, "The expression cannot be written to the left of the 'async', 'sync' or 'delay' keyword."),
    E0023(ParserExceptions.UNEXPECTED_SYNTAX, "An expression or block is required to the right of the 'async', 'sync' or 'delay' keyword."),
    E0024(ParserExceptions.UNEXPECTED_SYNTAX, "The execution part of the 'async', 'sync' or 'delay' expressions must be enclosed in a block."),
    E0025(ParserExceptions.UNEXPECTED_SYNTAX, "On the right side, an expression is needed."),
    E0026(ParserExceptions.UNEXPECTED_SYNTAX, "Variable names cannot contain '#'."),
    E0027(ParserExceptions.UNEXPECTED_SYNTAX, "Only the label name can be written to the right of the loop control keyword."),
    E0028(ParserExceptions.UNEXPECTED_SYNTAX, "The specified label name cannot be found."),
    E0029(ParserExceptions.UNEXPECTED_SYNTAX, "A key is required in the '[]' operator."),
    E0030(ParserExceptions.UNEXPECTED_SYNTAX, "Expressions cannot be written on the right side of the '[]' operator."),
    E0031(ParserExceptions.UNEXPECTED_SYNTAX, "The number of ticks to delay must be specified to the right of the 'delay' keyword.");

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
