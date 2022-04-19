package org.contan_lang.syntax;

import org.contan_lang.syntax.parser.environment.ScopeType;
import org.jetbrains.annotations.Nullable;

public enum Identifier {
    EXPRESSION_SPLIT(1, true, false, null, "\n", ";"),
    CLASS(14, false, true, ScopeType.CLASS, "class"),
    INITIALIZE(13, false, true, ScopeType.INITIALIZE, "initialize"),
    FUNCTION(13, false, true, ScopeType.FUNCTION, "function"),
    IF(12, false, true, ScopeType.IF, "if"),
    BLOCK_START(12, true, true, null, "{"),
    BLOCK_END(12, true, false, null, "}"),
    BLOCK_OPERATOR_START(3, true, true, null, "("),
    BLOCK_OPERATOR_END(3, true, false, null, ")"),
    DEFINE_DATA(10, false, false, null, "data"),
    RETURN(11, false, false, null, "return"),
    DEFINE_STRING_START_OR_END(2, true, true, null, "\""),
    DOT(2, true, false, null, "."),
    OPERATOR_EQUAL(6, true, false, null, "=="),
    OPERATOR_AND(9, true, false, null, "&&"),
    OPERATOR_PLUS(5, true, false, null, "+"),
    OPERATOR_MULTIPLY(4, true, false, null, "*"),
    SUBSTITUTION(8, true, false, null, "="),
    NEW(7, false, false, null, "new"),
    IMPORT(7, false, false, null, "import"),
    ARGUMENT_SPLIT(0, true, false, null, ","),
    NULL(1, false, false, null, "null", "NULL");
    
    
    public final int priority;
    public final String[] words;
    public final boolean adjoinable;
    public final boolean blockHead;
    public final @Nullable ScopeType scopeType;
    
    Identifier(int priority, boolean adjoinable, boolean blockHead, @Nullable ScopeType scopeType, String... words){
        this.priority = priority;
        this.words = words;
        this.adjoinable = adjoinable;
        this.blockHead = blockHead;
        this.scopeType = scopeType;
    }
}
