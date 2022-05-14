package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.operators.Operator;
import org.contan_lang.runtime.ContanRuntimeUtil;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.thread.ContanTickBasedThread;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanYieldObject;

public class DelayOperator extends Operator {
    
    public DelayOperator(ContanEngine contanEngine, Token token, Evaluator... operators) {
        super(contanEngine, token, operators);
    }
    
    @Override
    public ContanObject<?> eval(Environment environment) {
        ContanThread contanThread = environment.getContanThread();
        if (!(contanThread instanceof ContanTickBasedThread)) {
            ContanRuntimeError.E0032.throwError("", null, token);
            return null;
        }
        
        
        ContanObject<?> delayTicksResult = operators[0].eval(environment);
        delayTicksResult = ContanRuntimeUtil.removeReference(token, delayTicksResult);
        
        if (environment.hasYieldReturnValue()) {
            return ContanYieldObject.INSTANCE;
        }
        
        long delay;
        if (delayTicksResult.convertibleToLong()) {
            delay = delayTicksResult.asLong();
        } else {
            ContanRuntimeError.E0031.throwError("", null, token);
            return null;
        }
        
        
        ContanTickBasedThread tickBasedThread = (ContanTickBasedThread) contanThread;
        
        tickBasedThread.scheduleTask(() -> {
            Environment returnEnv = environment.getReturnEnvironment();
            if (returnEnv != null) {
                returnEnv.rerun();
            }
            
            return null;
        }, delay);
        environment.setReturnValue(ContanYieldObject.INSTANCE);
        
        return ContanYieldObject.INSTANCE;
    }
    
}
