package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.operators.Operator;
import org.contan_lang.runtime.ContanRuntimeUtil;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanVoidObject;
import org.contan_lang.variables.primitive.ContanYieldObject;

public class SetReturnValueOperator extends Operator {

    public SetReturnValueOperator(ContanEngine contanEngine, Token token, Evaluator... operators) {
        super(contanEngine, token, operators);
    }

    @Override
    public ContanObject<?> eval(Environment environment) {
        ContanObject<?> variable = operators[0].eval(environment);
        variable = ContanRuntimeUtil.dereference(token, variable);
    
        if (environment.hasYieldReturnValue() || variable == ContanYieldObject.INSTANCE) {
            return ContanYieldObject.INSTANCE;
        }
        
        environment.setReturnValue(variable);
        return ContanVoidObject.INSTANCE;
    }
}
