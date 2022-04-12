package org.contan_lang.standard.functions;

import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.evaluators.FunctionBlock;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanVariable;
import org.contan_lang.variables.primitive.ContanVoid;

public class Print extends FunctionBlock {
    
    public Print(Token functionName, Evaluator evaluator, Token... args) {
        super(functionName, evaluator, args);
    }
    
    @Override
    public ContanVariable<?> eval(Environment environment, ContanVariable<?>... contanVariables) {
        for (ContanVariable<?> variable : contanVariables) {
            if (variable instanceof ContanVoid) {
                System.out.println("NULL");
                continue;
            }
            
            System.out.println(variable.getBasedJavaObject());
        }
        return ContanVoid.INSTANCE;
    }
    
}
