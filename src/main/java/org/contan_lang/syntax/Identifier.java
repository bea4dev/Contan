package org.contan_lang.syntax;

public enum Identifier {
    EXPRESSION_SPLIT(1, true, "\n", ";"),
    CLASS(13, false, "class"),
    INITIALIZE(12, false, "initialize"),
    FUNCTION(12, false, "function"),
    IF(11, false, "if"),
    BLOCK_START(11, true, "{"),
    BLOCK_END(11, true, "}"),
    BLOCK_OPERATOR_START(3, true, "("),
    BLOCK_OPERATOR_END(3, true, ")"),
    DEFINE_DATA(10, false, "data"),
    DEFINE_STRING_START_OR_END(2, true, "\""),
    OPERATOR_EQUAL(6, true, "=="),
    OPERATOR_AND(9, true, "&&"),
    OPERATOR_PLUS(5, true, "+"),
    OPERATOR_MULTIPLY(4, true, "*"),
    SUBSTITUTION(8, true, "="),
    NEW(7, false, "new"),
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
}
