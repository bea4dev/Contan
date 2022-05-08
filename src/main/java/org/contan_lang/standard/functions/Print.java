package org.contan_lang.standard.functions;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.evaluators.FunctionBlock;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanVoidObject;
import org.contan_lang.variables.primitive.ContanYieldObject;

public class Print extends FunctionBlock {
    
    public Print(ContanEngine contanEngine, Token functionName, Evaluator evaluator, Token... args) {
        super(contanEngine, functionName, evaluator, args);
    }
    
    @Override
    public ContanObject<?> eval(Environment environment, Token token, ContanThread contanThread, ContanObject<?>... contanObjects) {
        for (ContanObject<?> variable : contanObjects) {
            if (variable instanceof ContanVoidObject) {
                System.out.println("NULL");
                continue;
            }
            
            if (variable instanceof ContanYieldObject) {
                System.out.println("YIELD_OBJECT");
                continue;
            }
            
            System.out.println(variable.getBasedJavaObject());
        }
        return ContanVoidObject.INSTANCE;
    }
    
}
