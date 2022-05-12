package org.contan_lang.evaluators;

import org.contan_lang.environment.CancelStatus;
import org.contan_lang.environment.CoroutineStatus;
import org.contan_lang.environment.Environment;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanVoidObject;
import org.contan_lang.variables.primitive.ContanYieldObject;

public class Expressions implements Evaluator {
    
    private final Evaluator[] expressions;
    
    public Expressions(Evaluator... expressions) {
        this.expressions = expressions;
    }
    
    @Override
    public ContanObject<?> eval(Environment environment) {
        if (environment.isCoroutineEnvironment()) {
            CoroutineStatus coroutineStatus = environment.getCoroutineStatus(this);
            if (coroutineStatus != null) {
                return eval(environment, (int) coroutineStatus.count);
            }
        }
        return eval(environment, 0);
    }
    
    public ContanObject<?> eval(Environment environment, int start) {
        for (int i = start; i < expressions.length; i ++) {
            Evaluator evaluator = expressions[i];

            ContanObject<?> result;
            result = evaluator.eval(environment);

            if (environment.hasYieldReturnValue()) {
                environment.setCoroutineStatus(this, i, ContanYieldObject.INSTANCE);
                return ContanYieldObject.INSTANCE;
            }

            if (environment.getCancelStatus() != CancelStatus.NONE) {
                return ContanVoidObject.INSTANCE;
            }

            if (i == expressions.length - 1) {
                return result;
            }
            
            if (environment.hasReturnValue()) {
                return ContanVoidObject.INSTANCE;
            }
        }
        
        return ContanVoidObject.INSTANCE;
    }
    
}
