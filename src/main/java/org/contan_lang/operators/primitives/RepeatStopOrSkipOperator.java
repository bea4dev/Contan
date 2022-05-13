package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.CancelStatus;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.operators.Operator;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanVoidObject;

public class RepeatStopOrSkipOperator extends Operator {

    private final String labelName;
    private final CancelStatus cancelStatus;

    public RepeatStopOrSkipOperator(ContanEngine contanEngine, Token token, String labelName, CancelStatus cancelStatus) {
        super(contanEngine, token);
        this.labelName = labelName;
        this.cancelStatus = cancelStatus;
    }

    @Override
    public ContanObject<?> eval(Environment environment) {

        Environment current = environment;
        while (true) {
            String name = current.getName();
            if (name != null) {
                if (name.equals(labelName)) {
                    break;
                }
            }

            current = current.getParent();

            if (current == null) {
                break;
            }
        }

        if (current == null) {
            ContanRuntimeError.E0025.throwError("", null, token);
            return null;
        }

        current.setCancelStatus(cancelStatus);

        return ContanVoidObject.INSTANCE;
    }

}
