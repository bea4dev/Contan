package org.contan_lang.syntax.exception;

import org.contan_lang.syntax.tokens.Token;

public enum ParserExceptions {

    INTERNAL_ERROR(ContanParseException::new),
    UNEXPECTED_SYNTAX(UnexpectedSyntaxException::new),
    UNDEFINED_VARIABLE(UndefinedParseException::new);

    private final CreateExceptionFunction createExceptionFunction;

    ParserExceptions(CreateExceptionFunction createExceptionFunction) {
        this.createExceptionFunction = createExceptionFunction;
    }

    public void throwException(String reason, Token... tokens) throws ContanParseException {
        for (int i = 0; i < tokens.length; i++) {
            reason = reason.replace("%" + i, tokens[i].getText());
        }
        throw createExceptionFunction.apply(reason);
    }


    @FunctionalInterface
    public interface CreateExceptionFunction {

        ContanParseException apply(String message);

    }

}
