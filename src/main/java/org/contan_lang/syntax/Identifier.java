package org.contan_lang.syntax;

public enum Identifier {
    EXPRESSION_SPLIT(1, true, "\n", ";"),
    FUNCTION(11, false, "function"),
    IF(10, false, "if"),
    BLOCK_START(10, true, "{"),
    BLOCK_END(10, true, "}"),
    BLOCK_OPERATOR_START(3, true, "("),
    BLOCK_OPERATOR_END(3, true, ")"),
    DEFINE_DATA(9, false, "data"),
    DEFINE_STRING_START_OR_END(2, true, "\""),
    OPERATOR_EQUAL(6, true, "=="),
    OPERATOR_AND(8, true, "&&"),
    OPERATOR_PLUS(5, true, "+"),
    OPERATOR_MULTIPLY(4, true, "*"),
    SUBSTITUTION(7, true, "="),
    ARGUMENT_SPLIT(0, true, ",");
    
    
    public final int priority;
    public final String[] words;
    public final boolean adjoinable;
    
    Identifier(int priority, boolean adjoinable, String... words){
        this.priority = priority;
        this.words = words;
        this.adjoinable = adjoinable;
    }
}
