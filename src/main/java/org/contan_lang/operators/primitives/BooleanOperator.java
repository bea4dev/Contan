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
import org.contan_lang.variables.primitive.ContanVoidObject;
import org.contan_lang.variables.primitive.ContanYieldObject;

public class BooleanOperator extends BooleanBaseOperator {
    
    private final Identifier operatorType;
    
    public BooleanOperator(ContanEngine contanEngine, Token token, Identifier operatorType, Evaluator... operators) {
        super(contanEngine, token, operators);
        this.operatorType = operatorType;
    }
    
    @Override
    public ContanObject<Boolean> eval(Environment environment) {
        if (super.evalLeftAndRight(environment) == ContanYieldObject.INSTANCE) {
            return ContanYieldObject.INSTANCE;
        }
    
        contanObject0 = ContanRuntimeUtil.removeReference(token, contanObject0);
        contanObject1 = ContanRuntimeUtil.removeReference(token, contanObject1);
    
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
                return ContanVoidObject.INSTANCE;
            }
        }
    }
    
}
