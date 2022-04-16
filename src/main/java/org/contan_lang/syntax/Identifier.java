package org.contan_lang.syntax;

public enum Identifier {
    EXPRESSION_SPLIT(1, true, "\n", ";"),
    CLASS(14, false, "class"),
    INITIALIZE(13, false, "initialize"),
    FUNCTION(13, false, "function"),
    IF(12, false, "if"),
    BLOCK_START(12, true, "{"),
    BLOCK_END(12, true, "}"),
    BLOCK_OPERATOR_START(3, true, "("),
    BLOCK_OPERATOR_END(3, true, ")"),
    DEFINE_DATA(10, false, "data"),
    RETURN(11, false, "return"),
    DEFINE_STRING_START_OR_END(2, true, "\""),
    OPERATOR_EQUAL(6, true, "=="),
    OPERATOR_AND(9, true, "&&"),
    OPERATOR_PLUS(5, true, "+"),
    OPERATOR_MULTIPLY(4, true, "*"),
    SUBSTITUTION(8, true, "="),
    NEW(7, false, "new"),
    IMPORT(7, false, "import"),
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
