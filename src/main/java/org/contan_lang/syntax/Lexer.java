package org.contan_lang.syntax;

import org.contan_lang.syntax.tokens.LineToken;
import org.contan_lang.syntax.tokens.StringToken;
import org.contan_lang.syntax.tokens.Token;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    public final String text;

    public final String rootName;

    public Lexer(String rootName, String text) {
        this.rootName = rootName;
        this.text = text;
    }
    
    public List<Token> split() {
        List<Token> tokens = new ArrayList<>();
        
        int textLength = text.length();
        
        int currentLine = 1;
        int currentColumn = 0;
        LineToken currentLineToken = new LineToken(1);
        
        StringBuilder keyWord = new StringBuilder();
        
        boolean isInString = false;
        boolean escaped = false;
        
        loop : for (int i = 0; i < textLength; i++) {
            char currentCharacter = text.charAt(i);
            
            if (currentCharacter == '"' && !escaped) {
                isInString = !isInString;
            }
            
            if ((currentCharacter == ' ' || currentCharacter == '\n' || i == textLength - 1) && !isInString) {
                if (keyWord.length() != 0) {
                    String key = keyWord.toString();
                    
                    Token token = null;
                    //Check identifier
                    id : for (Identifier identifier : Identifier.values()) {
                        for (String word : identifier.words) {
                            if (word.equals(key)) {
                                token = new Token(this, key, currentColumn, currentLineToken, identifier);
                                break id;
                            }
                        }
                    }
                    
                    if (token == null) {
                        if (text.charAt(i - 1) == '"') {
                            token = new StringToken(this, keyWord.toString(), currentColumn, currentLineToken, null);
                        } else {
                            token = new Token(this, keyWord.toString(), currentColumn, currentLineToken, null);
                        }
                    }
                    
                    tokens.add(token);
                    keyWord = new StringBuilder();
                }
            } else {
                if (currentCharacter == '\\' || currentCharacter == '"') {
                    if (escaped) {
                        keyWord.append(currentCharacter);
                    }
                } else {
                    keyWord.append(currentCharacter);
                }
            }
    
            if (currentCharacter == '\\' && isInString) {
                escaped = true;
            } else {
                escaped = false;
            }
    
            
            currentColumn++;
    
            if (currentCharacter == '\n') {
                currentLine++;
                currentColumn = 0;
        
                currentLineToken.build();
                currentLineToken = new LineToken(currentLine);
            } else {
                currentLineToken.append(currentCharacter);
            }
            
            
            if (isInString) {
                continue;
            }
            
            //Check identifier
            for (Identifier identifier : Identifier.values()) {
                if (!identifier.adjoinable) {
                    continue;
                }
                
                for (String word : identifier.words) {
                    if (text.substring(i).startsWith(word)) {
                        if (keyWord.length() > 1) {
                            String key = keyWord.toString();
                            if (text.charAt(i - 1) == '"') {
                                tokens.add(new StringToken(this, key.substring(0, key.length() - 1), currentColumn - 1, currentLineToken, null));
                            } else {
                                tokens.add(new Token(this, key.substring(0, key.length() - 1), currentColumn - 1, currentLineToken, null));
                            }
                        }
                        keyWord = new StringBuilder();
                        
                        i += word.length() - 1;
                        currentColumn += word.length() - 1;
                        currentLineToken.append(word.substring(1));
                        tokens.add(new Token(this, word, currentColumn, currentLineToken, identifier));
                        
                        continue loop;
                    }
                }
            }
        }
    
        currentLineToken.build();
        
        return tokens;
    }
    
}
