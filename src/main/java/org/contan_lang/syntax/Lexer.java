package org.contan_lang.syntax;

import org.contan_lang.syntax.tokens.DefinedStringToken;
import org.contan_lang.syntax.tokens.Token;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    public final String text;

    public Lexer(String text) {
        this.text = text;
    }
    
    public List<Token> split() {
        List<Token> tokens = new ArrayList<>();
        
        int length = text.length();
        
        StringBuilder stringBuilder = new StringBuilder();
        boolean isInStringDefine = false;
        boolean isInStringDefinePrevious = false;
        
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
    
            isInStringDefinePrevious = isInStringDefine;
            
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

            
            if (identifier == null || identifier == Identifier.DEFINE_STRING_START_OR_END) {
                stringBuilder.append(text.charAt(i));
            } else {
                String name = stringBuilder.toString();
                if (!isInStringDefinePrevious) name = name.replace(" ", "");
                if(name.length() != 0) tokens.add(new Token(this, name, null));
                stringBuilder = new StringBuilder();
                
                tokens.add(new Token(this, word, identifier));
                i += word.length() - 1;
            }

            if (isInStringDefinePrevious && !isInStringDefine) {
                tokens.add(new DefinedStringToken(this, stringBuilder.toString(), null));
                stringBuilder = new StringBuilder();
                continue;
            }
            
            if (i == length - 1) {
                if (stringBuilder.length() != 0) {
                    String name = stringBuilder.toString();
                    if (!isInStringDefinePrevious) name = name.replace(" ", "");
                    if(name.length() != 0) tokens.add(new Token(this, name, null));
                }
            }
        }
        
        return tokens;
    }
    
}
