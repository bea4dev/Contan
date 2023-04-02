package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.CoroutineStatus;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.runtime.ContanRuntimeUtil;
import org.contan_lang.syntax.Identifier;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanBoolean;
import org.contan_lang.variables.primitive.ContanYieldObject;

public class BooleanOperator extends BooleanBaseOperator {
    
    private final Identifier operatorType;
    
    public BooleanOperator(ContanEngine contanEngine, Token token, Identifier operatorType, Evaluator... operators) {
        super(contanEngine, token, operators);
        this.operatorType = operatorType;
    }
    
    @Override
    public ContanObject<Boolean> eval(Environment environment) {
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
    
        contanObject0 = ContanRuntimeUtil.dereference(token, contanObject0);
        contanObject1 = ContanRuntimeUtil.dereference(token, contanObject1);
    
        Object first = contanObject0.getBasedJavaObject();
        Object second = contanObject1.getBasedJavaObject();
        
        if (!(first instanceof Boolean) || !(second instanceof Boolean)) {
            ContanRuntimeError.E0039.throwError("", null, token);
            return null;
        }
        
        switch (operatorType) {
            case OPERATOR_AND: {
                return new ContanBoolean(contanEngine, (Boolean) first && (Boolean) second);
            }
            
            case OPERATOR_OR: {
                return new ContanBoolean(contanEngine, (Boolean) first || (Boolean) second);
            }
            
            default: {
                ContanRuntimeError.E0000.throwError("", null, token);
                return null;
            }
        }
    }
    
}
