package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.operators.Operator;
import org.contan_lang.runtime.ContanRuntimeUtil;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanFloat;
import org.contan_lang.variables.primitive.ContanInteger;
import org.contan_lang.variables.primitive.ContanYieldObject;

public class InvertSignOperator extends Operator {

    public InvertSignOperator(ContanEngine contanEngine, Token token, Evaluator... operators) {
        super(contanEngine, token, operators);
    }

    @Override
    public ContanObject<?> eval(Environment environment) {
        ContanObject<?> rightResult = operators[0].eval(environment);
        rightResult = ContanRuntimeUtil.removeReference(token, rightResult);

        if (environment.hasYieldReturnValue() || rightResult == ContanYieldObject.INSTANCE) {
            return ContanYieldObject.INSTANCE;
        }

        Object based = rightResult.getBasedJavaObject();
        if (based instanceof Integer) {
            return new ContanInteger(contanEngine, (long) ((Integer) based) * -1L);
        } else if (based instanceof Long) {
            return new ContanInteger(contanEngine, (Long) based * -1L);
        } else if (based instanceof Float) {
            return new ContanFloat(contanEngine, (double) ((Float) based) * -1.0);
        } else if (based instanceof Double) {
            return new ContanFloat(contanEngine, (Double) based * -1.0);
        }

        ContanRuntimeError.E0021.throwError("", null, token);
        return null;
    }

}
