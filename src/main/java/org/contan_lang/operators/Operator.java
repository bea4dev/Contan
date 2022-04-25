package org.contan_lang.operators;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;

public abstract class Operator implements Evaluator {

    protected final ContanEngine contanEngine;

    protected final Token token;
    
    protected final Evaluator[] operators;
    
    public Operator(ContanEngine contanEngine, Token token, Evaluator... operators) {
        this.contanEngine = contanEngine;
        this.token = token;
        this.operators = operators;
    }

    @Override
    public abstract ContanObject<?> eval(Environment environment);
    
}
