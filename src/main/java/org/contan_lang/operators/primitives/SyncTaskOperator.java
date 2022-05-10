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

            newEnvironment = new Environment(contanEngine, environment, contanThread, operators[1], true);
            newEnvironment.reRun();
            environment.setCoroutineStatus(this, 0, newEnvironment.getCompletable().getContanInstance());
        } else {
            return (ContanClassInstance) coroutineStatus.results[0];
        }

        return newEnvironment.getCompletable().getContanInstance();
    }

}
