package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.operators.Operator;
import org.contan_lang.runtime.ContanRuntimeUtil;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanBoolean;
import org.contan_lang.variables.primitive.ContanYieldObject;

public class NotOperator extends Operator {

    public NotOperator(ContanEngine contanEngine, Token token, Evaluator... operators) {
        super(contanEngine, token, operators);
    }

    @Override
    public ContanObject<?> eval(Environment environment) {
        ContanObject<?> rightResult = operators[0].eval(environment);

        if (environment.hasYieldReturnValue() || rightResult == ContanYieldObject.INSTANCE) {
            return ContanYieldObject.INSTANCE;
        }

        rightResult = ContanRuntimeUtil.dereference(token, rightResult);
        Object based = rightResult.getBasedJavaObject();

        if (!(based instanceof Boolean)) {
            ContanRuntimeError.E0039.throwError("", null, token);
            return null;
        }

        return new ContanBoolean(contanEngine, !(Boolean) based);
    }

}
