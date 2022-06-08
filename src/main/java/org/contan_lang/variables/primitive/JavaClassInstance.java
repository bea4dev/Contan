package org.contan_lang.variables.primitive;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.NumberType;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

public class JavaClassInstance extends ContanPrimitiveObject<Object> {
    
    public JavaClassInstance(ContanEngine contanEngine, Object based) {
        super(contanEngine, based);
    }
    
    @Override
    public ContanObject<?> invokeFunctionChild(ContanThread contanThread, Token functionName, ContanObject<?>... variables) {
        return invokeJavaMethod(contanEngine, based.getClass(), based, functionName, variables);
    }
    
    @Override
    public ContanObject<Object> createClone() {
        return new JavaClassInstance(contanEngine, based);
    }
    
    @Override
    public long toLong() {
        if (based instanceof String) {
            return ContanString.toLong((String) based);
        }
        
        if (based instanceof Integer) {
            return (int) based;
        }
        
        if (based instanceof Long) {
            return (long) based;
        }
        
        if (based instanceof Float) {
            return (long) ((float) based);
        }
    
        if (based instanceof Double) {
            return (long) ((double) based);
        }
    
        return 0;
    }
    
    @Override
    public double toDouble() {
        if (based instanceof String) {
            return ContanString.toDouble((String) based);
        }
    
        if (based instanceof Integer) {
            return (int) based;
        }
    
        if (based instanceof Long) {
            return (long) based;
        }
    
        if (based instanceof Float) {
            return (double) ((float) based);
        }
    
        if (based instanceof Double) {
            return (double) ((double) based);
        }
    
        return 0.0;
    }
    
    @Override
    public boolean convertibleToLong() {
        if (based instanceof Integer || based instanceof Long || based instanceof Float || based instanceof Double) {
            double number;
            if (based instanceof Integer) {
                number = (Integer) based;
            } else if (based instanceof Long) {
                number = (Long) based;
            } else if (based instanceof Float) {
                number = (Float) based;
            } else {
                number = (Double) based;
            }
    
            NumberType numberType = NumberType.getType(number);
            return numberType == NumberType.INTEGER || numberType == NumberType.LONG;
        } else if (based instanceof String) {
            if (!((String) based).matches("[+-]?\\d+(?:\\.\\d+)?")) {
                return false;
            }
    
            NumberType numberType = NumberType.getType(Double.parseDouble((String) based));
            return numberType == NumberType.INTEGER || numberType == NumberType.LONG;
        } else {
            return false;
        }
    }
    
    @Override
    public boolean convertibleToDouble() {
        if (based instanceof Integer || based instanceof Long || based instanceof Float || based instanceof Double) {
            return true;
        } else if (based instanceof String) {
            return ((String) based).matches("[+-]?\\d+(?:\\.\\d+)?");
        } else {
            return false;
        }
    }
    
    @Override
    public Object convertToJavaObject() {
        return based;
    }
    
    @Override
    public String toString() {
        return based.toString();
    }
    
    
    public static ContanObject<?> invokeJavaMethod(ContanEngine contanEngine, Class<?> clazz, @Nullable Object based, Token functionName, ContanObject<?>... variables) {
        try {
            methodLoop : for (Method method : clazz.getMethods()) {
                if (method.getParameterCount() != variables.length) continue;
                if (!method.getName().equals(functionName.getText())) continue;
                
                Object[] convertedArgs = new Object[variables.length];
                Parameter[] parameters = method.getParameters();
                for (int i = 0; i < variables.length; i++) {
                    Parameter parameter = parameters[i];
                    ContanObject<?> variable = variables[i];
                    Class<?> parameterType = parameter.getType();

                    if (variable.convertibleToDouble()) {
                        double original = variable.toDouble();
                        NumberType numberType = NumberType.getType(original);

                        if (parameterType == int.class || parameterType == Integer.class) {
                            if (numberType != NumberType.INTEGER) {
                                continue methodLoop;
                            }

                            convertedArgs[i] = (int) original;
                            continue;
                        } else if (parameterType == long.class || parameterType == Long.class) {
                            if (numberType != NumberType.LONG && numberType != NumberType.INTEGER) {
                                continue methodLoop;
                            }

                            convertedArgs[i] = (long) original;
                            continue;
                        } else if (parameterType == float.class || parameterType == Float.class) {
                            if (numberType != NumberType.FLOAT && numberType != NumberType.INTEGER) {
                                continue methodLoop;
                            }

                            convertedArgs[i] = (float) original;
                            continue;
                        } else if (parameterType == double.class || parameterType == Double.class) {
                            convertedArgs[i] = original;
                            continue;
                        }
                    }

                    if (variable == ContanVoidObject.INSTANCE) {
                        convertedArgs[i] = null;
                    } else {
                        if (!parameterType.isInstance(variable.convertibleToDouble())) {
                            continue methodLoop;
                        }
    
                        convertedArgs[i] = variable.getBasedJavaObject();
                    }
                }
                
                method.setAccessible(true);
                Object returned = method.invoke(based, convertedArgs);
                if (returned == null) {
                    return ContanVoidObject.INSTANCE;
                } else {
                    if (returned instanceof ContanObject<?>) {
                        return (ContanObject<?>) returned;
                    } else {
                        return new JavaClassInstance(contanEngine, returned);
                    }
                }
            }
        } catch (Exception e) {
            ContanRuntimeError.E0007.throwError("\nMethod : " + functionName.getText()
                    + (based == null ? "" : " | Class : " + based.getClass().getName())
                    + " | Arguments : " + Arrays.toString(variables), e, functionName);
        }
    
        ContanRuntimeError.E0007.throwError("\nMethod : " + functionName.getText()
                + (based == null ? "" : " | Class : " + based.getClass().getName())
                + " | Arguments : " + Arrays.toString(variables), null, functionName);
        
        return null;
    }
    
}
