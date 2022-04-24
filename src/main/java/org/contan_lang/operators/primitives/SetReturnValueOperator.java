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

public class SetReturnValueOperator extends Operator {

    public SetReturnValueOperator(ContanEngine contanEngine, Token token, Evaluator... operators) {
        super(contanEngine, token, operators);
    }

    @Override
    public ContanVariable<?> eval(Environment environment) {
        ContanVariable<?> variable = operators[0].eval(environment);
        if (variable instanceof ContanVariableReference) {
            try {
                variable = ((ContanVariableReference) variable).getContanVariable();
            } catch (IllegalAccessException e) {
                ContanRuntimeError.E0013.throwError("", e, token);
            }
        }
        
        environment.setReturnValue(variable);
        return ContanVoid.INSTANCE;
    }
}
