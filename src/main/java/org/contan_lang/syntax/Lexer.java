package org.contan_lang.syntax;

import org.contan_lang.syntax.tokens.IdentifierToken;
import org.contan_lang.syntax.tokens.NameToken;
import org.contan_lang.syntax.tokens.Token;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    
    public static List<Token> split(String text) {
        List<Token> tokens = new ArrayList<>();
        
        int length = text.length();
        
        StringBuilder stringBuilder = new StringBuilder();
        boolean isInStringDefine = false;
        boolean isInStringDefinePre = false;
        
        for(int i = 0; i < length; i++) {
            //Check identifier
            Identifier identifier = null;
            String word = "";
            idLoop : for (Identifier id : Identifier.values()) {
                for (String w : id.words) {
                    int endIndex = Math.min(i + w.length(), length);
                    
                    if (w.equals(text.substring(i, endIndex))) {
                        identifier = id;
                        word = w;
                        break idLoop;
                    }
                }
            }
    
            isInStringDefinePre = isInStringDefine;
            
            if (identifier != null) {
                if (identifier == Identifier.DEFINE_STRING_START_OR_END) {
                    if (i != 0) {
                        if (text.charAt(i - 1) == '\\') {
                            identifier = null;
                        }
                    }
                    
                    if(identifier != null){
                        isInStringDefine = !isInStringDefine;
                    }
                }
            }
            
            if(isInStringDefine && identifier != Identifier.DEFINE_STRING_START_OR_END) {
                identifier = null;
            }
            
            if (identifier != null) {
                if (!identifier.adjoinable) {
                    boolean is = false;
                    if (i != 0) {
                        if (String.valueOf(text.charAt(i - 1)).matches("^[A-Za-z0-9]+$")) {
                            is = true;
                        }
                    }
                    
                    if (i + word.length() < length) {
                        if (String.valueOf(text.charAt(i + word.length())).matches("^[A-Za-z0-9]+$")) {
                            is = true;
                        }
                    }
                    
                    if (is) {
                        identifier = null;
                    }
                }
            }
            
            if (identifier == null) {
                stringBuilder.append(text.charAt(i));
            } else {
                String name = stringBuilder.toString();
                if (!isInStringDefinePre) name = name.replace(" ", "");
                if(name.length() != 0) tokens.add(new NameToken(name));
                stringBuilder = new StringBuilder();
                
                tokens.add(new IdentifierToken(word, identifier));
                i += word.length() - 1;
            }
            
            if (i == length - 1) {
                if (stringBuilder.length() != 0) {
                    String name = stringBuilder.toString();
                    if (!isInStringDefinePre) name = name.replace(" ", "");
                    if(name.length() != 0) tokens.add(new NameToken(name));
                }
            }
        }
        
        return tokens;
    }
    
    
    public static @Nullable Identifier getIdentifier(String text, int index) {
        int length = text.length();
        if (length <= index) return null;
        
        for(Identifier identifier : Identifier.values()) {
            for(String word : identifier.words) {
                int endIndex = index + word.length();
                if (length <= endIndex) continue;
                
                if (word.equals(text.substring(index, endIndex))) {
                    return identifier;
                }
            }
        }
        
        return null;
    }
    
}
