package org.contan_lang.syntax;

import org.contan_lang.syntax.parser.environment.ScopeType;
import org.jetbrains.annotations.Nullable;

public enum Identifier {
    EXPRESSION_SPLIT(1, true, false, null, "\n", ";"),
    CLASS(13, false, true, ScopeType.CLASS, "class"),
    INITIALIZE(14, false, true, ScopeType.INITIALIZE, "initialize"),
    FUNCTION(8, false, true, ScopeType.FUNCTION, "function"),
    IF(13, false, true, ScopeType.IF, "if"),
    BLOCK_START(0, true, true, null, "{"),
    BLOCK_END(0, true, false, null, "}"),
    BLOCK_OPERATOR_START(0, true, true, null, "("),
    BLOCK_OPERATOR_END(0, true, false, null, ")"),
    DEFINE_VARIABLE(11, false, false, null, "data"),
    RETURN(12, false, false, null, "return"),
    ASYNC(9, false, false, null, "async"),
    AWAIT(9, false, false, null, ".await()"),
    DOT(3, true, false, null, "."),
    OPERATOR_EQUAL(6, true, false, null, "=="),
    OPERATOR_AND(7, true, false, null, "&&"),
    OPERATOR_PLUS(5, true, false, null, "+"),
    OPERATOR_MULTIPLY(4, true, false, null, "*"),
    LAMBDA(8, true, false, ScopeType.FUNCTION, "=>"),
    ASSIGNMENT(10, true, false, null, "="),
    NEW(3, false, false, null, "new"),
    IMPORT(11, false, false, null, "import"),
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
