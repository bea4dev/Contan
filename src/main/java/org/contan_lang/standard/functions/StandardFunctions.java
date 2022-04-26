package org.contan_lang.standard.functions;

import org.contan_lang.evaluators.FunctionBlock;
import org.contan_lang.syntax.tokens.Token;

import java.util.HashMap;
import java.util.Map;

public class StandardFunctions {
    
    public static final Map<String, FunctionBlock> FUNCTIONS = new HashMap<>();
    
    static {
        FUNCTIONS.put("print", new Print(null, new Token(null, "print", 5, null, null), null));
    }
    
}
