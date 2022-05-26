package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.CoroutineStatus;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.runtime.ContanRuntimeUtil;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.thread.ContanTickBasedThread;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanYieldObject;

public class DelayTaskOperator extends TaskOperator {
    
    public DelayTaskOperator(ContanEngine contanEngine, Token token, Evaluator... operators) {
        super(contanEngine, token, operators);
    }
    
    @Override
    public ContanObject<?> runTask(Environment environment) {
        CoroutineStatus coroutineStatus = environment.getCoroutineStatus(this);
    
        Environment newEnvironment;
    
        ContanThread contanThread = environment.getContanThread();
        if (!(contanThread instanceof ContanTickBasedThread)) {
            ContanRuntimeError.E0032.throwError("", null, token);
            return null;
        }
        
        ContanTickBasedThread tickBasedThread = (ContanTickBasedThread) contanThread;
    
        if (coroutineStatus == null) {
            ContanObject<?> delayResult = operators[0].eval(environment);
            delayResult = ContanRuntimeUtil.removeReference(token, delayResult);
            
            if (environment.hasYieldReturnValue()) {
                return ContanYieldObject.INSTANCE;
            }
            
            long delay;
            if (delayResult.convertibleToLong()) {
                delay = delayResult.toLong();
            } else {
                ContanRuntimeError.E0031.throwError("", null, token);
                return null;
            }
        
            newEnvironment = new Environment(contanEngine, environment, tickBasedThread, operators[1], true);
            
            tickBasedThread.scheduleTask(() -> operators[1].eval(newEnvironment), delay);
            
            environment.setCoroutineStatus(this, 0, newEnvironment.getFuture().getContanInstance());
        } else {
            return coroutineStatus.results[0];
        }
    
        return newEnvironment.getFuture().getContanInstance();
    }
    
}
