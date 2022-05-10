package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.operators.Operator;
import org.contan_lang.runtime.ContanRuntimeUtil;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanYieldObject;

public class CreateConstVariableOperator extends Operator {

    public CreateConstVariableOperator(ContanEngine contanEngine, Token token, Evaluator... operators) {
        super(contanEngine, token, operators);
    }

    @Override
    public ContanObject<?> eval(Environment environment) {
        ContanObject<?> rightResult = operators[0].eval(environment);
        rightResult = ContanRuntimeUtil.removeReference(token, rightResult);

        if (environment.hasYieldReturnValue() || rightResult == ContanYieldObject.INSTANCE) {
            return ContanYieldObject.INSTANCE;
        }

        environment.createConstVariable(token.getText(), rightResult);

        return rightResult;
    }

}
