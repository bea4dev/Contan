package org.contan_lang.operators.primitives;

import org.contan_lang.environment.Environment;
import org.contan_lang.environment.EnvironmentVariable;
import org.contan_lang.operators.Operator;
import org.contan_lang.variables.ContanVariable;

public class GetValueOperator extends Operator {
    
    private final String targetVariableName;
    
    public GetValueOperator(String targetVariableName, Operator... operators) {
        super(operators);
        this.targetVariableName = targetVariableName;
    }
    
    public String getTargetVariableName() {return targetVariableName;}
    
    @Override
    public ContanVariable<?> eval(Environment environment) {
        EnvironmentVariable variable = environment.getVariable(targetVariableName);
        return variable.getContanVariable().createClone();
    }
    
}
