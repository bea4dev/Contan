package org.contan_lang.evaluators;

import org.contan_lang.environment.Environment;
import org.contan_lang.operators.primitives.BooleanOperator;
import org.contan_lang.variables.ContanVariable;

public class BooleanExpression implements Evaluator {
    
    protected final BooleanOperator operator;
    
    public BooleanExpression(BooleanOperator operator) {
        this.operator = operator;
    }
    
    @Override
    public ContanVariable<Boolean> eval(Environment environment) {
        return operator.eval(environment);
    }
    
}