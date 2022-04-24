package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.ContanVariableReference;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.operators.Operator;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanVariable;
import org.contan_lang.variables.primitive.ContanVoid;

public class SetValueOperator extends Operator {
    
    public SetValueOperator(ContanEngine contanEngine, Token token, Evaluator... operators) {
        super(contanEngine, token, operators);
    }
    
    @Override
    public ContanVariable<?> eval(Environment environment) {
        ContanVariable<?> variable = operators[0].eval(environment);
        
        if (!(variable instanceof ContanVariableReference)) {
            ContanRuntimeError.E0003.throwError("", null, token);
            return null;
        }
        
        try {
            ((ContanVariableReference) variable).setContanVariable(operators[1].eval(environment));
        } catch (IllegalAccessException e) {
            ContanRuntimeError.E0012.throwError("", e, token);
        }
        
        return ContanVoid.INSTANCE;
    }
}
