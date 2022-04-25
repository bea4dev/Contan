package org.contan_lang.standard.functions;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.evaluators.FunctionBlock;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanVoid;

public class Print extends FunctionBlock {
    
    public Print(ContanEngine contanEngine, Token functionName, Evaluator evaluator, Token... args) {
        super(contanEngine, functionName, evaluator, args);
    }
    
    @Override
    public ContanObject<?> eval(Environment environment, Token token, ContanObject<?>... contanObjects) {
        for (ContanObject<?> variable : contanObjects) {
            if (variable instanceof ContanVoid) {
                System.out.println("NULL");
                continue;
            }
            
            System.out.println(variable.getBasedJavaObject());
        }
        return ContanVoid.INSTANCE;
    }
    
}
