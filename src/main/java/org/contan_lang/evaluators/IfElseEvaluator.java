package org.contan_lang.evaluators;

import org.contan_lang.environment.Environment;
import org.contan_lang.variables.ContanVariable;
import org.contan_lang.variables.primitive.ContanVoid;
import org.jetbrains.annotations.Nullable;

public class IfElseEvaluator implements Evaluator {
    
    private final Evaluator equalEvaluator;
    private final Evaluator trueExpression;
    private final Evaluator falseExpression;
    
    public IfElseEvaluator(Evaluator equalEvaluator, @Nullable Evaluator trueExpression, @Nullable Evaluator falseExpression) {
        this.equalEvaluator = equalEvaluator;
        this.trueExpression = trueExpression;
        this.falseExpression = falseExpression;
    }
    
    @Override
    public ContanVariable<?> eval(Environment environment) {
        Boolean bool = (Boolean) equalEvaluator.eval(environment).getBasedJavaObject();
        
        if (bool) {
            if(trueExpression != null) {
                Environment nestedEnv = new Environment(environment);
                trueExpression.eval(nestedEnv);
            }
        } else {
            if(falseExpression != null) {
                Environment nestedEnv = new Environment(environment);
                falseExpression.eval(nestedEnv);
            }
        }
        
        return ContanVoid.INSTANCE;
    }
    
}
