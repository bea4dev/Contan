package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.CoroutineStatus;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.ClassBlock;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.runtime.ContanRuntimeUtil;
import org.contan_lang.runtime.JavaContanFuture;
import org.contan_lang.standard.classes.StandardClasses;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.NumberType;
import org.contan_lang.variables.primitive.*;

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
        int startIndex = 0;
        CoroutineStatus coroutineStatus = environment.getCoroutineStatus(this);
        ContanObject<?>[] variables = new ContanObject<?>[args.length];
        
        if (coroutineStatus != null) {
            startIndex = (int) coroutineStatus.count;
            System.arraycopy(coroutineStatus.results, 0, variables, 0, startIndex);
        }
        
        for (int i = startIndex; i < args.length; i++) {
            ContanObject<?> result = args[i].eval(environment).createClone();
            result = ContanRuntimeUtil.removeReference(nameToken, result);
            
            if (environment.hasYieldReturnValue() || result == ContanYieldObject.INSTANCE) {
                ContanObject<?>[] results = new ContanObject<?>[i + 1];
                System.arraycopy(variables, 0, results, 0, i);
                
                environment.setCoroutineStatus(this, i, results);
                return ContanYieldObject.INSTANCE;
            }
            
            variables[i] = result;
        }


        ContanObject<?> leftResult = left.eval(environment);
        leftResult = ContanRuntimeUtil.removeReference(nameToken, leftResult);
        
        if (environment.hasYieldReturnValue()) {
            environment.setCoroutineStatus(this, args.length, variables);
            return ContanYieldObject.INSTANCE;
        }

        if (leftResult instanceof ContanClassObject) {

            ClassBlock classBlock = (ClassBlock) leftResult.getBasedJavaObject();
            ContanClassInstance instance = classBlock.createInstance(contanEngine, environment.getContanThread(), variables);

            if (classBlock == StandardClasses.FUTURE) {
                Environment instanceEnv = instance.getEnvironment();
                instanceEnv.createOrSetVariable("javaFuture", new JavaClassInstance(contanEngine, new JavaContanFuture(instance)));
            }

            return instance;

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
                            if (variable == ContanVoidObject.INSTANCE) {
                                convertedArgs[i] = null;
                            } else {
                                convertedArgs[i] = variable.getBasedJavaObject();
                            }
                        }
                    }

                    Object instance = constructor.newInstance(convertedArgs);
                    return new JavaClassInstance(contanEngine, instance);
                }
            } catch (Exception e) {
                ContanRuntimeError.E0005.throwError("\nClassPath : " + javaClass.getName()
                        + " | ClassName : " + nameToken.getText()
                        + " | Arguments : " + Arrays.toString(variables), e, nameToken);
            }

        }
        
        ContanRuntimeError.E0014.throwError(leftResult.toString(), null, nameToken);
        return null;
    }

}
