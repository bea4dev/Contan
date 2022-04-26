package org.contan_lang.syntax;

import org.contan_lang.syntax.parser.ParserUtil;
import org.contan_lang.syntax.tokens.DefinedStringToken;
import org.contan_lang.syntax.tokens.LineToken;
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

        StringBuilder keyWorld = new StringBuilder();
        boolean escaped = false;
        boolean isString = false;

        int currentLine = 1;
        int columnIndex = 0;
        LineToken currentLineToken = new LineToken(currentLine);

        loop : for (int i = 0; i < text.length(); i++) {

            String after = text.substring(i);
            char chara = text.charAt(i);

            if (chara == '\n') {
                currentLineToken.build();

                currentLine++;
                currentLineToken = new LineToken(currentLine);
                columnIndex = 0;
            } else {
                currentLineToken.append(chara);
                columnIndex++;
            }

            //Skip space
            if (!isString && chara == ' ') {
                continue;
            }

            //Check identifier
            if (!isString) {
                for (Identifier identifier : Identifier.values()) {
                    for (String word : identifier.words) {
                        if (after.startsWith(word)) {
                            
                            if (i != text.length() - 1 && identifier == Identifier.DOT) {
                                //Determine if it is a float dot.
                                if (ParserUtil.isNumber(keyWorld.toString()) && Character.isDigit(text.charAt(i + 1))) {
                                    keyWorld.append(".");
                                    continue loop;
                                }
                            }
                            
                            i += word.length() - 1;

                            if (keyWorld.length() != 0) {
                                tokens.add(new Token(this, keyWorld.toString(), columnIndex, currentLineToken, null));
                            }
                            tokens.add(new Token(this, word, columnIndex + word.length(), currentLineToken, identifier));
                            keyWorld = new StringBuilder();

                            String sub = word.substring(1);
                            currentLineToken.append(sub);
                            columnIndex += sub.length();

                            continue loop;
                        }
                    }
                }
            }

            if (chara != '\\' && chara != '\"') {
                keyWorld.append(chara);
            }

            //String token
            if (chara == '\"' && !escaped) {
                if (isString) {
                    tokens.add(new DefinedStringToken(this, keyWorld.toString(), columnIndex, currentLineToken, null));
                    keyWorld = new StringBuilder();
                }
                isString = !isString;
            }

            if (isString) {
                if (escaped) {
                    if (chara == '\"' || chara == '\\') {
                        keyWorld.append(chara);
                    }
                }

                escaped = chara == '\\';
            }
        }


        if (keyWorld.length() != 0) {
            tokens.add(new Token(this, keyWorld.toString(), Math.max(0, columnIndex - keyWorld.length()), currentLineToken, null));
        }

        currentLineToken.build();

        return tokens;
    }
    
}
