package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.operators.Operator;
import org.contan_lang.variables.ContanVariable;
import org.contan_lang.variables.primitive.ContanVoid;

public class NullValueOperator extends Operator {

    public NullValueOperator(ContanEngine contanEngine, Evaluator... operators) {
        super(contanEngine, operators);
    }

    @Override
    public ContanVariable<?> eval(Environment environment) {
        return ContanVoid.INSTANCE;
    }
}
