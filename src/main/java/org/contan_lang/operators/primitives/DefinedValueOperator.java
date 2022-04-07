package org.contan_lang.operators.primitives;

import org.contan_lang.environment.Environment;
import org.contan_lang.operators.Operator;
import org.contan_lang.variables.ContanVariable;

public class DefinedValueOperator extends Operator {
    
    private final ContanVariable<?> value;
    
    public DefinedValueOperator(ContanVariable<?> value) {
        super();
        this.value = value;
    }
    
    public ContanVariable<?> getValue() {return value;}
    
    @Override
    public ContanVariable<?> eval(Environment environment) {
        return value;
    }
    
}
