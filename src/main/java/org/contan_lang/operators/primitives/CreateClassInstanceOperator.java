package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.ContanObjectReference;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.ClassBlock;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.NumberType;
import org.contan_lang.variables.primitive.ContanClassObject;
import org.contan_lang.variables.primitive.ContanVoid;
import org.contan_lang.variables.primitive.JavaClassInstance;
import org.contan_lang.variables.primitive.JavaClassObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;

public class CreateClassInstanceOperator implements Evaluator {
    
    private final ContanEngine contanEngine;

    private final Token nameToken;

    private final Evaluator left;

    private final Evaluator[] args;


    public CreateClassInstanceOperator(ContanEngine contanEngine, Token nameToken, Evaluator left, Evaluator... args) {
        this.contanEngine = contanEngine;
        this.nameToken = nameToken;
        this.left = left;
        this.args = args;
    }


    @Override
    public ContanObject<?> eval(Environment environment) {
        ContanObject<?>[] variables = new ContanObject<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            variables[i] = args[i].eval(environment).createClone();
        }


        ContanObject<?> leftResult = left.eval(environment);
        if (leftResult instanceof ContanObjectReference) {
            try {
                leftResult = ((ContanObjectReference) leftResult).getContanVariable();
            } catch (IllegalAccessException e) {
                ContanRuntimeError.E0012.throwError("", e, nameToken);
            }
        }

        if (leftResult instanceof ContanClassObject) {

            ClassBlock classBlock = (ClassBlock) leftResult.getBasedJavaObject();
            return classBlock.createInstance(variables);

        } else if (leftResult instanceof JavaClassObject) {

            Class<?> javaClass = (Class<?>) leftResult.getBasedJavaObject();
            try {
                methodLoop:
                for (Constructor<?> constructor : javaClass.getConstructors()) {
                    if (constructor.getParameterCount() != variables.length) continue;

                    Object[] convertedArgs = new Object[variables.length];
                    Parameter[] parameters = constructor.getParameters();
                    for (int i = 0; i < variables.length; i++) {
                        Parameter parameter = parameters[i];
                        ContanObject<?> variable = variables[i];
                        Class<?> parameterType = parameter.getType();

                        if (variable.convertibleToDouble()) {
                            double original = variable.asDouble();
                            NumberType numberType = NumberType.getType(original);

                            if (parameterType == int.class || parameterType == Integer.class) {
                                if (numberType != NumberType.INTEGER) {
                                    continue methodLoop;
                                }

                                convertedArgs[i] = (int) original;
                            } else if (parameterType == long.class || parameterType == Long.class) {
                                if (numberType != NumberType.LONG) {
                                    continue methodLoop;
                                }

                                convertedArgs[i] = (long) original;
                            } else if (parameterType == float.class || parameterType == Float.class) {
                                if (numberType != NumberType.FLOAT) {
                                    continue methodLoop;
                                }

                                convertedArgs[i] = (float) original;
                            } else if (parameterType == double.class || parameterType == Double.class) {
                                if (numberType != NumberType.DOUBLE) {
                                    continue methodLoop;
                                }

                                convertedArgs[i] = original;
                            } else {
                                convertedArgs[i] = variable.getBasedJavaObject();
                            }
                        } else {
                            if (variable == ContanVoid.INSTANCE) {
                                convertedArgs[i] = null;
                            } else {
                                convertedArgs[i] = variable.getBasedJavaObject();
                            }
                        }
                    }

                    Object instance = constructor.newInstance(convertedArgs);
                    return new JavaClassInstance(contanEngine, instance);
                }

                ContanRuntimeError.E0006.throwError(System.lineSeparator() + "ClassPath : " + javaClass.getName()
                        + System.lineSeparator() + "ClassName : " + nameToken.getText()
                        + System.lineSeparator() + "Arguments : " + Arrays.toString(variables), null, nameToken);
            } catch (Exception e) {
                ContanRuntimeError.E0005.throwError(System.lineSeparator() + "ClassPath : " + javaClass.getName()
                        + System.lineSeparator() + "ClassName : " + nameToken.getText()
                        + System.lineSeparator() + "Arguments : " + Arrays.toString(variables), e, nameToken);
            }

        }
        
        ContanRuntimeError.E0014.throwError(leftResult.toString(), null, nameToken);
        return null;
    }

}
