package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.ContanObjectReference;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.environment.expection.ContanRuntimeException;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.operators.Operator;
import org.contan_lang.runtime.ContanRuntimeUtil;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanVoidObject;
import org.contan_lang.variables.primitive.ContanYieldObject;

public class ExchangeOperator extends Operator {
    public ExchangeOperator(ContanEngine contanEngine, Token token, Evaluator... operators) {
        super(contanEngine, token, operators);
    }

    @Override
    public ContanObject<?> eval(Environment environment) {
        if (super.evalLeftAndRight(environment) == ContanYieldObject.INSTANCE) {
            return ContanYieldObject.INSTANCE;
        }

        if (!(contanObject0 instanceof ContanObjectReference) || !(contanObject1 instanceof ContanObjectReference)) {
            ContanRuntimeError.E0044.throwError("", null, token);
            return null;
        }

        if (((ContanObjectReference) contanObject0).isConst() || ((ContanObjectReference) contanObject1).isConst()) {
            ContanRuntimeError.E0044.throwError("", null, token);
            return null;
        }

        ContanObject<?> value0 = ContanRuntimeUtil.removeReference(token, contanObject0);
        ContanObject<?> value1 = ContanRuntimeUtil.removeReference(token, contanObject1);

        try {
            ((ContanObjectReference) contanObject0).setContanObject(value1);
            ((ContanObjectReference) contanObject1).setContanObject(value0);
        } catch (ContanRuntimeException e) {
            throw e;
        } catch (Exception e) {
            ContanRuntimeError.E0012.throwError("", e, token);
            return ContanVoidObject.INSTANCE;
        }

        return value1;
    }
}
