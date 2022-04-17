package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.operators.Operator;
import org.contan_lang.variables.ContanVariable;

public abstract class BooleanOperator extends Operator {
    
    public BooleanOperator(ContanEngine contanEngine, Evaluator... operators) {
        super(contanEngine, operators);
    }
    
    @Override
    public abstract ContanVariable<Boolean> eval(Environment environment);
    
}
