package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.ContanObjectReference;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.operators.Operator;
import org.contan_lang.ContanModule;
import org.contan_lang.runtime.ContanRuntimeUtil;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.*;

public class GetFieldOperator extends Operator {

    private final Evaluator left;

    public GetFieldOperator(ContanEngine contanEngine, Token fieldName, Evaluator left) {
        super(contanEngine, fieldName);
        this.left = left;
    }

    @Override
    public ContanObject<?> eval(Environment environment) {

        ContanObject<?> leftResult = left.eval(environment);
        leftResult = ContanRuntimeUtil.removeReference(token, leftResult);
        
        if (environment.hasYieldReturnValue() || leftResult == ContanYieldObject.INSTANCE) {
            return ContanYieldObject.INSTANCE;
        }
        

        if (leftResult instanceof JavaClassObject) {
            Class<?> clazz = (Class<?>) leftResult.getBasedJavaObject();
            //Get static field
            try {
                Object object = clazz.getField(token.getText()).get(null);
                return new JavaClassInstance(contanEngine, object);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                ContanRuntimeError.E0015.throwError("", e, token);
            }
        } else if (leftResult instanceof JavaClassInstance) {
            Class<?> clazz = leftResult.getBasedJavaObject().getClass();
            //Get instance field
            try {
                Object object = clazz.getField(token.getText()).get(leftResult.getBasedJavaObject());
                return new JavaClassInstance(contanEngine, object);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                ContanRuntimeError.E0015.throwError("", e, token);
            }
        } else if (leftResult instanceof ContanModuleObject) {
            ContanModule contanModule = (ContanModule) leftResult.getBasedJavaObject();
            return contanModule.getModuleEnvironment().getVariable(token.getText());
        } else if (leftResult instanceof ContanClassInstance) {
            Environment instanceEnvironment = ((ContanClassInstance) leftResult).getEnvironment();
            return instanceEnvironment.getVariable(token.getText());
        }

        ContanRuntimeError.E0015.throwError("", null, token);
        return null;
    }

}
