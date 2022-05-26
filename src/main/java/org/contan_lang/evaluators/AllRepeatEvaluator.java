package org.contan_lang.evaluators;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.CancelStatus;
import org.contan_lang.environment.CoroutineStatus;
import org.contan_lang.environment.Environment;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanVoidObject;
import org.contan_lang.variables.primitive.ContanYieldObject;
import org.contan_lang.variables.primitive.JavaClassInstance;

import java.util.Iterator;

public class AllRepeatEvaluator implements Evaluator {

    private final ContanEngine contanEngine;
    private final Evaluator iteratorEval;
    private final Evaluator evaluator;
    private final String name;
    private final String variableName;

    public AllRepeatEvaluator(ContanEngine contanEngine, Evaluator iteratorEval, Evaluator evaluator, String name, String variableName) {
        this.contanEngine = contanEngine;
        this.iteratorEval = iteratorEval;
        this.evaluator = evaluator;
        this.name = name;
        this.variableName = variableName;
    }

    @Override
    public ContanObject<?> eval(Environment environment) {

        Environment newEnv;
        CoroutineStatus coroutineStatus = environment.getCoroutineStatus(this);

        if (coroutineStatus == null) {
            newEnv = new Environment(contanEngine, environment, environment.getContanThread());
            newEnv.setName(name);
        } else {
            newEnv = (Environment) ((JavaClassInstance) coroutineStatus.results[0]).getBasedJavaObject();
        }


        ContanObject<?> iterableObject = iteratorEval.eval(environment);
        if (environment.hasYieldReturnValue() || iterableObject == ContanYieldObject.INSTANCE) {
            environment.setCoroutineStatus(this, 0, new JavaClassInstance(contanEngine, newEnv));
            return ContanYieldObject.INSTANCE;
        }

        Object iterable;
        if (coroutineStatus != null && coroutineStatus.count >= 1) {
            iterable = ((JavaClassInstance) coroutineStatus.results[1]).getBasedJavaObject();
        } else {
            iterable = iterableObject.getBasedJavaObject();
        }

        if (iterable instanceof Iterable<?>) {
            Iterator<?> iterator;
            if (coroutineStatus != null && coroutineStatus.count >= 2) {
                iterator = (Iterator<?>) ((JavaClassInstance) coroutineStatus.results[2]).getBasedJavaObject();
            } else {
                iterator = ((Iterable<?>) iterable).iterator();
            }

            while (iterator.hasNext()) {
                Object element = iterator.next();
                newEnv.createOrSetVariable(variableName, new JavaClassInstance(contanEngine, element));

                ContanObject<?> result = evaluator.eval(newEnv);

                if (newEnv.getCancelStatus() == CancelStatus.STOP) {
                    return ContanVoidObject.INSTANCE;
                }

                if (newEnv.hasYieldReturnValue() || result == ContanYieldObject.INSTANCE) {
                    environment.setCoroutineStatus(this, 2, new JavaClassInstance(contanEngine, newEnv),
                            new JavaClassInstance(contanEngine, iterable), new JavaClassInstance(contanEngine, iterator));
                    return ContanYieldObject.INSTANCE;
                }
            }
        } else {
            newEnv.createOrSetVariable(variableName, new JavaClassInstance(contanEngine, iterable));

            ContanObject<?> result = evaluator.eval(newEnv);

            if (newEnv.hasYieldReturnValue() || result == ContanYieldObject.INSTANCE) {
                environment.setCoroutineStatus(this, 1, new JavaClassInstance(contanEngine, newEnv),
                        new JavaClassInstance(contanEngine, iterable));
                return ContanYieldObject.INSTANCE;
            }
        }

        return ContanVoidObject.INSTANCE;
    }

}

