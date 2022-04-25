package org.contan_lang.evaluators;

import org.contan_lang.environment.Environment;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanVoid;

public class Expressions implements Evaluator {
    
    private final Evaluator[] expressions;
    
    public Expressions(Evaluator... expressions) {
        this.expressions = expressions;
    }
    
    @Override
    public ContanObject<?> eval(Environment environment) {
        for (int i = 0; i < expressions.length; i ++) {
            Evaluator evaluator = expressions[i];

            if (i == expressions.length - 1) {
                return evaluator.eval(environment);
            } else {
                evaluator.eval(environment);
            }

            if (environment.hasReturnValue()) {
                return ContanVoid.INSTANCE;
            }
        }

        return ContanVoid.INSTANCE;
    }
    
}
