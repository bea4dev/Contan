package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.operators.Operator;
import org.contan_lang.variables.ContanVariable;
import org.contan_lang.variables.primitive.ContanVoid;

public class CreateVariableOperator extends Operator {
    
    protected final String variableName;
    
    public CreateVariableOperator(ContanEngine contanEngine, String variableName) {
        super(contanEngine);
        this.variableName = variableName;
    }
    
    public String getVariableName() {return variableName;}
    
    @Override
    public ContanVariable<?> eval(Environment environment) {
        environment.createVariable(variableName, ContanVoid.INSTANCE);
        return ContanVoid.INSTANCE;
    }
    
}
