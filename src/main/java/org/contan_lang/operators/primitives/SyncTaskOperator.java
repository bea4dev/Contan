package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.CoroutineStatus;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanClassInstance;
import org.contan_lang.variables.primitive.ContanYieldObject;

import java.util.function.Consumer;

public class SyncTaskOperator extends TaskOperator {

    public SyncTaskOperator(ContanEngine contanEngine, Token token, Evaluator... operators) {
        super(contanEngine, token, operators);
    }

    @Override
    public ContanClassInstance runTask(Environment environment) {
        CoroutineStatus coroutineStatus = environment.getCoroutineStatus(this);

        Environment newEnvironment;

        if (coroutineStatus == null) {
            ContanThread contanThread;
            ContanObject<?> thread = operators[0].eval(environment);
            if (thread.getBasedJavaObject() instanceof ContanThread) {
                contanThread = (ContanThread) thread.getBasedJavaObject();
            } else {
                ContanRuntimeError.E0020.throwError("", null, token);
                return null;
            }

            Consumer<Environment> scheduleTask = env -> contanThread.scheduleTask(() -> {
                ContanObject<?> result = operators[1].eval(env);

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

            newEnvironment = new Environment(contanEngine, environment, contanThread, scheduleTask, true);
            scheduleTask.accept(newEnvironment);
            environment.setCoroutineStatus(this, 0, newEnvironment.getCompletable().getContanInstance());
        } else {
            return (ContanClassInstance) coroutineStatus.results[0];
        }

        return newEnvironment.getCompletable().getContanInstance();
    }

}
