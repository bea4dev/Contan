package org.contan_lang.evaluators;

import org.contan_lang.environment.Environment;
import org.contan_lang.variables.ContanVariable;
import org.contan_lang.variables.primitive.ContanVoid;
import org.jetbrains.annotations.Nullable;

public class IfEvaluator implements Evaluator {
    
    private final Evaluator termsEvaluator;
    private final Evaluator trueExpression;
    
    private IfEvaluator linkedElseEvaluator = null;
    
    public IfEvaluator(Evaluator termsEvaluator, @Nullable Evaluator trueExpression) {
        this.termsEvaluator = termsEvaluator;
        this.trueExpression = trueExpression;
    }
    
    public void setLinkedElseEvaluator(IfEvaluator linkedElseEvaluator) {
        this.linkedElseEvaluator = linkedElseEvaluator;
    }
    
    @Override
    public ContanVariable<?> eval(Environment environment) {
        Boolean bool = (Boolean) termsEvaluator.eval(environment).getBasedJavaObject();
        
        if (bool) {
            if(trueExpression != null) {
                Environment nestedEnv = new Environment(environment);
                trueExpression.eval(nestedEnv);
            }
        } else {
            if (linkedElseEvaluator != null) {
                linkedElseEvaluator.eval(environment);
            }
        }
        
        return ContanVoid.INSTANCE;
    }
    
}
