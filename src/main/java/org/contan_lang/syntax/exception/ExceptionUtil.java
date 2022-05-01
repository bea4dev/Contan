package org.contan_lang.syntax.exception;

import org.contan_lang.syntax.tokens.LineToken;
import org.contan_lang.syntax.tokens.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExceptionUtil {

    public static String createVisualErrorMessage(String reason, Token... tokens) {
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
            int lineStart = Math.max(0, start - 30);
            int lineEnd = Math.min(lineText.length(), end + 30);
            line.append(lineText, lineStart, lineEnd);
            line.append(System.lineSeparator());
        
            line.append(space);
            line.append("| ");
        
            space = new StringBuilder();
            for (int i = 0; i < start - lineStart; i++) {
                space.append(" ");
            }
        
            for (int i = start; i < end; i++) {
                space.append("^");
            }
            line.append(space);
        
            line.append(System.lineSeparator());
            reasonBuilder.append(line);
        }
        
        return reasonBuilder.toString();
    }
    
}
