package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.operators.Operator;
import org.contan_lang.runtime.ContanRuntimeUtil;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanF64;
import org.contan_lang.variables.primitive.ContanI64;
import org.contan_lang.variables.primitive.ContanYieldObject;

public class InvertSignOperator extends Operator {

    public InvertSignOperator(ContanEngine contanEngine, Token token, Evaluator... operators) {
        super(contanEngine, token, operators);
    }

    @Override
    public ContanObject<?> eval(Environment environment) {
        ContanObject<?> rightResult = operators[0].eval(environment);
        rightResult = ContanRuntimeUtil.dereference(token, rightResult);

        if (environment.hasYieldReturnValue() || rightResult == ContanYieldObject.INSTANCE) {
            return ContanYieldObject.INSTANCE;
        }

        if (rightResult.convertibleToLong()) {
            return new ContanI64(contanEngine, rightResult.toLong() * -1);
        } else if (rightResult.convertibleToDouble()) {
            return new ContanF64(contanEngine, rightResult.toDouble() * -1.0);
        }

        ContanRuntimeError.E0021.throwError("", null, token);
        return null;
    }

}
