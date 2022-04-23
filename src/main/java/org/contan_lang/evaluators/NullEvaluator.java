package org.contan_lang.evaluators;

import org.contan_lang.environment.Environment;
import org.contan_lang.variables.ContanVariable;
import org.contan_lang.variables.primitive.ContanVoid;

public class NullEvaluator implements Evaluator {

    public static NullEvaluator INSTANCE = new NullEvaluator();


    private NullEvaluator(){}

    @Override
    public ContanVariable<?> eval(Environment environment) {
        return ContanVoid.INSTANCE;
    }

}
