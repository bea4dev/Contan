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
        ContanObject<?> contanObject0;
        ContanObject<?> contanObject1;
    
        CoroutineStatus coroutineStatus = environment.getCoroutineStatus(this);
    
        if (coroutineStatus == null) {
            contanObject0 = operators[0].eval(environment);
            if (environment.hasYieldReturnValue() || contanObject0 == ContanYieldObject.INSTANCE) {
                return ContanYieldObject.INSTANCE;
            }
        } else {
            contanObject0 = coroutineStatus.results[0];
        }
    
        contanObject1 = operators[1].eval(environment);
        if (environment.hasYieldReturnValue() || contanObject1 == ContanYieldObject.INSTANCE) {
            environment.setCoroutineStatus(this, 0, contanObject0);
            return ContanYieldObject.INSTANCE;
        }

        contanObject0 = ContanRuntimeUtil.dereference(token, contanObject0);
        contanObject1 = ContanRuntimeUtil.dereference(token, contanObject1);

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
