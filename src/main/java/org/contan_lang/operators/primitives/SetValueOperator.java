package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.ContanObjectReference;
import org.contan_lang.environment.CoroutineStatus;
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

public class SetValueOperator extends Operator {
    
    public SetValueOperator(ContanEngine contanEngine, Token token, Evaluator... operators) {
        super(contanEngine, token, operators);
    }
    
    @Override
    public ContanObject<?> eval(Environment environment) {
        ContanObject<?> contanObject0;
        ContanObject<?> contanObject1;
    
        CoroutineStatus coroutineStatus = environment.getCoroutineStatus(this);
        
        if (coroutineStatus == null) {
            contanObject0 = operators[0].eval(environment);
            if (environment.hasYieldReturnValue() || contanObject0 == ContanYieldObject.INSTANCE) {
                return ContanYieldObject.INSTANCE;
            }
        } else {
            contanObject0 = coroutineStatus.results[0];
        }
    
        contanObject1 = operators[1].eval(environment);
        if (environment.hasYieldReturnValue() || contanObject1 == ContanYieldObject.INSTANCE) {
            environment.setCoroutineStatus(this, 0, contanObject0);
            return ContanYieldObject.INSTANCE;
        }

        if (!(contanObject0 instanceof ContanObjectReference)) {
            ContanRuntimeError.E0003.throwError("", null, token);
            return null;
        }

        try {
            ContanObject<?> rightResult = ContanRuntimeUtil.removeReference(token, contanObject1);
            ContanObject<?> resultClone = rightResult.createClone();

            ContanObjectReference reference = (ContanObjectReference) contanObject0;
            if (reference.isConst()) {
                ContanRuntimeError.E0023.throwError("", null, token);
            }
            
            
            reference.setContanObject(resultClone);
            
            return resultClone;
            
        } catch (ContanRuntimeException e) {
            throw e;
        } catch (Exception e) {
            ContanRuntimeError.E0012.throwError("", e, token);
            return ContanVoidObject.INSTANCE;
        }
    }
}
