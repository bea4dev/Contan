package org.contan_lang.evaluators;

import org.contan_lang.environment.Environment;
import org.contan_lang.variables.ContanVariable;
import org.contan_lang.variables.primitive.ContanVoid;

public class Expressions implements Evaluator {
    
    private final Evaluator[] expressions;
    
    public Expressions(Evaluator... expressions) {
        this.expressions = expressions;
    }
    
    public Evaluator[] getExpressions() {return expressions;}
    
    @Override
    public ContanVariable<?> eval(Environment environment) {
        for (Evaluator evaluator : expressions) {
            evaluator.eval(environment);
            
            if (environment.hasReturnValue()) {
                return ContanVoid.INSTANCE;
            }
        }
        
        return ContanVoid.INSTANCE;
    }
    
}
