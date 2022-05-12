package org.contan_lang.evaluators;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.CancelStatus;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanVoidObject;
import org.contan_lang.variables.primitive.ContanYieldObject;
import org.jetbrains.annotations.Nullable;

public class RepeatEvaluator implements Evaluator {

    private final ContanEngine contanEngine;
    private final Token token;
    private final Evaluator termsEvaluator;
    private final Evaluator evaluator;
    private final String name;

    public RepeatEvaluator(ContanEngine contanEngine, Token token, @Nullable Evaluator termsEvaluator, Evaluator evaluator, String name) {
        this.contanEngine = contanEngine;
        this.token = token;
        this.termsEvaluator = termsEvaluator;
        this.evaluator = evaluator;
        this.name = name;
    }

    @Override
    public ContanObject<?> eval(Environment environment) {

        Environment newEnv = new Environment(contanEngine, environment, environment.getContanThread());
        newEnv.setName(name);

        if (termsEvaluator == null) {
            while (true) {
                ContanObject<?> result = evaluator.eval(newEnv);

                if (newEnv.getCancelStatus() == CancelStatus.STOP) {
                    return ContanVoidObject.INSTANCE;
                }

                if (newEnv.hasYieldReturnValue() || result == ContanYieldObject.INSTANCE) {
                    return ContanYieldObject.INSTANCE;
                }
            }
        } else {
            ContanObject<?> termResult = termsEvaluator.eval(environment);

            if (environment.hasYieldReturnValue() || termResult == ContanYieldObject.INSTANCE) {
                return ContanYieldObject.INSTANCE;
            }

            if (!(termResult.convertibleToLong())) {
                ContanRuntimeError.E0025.throwError("", null, token);
            }

            long maxNumberOfRepeat = termResult.asLong();

            for (long i = 0; i < maxNumberOfRepeat; i++) {
                ContanObject<?> result = evaluator.eval(newEnv);

                if (newEnv.getCancelStatus() == CancelStatus.STOP) {
                    return ContanVoidObject.INSTANCE;
                }

                if (newEnv.hasYieldReturnValue() || result == ContanYieldObject.INSTANCE) {
                    newEnv.setCoroutineStatus(this, i);
                    return ContanYieldObject.INSTANCE;
                }
            }
        }

        return ContanVoidObject.INSTANCE;
    }

}
