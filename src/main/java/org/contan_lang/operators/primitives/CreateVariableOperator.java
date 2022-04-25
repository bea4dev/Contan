package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.operators.Operator;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanVoid;

public class CreateVariableOperator extends Operator {
    
    protected final String variableName;
    
    public CreateVariableOperator(ContanEngine contanEngine, Token token, Evaluator... operator) {
        super(contanEngine, token, operator);
        this.variableName = token.getText();
    }
    
    public String getVariableName() {return variableName;}
    
    @Override
    public ContanObject<?> eval(Environment environment) {
        environment.createVariable(variableName, ContanVoid.INSTANCE);
        return ContanVoid.INSTANCE;
    }
    
}
