package org.contan_lang.operators;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.CoroutineStatus;
import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanYieldObject;

public abstract class Operator implements Evaluator {

    protected final ContanEngine contanEngine;

    protected final Token token;
    
    protected final Evaluator[] operators;

    protected ContanObject<?> contanObject0;
    protected ContanObject<?> contanObject1;
    
    public Operator(ContanEngine contanEngine, Token token, Evaluator... operators) {
        this.contanEngine = contanEngine;
        this.token = token;
        this.operators = operators;
    }

    @Override
    public abstract ContanObject<?> eval(Environment environment);


    protected ContanYieldObject evalLeftAndRight(Environment environment) {
        CoroutineStatus coroutineStatus = environment.getCoroutineStatus(this);

        if (coroutineStatus == null) {
            contanObject0 = operators[0].eval(environment);
            if (environment.hasYieldReturnValue() || contanObject0 == ContanYieldObject.INSTANCE) {
                environment.setCoroutineStatus(this, 0, ContanYieldObject.INSTANCE);
                environment.setReturnValue(ContanYieldObject.INSTANCE);
                return ContanYieldObject.INSTANCE;
            }

            contanObject1 = operators[1].eval(environment);
            if (environment.hasYieldReturnValue() || contanObject1 == ContanYieldObject.INSTANCE) {
                environment.setCoroutineStatus(this, 1, contanObject0);
                environment.setReturnValue(ContanYieldObject.INSTANCE);
                return ContanYieldObject.INSTANCE;
            }
        } else {
            if (coroutineStatus.count == 0) {
                contanObject0 = operators[0].eval(environment);
            } else {
                contanObject0 = coroutineStatus.results[0];
            }
            contanObject1 = operators[1].eval(environment);
        }

        return null;
    }
    
}
