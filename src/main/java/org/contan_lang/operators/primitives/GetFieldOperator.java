package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.ContanJavaBaseObjectReference;
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

import java.lang.reflect.Field;

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
                Field field = clazz.getField(token.getText());
                return new ContanJavaBaseObjectReference(contanEngine, token.getText(), ContanVoidObject.INSTANCE, field, null);
            } catch (NoSuchFieldException e) {
                for (Object content : clazz.getEnumConstants()) {
                    if (content.toString().equals(token.getText())) {
                        return new JavaClassInstance(contanEngine, content);
                    }
                }

                ContanRuntimeError.E0015.throwError("", e, token);
            }
        } else if (leftResult instanceof JavaClassInstance) {
            Class<?> clazz = leftResult.getBasedJavaObject().getClass();
            //Get instance field
            try {
                Field field = clazz.getField(token.getText());
                return new ContanJavaBaseObjectReference(contanEngine, token.getText(), ContanVoidObject.INSTANCE, field, leftResult.getBasedJavaObject());
            } catch (NoSuchFieldException e) {
                ContanRuntimeError.E0015.throwError("", e, token);
            }
        } else if (leftResult instanceof ContanModuleObject) {
            ContanModule contanModule = (ContanModule) leftResult.getBasedJavaObject();
            ContanObject<?> result = contanModule.getModuleEnvironment().getVariable(token.getText());
            if (result == null) {
                ContanRuntimeError.E0015.throwError("", null, token);
            }
            return result;
        } else if (leftResult instanceof ContanClassInstance) {
            Environment instanceEnvironment = ((ContanClassInstance) leftResult).getEnvironment();
            ContanObject<?> result = instanceEnvironment.getVariable(token.getText());
            if (result == null) {
                ContanRuntimeError.E0015.throwError("", null, token);
            }
            return result;
        }

        ContanRuntimeError.E0015.throwError("", null, token);
        return null;
    }

}
