package org.contan_lang.evaluators;

import org.contan_lang.environment.Environment;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanNull;

public class NullEvaluator implements Evaluator {

    public static NullEvaluator INSTANCE = new NullEvaluator();


    private NullEvaluator(){}

    @Override
    public ContanObject<?> eval(Environment environment) {
        return ContanNull.INSTANCE;
    }

}
