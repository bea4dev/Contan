package org.contan_lang.operators.primitives;

import org.contan_lang.environment.Environment;
import org.contan_lang.operators.Operator;
import org.contan_lang.variables.ContanVariable;
import org.contan_lang.variables.primitive.ContanVoid;

public class NullValueOperator extends Operator {
    @Override
    public ContanVariable<?> eval(Environment environment) {
        return ContanVoid.INSTANCE;
    }
}
