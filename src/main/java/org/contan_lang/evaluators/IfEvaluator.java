package org.contan_lang.evaluators;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanVoidObject;
import org.jetbrains.annotations.Nullable;

public class IfEvaluator implements Evaluator {

    private final ContanEngine contanEngine;
    private final Evaluator termsEvaluator;
    private final Evaluator trueExpression;
    
    private IfEvaluator linkedElseEvaluator = null;
    
    public IfEvaluator(ContanEngine contanEngine, Evaluator termsEvaluator, @Nullable Evaluator trueExpression) {
        this.contanEngine = contanEngine;
        this.termsEvaluator = termsEvaluator;
        this.trueExpression = trueExpression;
    }
    
    public void setLinkedElseEvaluator(IfEvaluator linkedElseEvaluator) {
        this.linkedElseEvaluator = linkedElseEvaluator;
    }
    
    @Override
    public ContanObject<?> eval(Environment environment) {
        Boolean bool = (Boolean) termsEvaluator.eval(environment).getBasedJavaObject();
        
        if (bool) {
            if(trueExpression != null) {
                Environment nestedEnv = new Environment(contanEngine, environment, environment.getContanThread());
                trueExpression.eval(nestedEnv);
            }
        } else {
            if (linkedElseEvaluator != null) {
                linkedElseEvaluator.eval(environment);
            }
        }
        
        return ContanVoidObject.INSTANCE;
    }
    
}
