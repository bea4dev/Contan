package org.contan_lang.syntax.exception;

import org.contan_lang.syntax.tokens.LineToken;
import org.contan_lang.syntax.tokens.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ParserExceptions {

    INTERNAL_ERROR(ContanParseException::new),
    UNEXPECTED_SYNTAX(UnexpectedSyntaxException::new),
    UNDEFINED_VARIABLE(UndefinedParseException::new),
    LINK_FAILURE(ContanParseException::new);

    private final CreateExceptionFunction createExceptionFunction;

    ParserExceptions(CreateExceptionFunction createExceptionFunction) {
        this.createExceptionFunction = createExceptionFunction;
    }

    public void throwException(String reason, Token... tokens) throws ContanParseException {
        for (int i = 0; i < tokens.length; i++) {
            reason = reason.replace("%" + i, tokens[i].getText());
        }

        //Marge tokens
        Map<LineToken, List<Token>> tokenListMap = new HashMap<>();

        //Sort with line
        if (tokens.length != 0) {
            for (Token token : tokens) {
                tokenListMap.computeIfAbsent(token.lineToken, k -> new ArrayList<>()).add(token);
            }
        }

        StringBuilder line = new StringBuilder();
        line.append(System.lineSeparator());
        line.append(System.lineSeparator());

        StringBuilder reasonBuilder = new StringBuilder(reason);
        for (Map.Entry<LineToken, List<Token>> entry : tokenListMap.entrySet()) {
            LineToken lineToken = entry.getKey();
            List<Token> tokenList = entry.getValue();

            int start = Integer.MAX_VALUE;
            int end = 0;

            for (Token token : tokenList) {
                start = Math.min(token.startColumnIndex, start);
                end = Math.max(token.endColumnIndex, end);
            }

            String lineText = lineToken.getLineText();

            int lineLabelLength = String.valueOf(lineToken.line).length() + 1;

            line.append("Module : ");
            line.append(tokenList.get(0).getLexer().rootName);
            line.append(",  Line : ");
            line.append(lineToken.line);
            line.append(",  Column : ");
            line.append(start);
            line.append(System.lineSeparator());

            StringBuilder space = new StringBuilder();
            for (int i = 0; i < lineLabelLength; i++) {
                space.append(" ");
            }

            line.append(space);
            line.append("|");
            line.append(System.lineSeparator());

            line.append(lineToken.line);
            line.append(" | ");
            int lineStart = Math.max(0, start - 20);
            int lineEnd = Math.min(lineText.length(), end + 30);
            line.append(lineText, lineStart, lineEnd);
            line.append(System.lineSeparator());

            line.append(space);
            line.append("| ");

            space = new StringBuilder();
            for (int i = 0; i < start - lineStart - 1; i++) {
                space.append(" ");
            }

            for (int i = start; i < end; i++) {
                space.append("^");
            }
            line.append(space);

            line.append(System.lineSeparator());
            reasonBuilder.append(line);
        }
        reason = reasonBuilder.toString();


        throw createExceptionFunction.apply(reason);
    }


    @FunctionalInterface
    public interface CreateExceptionFunction {

        ContanParseException apply(String message);

    }

}
