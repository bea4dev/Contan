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

public class SetValueOperator extends Operator {
    
    public SetValueOperator(ContanEngine contanEngine, Token token, Evaluator... operators) {
        super(contanEngine, token, operators);
    }
    
    @Override
    public ContanObject<?> eval(Environment environment) {
        ContanObject<?> variable = operators[0].eval(environment);
        
        if (!(variable instanceof ContanObjectReference)) {
            ContanRuntimeError.E0003.throwError("", null, token);
            return null;
        }
        
        try {
            ContanObject<?> rightResult = operators[1].eval(environment);
            rightResult = ContanRuntimeUtil.removeReference(token, rightResult);
    
            if (environment.hasYieldReturnValue() || rightResult == ContanYieldObject.INSTANCE) {
                return ContanYieldObject.INSTANCE;
            }

            ContanObject<?> resultClone = rightResult.createClone();

            ContanObjectReference reference = (ContanObjectReference) variable;
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
