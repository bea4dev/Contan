package org.contan_lang.evaluators;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.CoroutineStatus;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanVoidObject;
import org.contan_lang.variables.primitive.ContanYieldObject;
import org.jetbrains.annotations.Nullable;

public class IfEvaluator implements Evaluator {

    private final ContanEngine contanEngine;
    private final Token token;
    private final Evaluator termsEvaluator;
    private final Evaluator trueExpression;
    
    private Evaluator linkedElseEvaluator = null;
    
    public IfEvaluator(ContanEngine contanEngine, Token token, Evaluator termsEvaluator, @Nullable Evaluator trueExpression) {
        this.contanEngine = contanEngine;
        this.token = token;
        this.termsEvaluator = termsEvaluator;
        this.trueExpression = trueExpression;
    }
    
    public void setLinkedElseEvaluator(Evaluator linkedElseEvaluator) {
        this.linkedElseEvaluator = linkedElseEvaluator;
    }
    
    @Override
    public ContanObject<?> eval(Environment environment) {

        ContanObject<?> termResult;
        CoroutineStatus coroutineStatus = environment.getCoroutineStatus(this);

        if (coroutineStatus == null) {
            termResult = termsEvaluator.eval(environment);

            if (!(termResult.getBasedJavaObject() instanceof Boolean)) {
                ContanRuntimeError.E0024.throwError("", null, token);
            }
        } else {
            termResult = coroutineStatus.results[0];
        }

        Boolean bool = (Boolean) termResult.getBasedJavaObject();
        
        if (bool) {
            if(trueExpression != null) {
                Environment nestedEnv = new Environment(contanEngine, environment, environment.getContanThread());
                ContanObject<?> result = trueExpression.eval(nestedEnv);

                if (environment.hasYieldReturnValue() || result == ContanYieldObject.INSTANCE) {
                    environment.setCoroutineStatus(this, 0, termResult);
                    return ContanVoidObject.INSTANCE;
                }
            }
        } else {
            if (linkedElseEvaluator != null) {
                ContanObject<?> result = linkedElseEvaluator.eval(environment);

                if (environment.hasYieldReturnValue() || result == ContanYieldObject.INSTANCE) {
                    environment.setCoroutineStatus(this, 0, termResult);
                    return ContanVoidObject.INSTANCE;
                }
            }
        }
        
        return ContanVoidObject.INSTANCE;
    }
    
}
