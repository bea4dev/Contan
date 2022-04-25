package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.operators.Operator;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;

public class DefinedValueOperator extends Operator {
    
    private final ContanObject<?> value;
    
    public DefinedValueOperator(ContanEngine contanEngine, Token token, ContanObject<?> value) {
        super(contanEngine, token);
        this.value = value;
    }
    
    public ContanObject<?> getValue() {return value;}
    
    @Override
    public ContanObject<?> eval(Environment environment) {
        return value;
    }
    
}
