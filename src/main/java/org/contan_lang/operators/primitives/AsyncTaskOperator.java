package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanClassInstance;
import org.contan_lang.variables.primitive.ContanYieldObject;

import java.util.function.Consumer;

public class AsyncTaskOperator extends TaskOperator {
    
    public AsyncTaskOperator(ContanEngine contanEngine, Token token, Evaluator... operators) {
        super(contanEngine, token, operators);
    }
    
    @Override
    public ContanClassInstance runTask(Environment environment, Evaluator evaluator) {
        ContanThread asyncThread = contanEngine.getNextAsyncThread();
        
        Consumer<Environment> scheduleTask = env -> asyncThread.scheduleTask(() -> {
            ContanObject<?> result = evaluator.eval(env);
        
            if (env.hasReturnValue()) {
                ContanObject<?> returnValue = env.getReturnValue();
                if (!(returnValue instanceof ContanYieldObject)) {
                    env.complete(returnValue);
                }
            } else {
                env.complete(result);
            }
        
            return null;
        });
        
        Environment newEnvironment = new Environment(contanEngine, environment, asyncThread, scheduleTask, true);
        
        return newEnvironment.getCompletable().getContanInstance();
    }
    
}
