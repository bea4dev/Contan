package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.variables.ContanVariable;
import org.contan_lang.variables.primitive.ContanBoolean;

public class EqualOperator extends BooleanOperator {
    
    public EqualOperator(ContanEngine contanEngine, Evaluator... operators) {
        super(contanEngine, operators);
    }
    
    @Override
    public ContanVariable<Boolean> eval(Environment environment) {
        boolean same = true;
        if (operators.length > 1) {
            Object previous = operators[0].eval(environment).getBasedJavaObject();
            
            for (int i = 1; i < operators.length; i++) {
                Object based = operators[i].eval(environment).getBasedJavaObject();
                if (!previous.equals(based)) {
                    same = false;
                    break;
                }
            }
        }
        
        return new ContanBoolean(same);
    }
    
}
