package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.CoroutineStatus;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.ClassBlock;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.operators.Operator;
import org.contan_lang.runtime.ContanRuntimeUtil;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.*;

public class InstanceOfOperator extends Operator {

    public InstanceOfOperator(ContanEngine contanEngine, Token token, Evaluator... operators) {
        super(contanEngine, token, operators);
    }

    @Override
    public ContanObject<?> eval(Environment environment) {
        if (super.evalLeftAndRight(environment) == ContanYieldObject.INSTANCE) {
            return ContanYieldObject.INSTANCE;
        }

        contanObject0 = ContanRuntimeUtil.removeReference(token, contanObject0);
        contanObject1 = ContanRuntimeUtil.removeReference(token, contanObject1);

        if (!(contanObject1.getBasedJavaObject() instanceof ClassBlock) && !(contanObject1 instanceof JavaClassObject)) {
            ContanRuntimeError.E0036.throwError("", null, token);
            return null;
        }


        if (contanObject0 instanceof ContanClassInstance && contanObject1.getBasedJavaObject() instanceof ClassBlock) {
            ClassBlock instanceClassBlock = (ClassBlock) contanObject0.getBasedJavaObject();
            ClassBlock classBlock = (ClassBlock) contanObject1.getBasedJavaObject();

            ClassBlock currentClassBlock = instanceClassBlock;
            boolean isInstanceOfClass = false;
            while (true) {
                if (currentClassBlock == classBlock) {
                    isInstanceOfClass = true;
                    break;
                }

                currentClassBlock = currentClassBlock.getSuperClass();

                if (currentClassBlock == null) {
                    break;
                }
            }

            return new ContanBoolean(contanEngine, isInstanceOfClass);
        } else if (contanObject0 instanceof ContanPrimitiveObject<?> && contanObject1 instanceof JavaClassObject) {
            Object javaObject = contanObject0.getBasedJavaObject();
            Class<?> clazz = (Class<?>) contanObject1.getBasedJavaObject();

            return new ContanBoolean(contanEngine, clazz.isInstance(javaObject));
        }

        return new ContanBoolean(contanEngine, false);
    }
}
