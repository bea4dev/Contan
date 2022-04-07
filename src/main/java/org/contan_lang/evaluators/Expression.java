package org.contan_lang.evaluators;

import org.contan_lang.environment.Environment;
import org.contan_lang.operators.Operator;
import org.contan_lang.variables.ContanVariable;

public class Expression implements Evaluator {
    
    protected final Operator operator;
    
    public Expression(Operator operator) {
        this.operator = operator;
    }
    
    @Override
    public ContanVariable<?> eval(Environment environment) {
        return operator.eval(environment);
    }
    
}
