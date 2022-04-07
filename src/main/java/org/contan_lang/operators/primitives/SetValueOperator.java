package org.contan_lang.operators.primitives;

import org.contan_lang.environment.Environment;
import org.contan_lang.environment.EnvironmentVariable;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.operators.Operator;
import org.contan_lang.variables.ContanVariable;
import org.contan_lang.variables.primitive.ContanVoid;

public class SetValueOperator extends Operator {
    
    private final String targetVariableName;
    private final Evaluator valueEval;
    
    public SetValueOperator(String targetVariableName, Evaluator valueEval) {
        super();
        this.targetVariableName = targetVariableName;
        this.valueEval = valueEval;
    }
    
    public String getTargetVariableName() {return targetVariableName;}
    
    public Evaluator getValueEval() {return valueEval;}
    
    @Override
    public ContanVariable<?> eval(Environment environment) {
        EnvironmentVariable variable = environment.getVariable(targetVariableName);
        variable.setContanVariable(valueEval.eval(environment));
        
        return ContanVoid.INSTANCE;
    }
}
