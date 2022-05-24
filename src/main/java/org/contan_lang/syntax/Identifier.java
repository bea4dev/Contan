package org.contan_lang.syntax;

import org.jetbrains.annotations.Nullable;

public enum Identifier {
    EXPRESSION_SPLIT(1, true, "\n", ";"),
    CLASS(13, false, "class"),
    EXTENDS(13, false, "extends"),
    INITIALIZE(14, false, "initialize"),
    FUNCTION(8, false, "function"),
    IF(13, false, "if"),
    ELSE(13, false, "else"),
    LABEL(13, true, ":"),
    REPEAT(13, false, "repeat"),
    BLOCK_START(0, true, "{"),
    BLOCK_END(0, true, "}"),
    BLOCK_OPERATOR_START(0, true, "("),
    BLOCK_OPERATOR_END(0, true, ")"),
    BLOCK_GET_START(0, true, "["),
    BLOCK_GET_END(0, true, "]"),
    DEFINE_VARIABLE(11, false, "data", "var"),
    RETURN(12, false, "return"),
    STOP(12, false, "stop"),
    SKIP(12, false, "skip"),
    ASYNC(3, false, "async"),
    SYNC(3, false, "sync"),
    DELAY(3, false, "delay"),
    DOT(3, true, "."),
    OPERATOR_EQUAL(6, true, "=="),
    INSTANCE_OF(6, false, "instanceof"),
    OPERATOR_AND(7, true, "&&"),
    OPERATOR_PLUS(5, true, "+"),
    OPERATOR_MINUS(5, true, "-"),
    OPERATOR_MULTIPLY(4, true, "*"),
    OPERATOR_DIVISION(4, true, "/"),
    LAMBDA(8, true, "=>"),
    ASSIGNMENT(10, true, "="),
    NEW(3, false, "new"),
    CONSTANT_VARIABLE(11, false, "import", "const"),
    ARGUMENT_SPLIT(0, true, ","),
    NULL(1, false, "null", "NULL");
    
    
    public final int priority;
    public final String[] words;
    public final boolean adjoinable;
    
    Identifier(int priority, boolean adjoinable, String... words){
        this.priority = priority;
        this.words = words;
        this.adjoinable = adjoinable;
    }
    
    @Nullable
    public static Identifier fromString(String keyWord) {
        for (Identifier identifier : Identifier.values()) {
            for (String word : identifier.words) {
                if (keyWord.equals(word)) {
                    return identifier;
                }
            }
        }
        
        return null;
    }
    
}
