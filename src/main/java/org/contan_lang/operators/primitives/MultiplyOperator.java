package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.CoroutineStatus;
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

public class MultiplyOperator extends Operator {
    
    public MultiplyOperator(ContanEngine contanEngine, Token token, Evaluator... operators) {
        super(contanEngine, token, operators);
    }
    
    @Override
    public ContanObject<?> eval(Environment environment) {
        if (super.evalLeftAndRight(environment) == ContanYieldObject.INSTANCE) {
            return ContanYieldObject.INSTANCE;
        }
    
        contanObject0 = ContanRuntimeUtil.removeReference(token, contanObject0);
        contanObject1 = ContanRuntimeUtil.removeReference(token, contanObject1);
    
        if (contanObject0.convertibleToLong()) {
            long left = contanObject0.toLong();

            if (contanObject1.convertibleToLong()) {
                long right = contanObject1.toLong();

                return new ContanI64(contanEngine, left * right);
            } else if (contanObject1.convertibleToDouble()) {
                double right = contanObject1.toDouble();

                return new ContanF64(contanEngine, (double) left * right);
            }
        } else if (contanObject0.convertibleToDouble()) {
            double left = contanObject0.toDouble();

            if (contanObject1.convertibleToLong() || contanObject1.convertibleToDouble()) {
                return new ContanF64(contanEngine, left * contanObject1.toDouble());
            }
        }
    
        ContanRuntimeError.E0002.throwError("\nLeft : " + contanObject0.toString()
                                                 + " | Right : " + contanObject1.toString(), null, token);
        return null;
    }
}
