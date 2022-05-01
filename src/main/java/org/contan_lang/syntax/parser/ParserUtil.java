package org.contan_lang.syntax.parser;

import org.contan_lang.syntax.Identifier;
import org.contan_lang.syntax.exception.ContanParseException;
import org.contan_lang.syntax.exception.ParserError;
import org.contan_lang.syntax.tokens.BlockToken;
import org.contan_lang.syntax.tokens.Token;

import java.util.ArrayList;
import java.util.List;

public class ParserUtil {

    public static List<Token> getNestedToken(List<Token> tokens, int startIndex, Identifier start, Identifier end, boolean containStartAndEnd, boolean checkLastIsEndOfTopNest) throws ContanParseException {
        int length = tokens.size();
        List<Token> nestedToken = new ArrayList<>();
        int nest = 0;
        for (int i = startIndex; i < length; i++) {
            Token token = tokens.get(i);

            Identifier identifier = token.getIdentifier();

            if (identifier == end && i != startIndex) {
                nest--;

                if (containStartAndEnd && nest == 0) {
                    nestedToken.add(token);
                }

                if (nest == 0) {
                    if (checkLastIsEndOfTopNest && i != length - 1) {
                        ParserError.E0002.throwError(end.words[0], tokens.get(startIndex));
                    }
                    return nestedToken;
                }
            }

            if (nest != 0 || containStartAndEnd) {
                nestedToken.add(token);
            }

            if (identifier == start) {
                nest++;
            }

        }

        ParserError.E0002.throwError(end.words[0], tokens.get(startIndex));
        return new ArrayList<>();
    }
    
    
    public static List<Token> getNestedTokenIfEnclosed(List<Token> tokens, Identifier start, Identifier end) throws ContanParseException {
        int length = tokens.size();
        List<Token> nestedToken = new ArrayList<>();
        int nest = 0;
        for (int i = 0; i < length; i++) {
            Token token = tokens.get(i);
            
            Identifier identifier = token.getIdentifier();
            
            if (identifier == end && i != 0) {
                nest--;
                
                if (nest == 0) {
                    if (i == length - 1) {
                        return nestedToken;
                    }
                }
            }
            
            if (nest != 0) {
                nestedToken.add(token);
            }
            
            if (identifier == start) {
                nest++;
            }
            
        }
        
        return tokens;
    }
    
    
    public static List<Token> removeLineBreaks(List<Token> tokens) throws ContanParseException {
        List<Token> result = new ArrayList<>();
        
        int length = tokens.size();
        
        for (int i = 0; i < length; i++) {
            Token token = tokens.get(i);
    
            Identifier identifier = token.getIdentifier();
            
            if (identifier == Identifier.BLOCK_START) {
                List<Token> nested = getNestedToken(tokens, i, Identifier.BLOCK_START, Identifier.BLOCK_END, true, false);
                i += nested.size() - 1;
                result.addAll(nested);
                continue;
            }
            
            if (!token.getText().equals("\n")) {
                result.add(token);
            }
        }
        
        return result;
    }
    
    
    public static List<Token> getNestedTokenReverse(List<Token> tokens, int startIndex, Identifier start, Identifier end, boolean containStartAndEnd, boolean checkLastIsEndOfTopNest) throws ContanParseException {
        List<Token> nestedToken = new ArrayList<>();
        int nest = 0;
        for (int i = startIndex; i >= 0; i--) {
            Token token = tokens.get(i);
            
            Identifier identifier = token.getIdentifier();
            
            if (identifier == end && i != startIndex) {
                nest--;
                
                if (containStartAndEnd && nest == 0) {
                    nestedToken.add(token);
                }
                
                if (nest == 0) {
                    if (checkLastIsEndOfTopNest && i != 0) {
                        ParserError.E0002.throwError(end.words[0], tokens.get(startIndex));
                    }
                    return nestedToken;
                }
            }
            
            if (nest != 0 || containStartAndEnd) {
                nestedToken.add(token);
            }
            
            if (identifier == start) {
                nest++;
            }
            
        }
        
        ParserError.E0002.throwError(end.words[0], tokens.get(startIndex));
        return new ArrayList<>();
    }


    public static List<Token> getTokensUntilFoundIdentifier(List<Token> tokens, int startIndex, Identifier end) throws ContanParseException {
        int length = tokens.size();
        List<Token> nestedToken = new ArrayList<>();
        for (int i = startIndex; i < length; i++) {
            Token token = tokens.get(i);

            Identifier identifier = token.getIdentifier();

            if (identifier == end) {
                return nestedToken;
            }

            nestedToken.add(token);
        }

        ParserError.E0002.throwError(end.words[0], tokens.get(startIndex));
        return new ArrayList<>();
    }


    public static List<Token> getDefinedArguments(List<Token> tokens) throws ContanParseException {
        List<Token> args = new ArrayList<>();

        for (Token token : tokens) {
            Identifier identifier = token.getIdentifier();

            if (identifier != null) {
                if (identifier == Identifier.BLOCK_OPERATOR_START || identifier == Identifier.BLOCK_OPERATOR_END
                        || identifier == Identifier.ARGUMENT_SPLIT || identifier == Identifier.EXPRESSION_SPLIT) {

                    continue;
                }

                ParserError.E0003.throwError("", token);
            }

            args.add(token);
        }

        return args;
    }


    public static boolean isNumber(String text) {
        return text.matches("[+-]?\\d+(?:\\.\\d+)?");
    }

    public static boolean containsIdentifier(List<Token> tokens, Identifier identifier) {
        for (Token token : tokens) {
            if (token.getIdentifier() == identifier) {
                return true;
            }
        }
        return false;
    }

}
