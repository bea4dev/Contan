package org.contan_lang.operators;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.variables.ContanVariable;

public abstract class Operator implements Evaluator {

    protected final ContanEngine contanEngine;
    
    protected final Evaluator[] operators;
    
    public Operator(ContanEngine contanEngine, Evaluator... operators) {
        this.contanEngine = contanEngine;
        this.operators = operators;
    }

    @Override
    public abstract ContanVariable<?> eval(Environment environment);
    
}
