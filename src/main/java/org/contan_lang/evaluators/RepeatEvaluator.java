package org.contan_lang.evaluators;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanYieldObject;

public class RepeatEvaluator implements Evaluator {

    private final ContanEngine contanEngine;
    private final Token token;
    private final Evaluator termsEvaluator;
    private final Evaluator evaluator;

    public RepeatEvaluator(ContanEngine contanEngine, Token token, Evaluator termsEvaluator, Evaluator evaluator) {
        this.contanEngine = contanEngine;
        this.token = token;
        this.termsEvaluator = termsEvaluator;
        this.evaluator = evaluator;
    }

    @Override
    public ContanObject<?> eval(Environment environment) {

        ContanObject<?> termResult = termsEvaluator.eval(environment);

        if (environment.hasYieldReturnValue() || termResult == ContanYieldObject.INSTANCE) {
            return ContanYieldObject.INSTANCE;
        }

        if (!(termResult.getBasedJavaObject() instanceof Boolean)) {
            ContanRuntimeError.E0024.throwError("", null, token);
        }

        return null;
    }

}
