package org.contan_lang.variables.primitive;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.syntax.tokens.Token;
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
    public ContanObject<?> invokeFunction(Token functionName, ContanObject<?>... variables) {
        return invokeJavaMethod(contanEngine, based.getClass(), based, functionName, variables);
    }
    
    @Override
    public ContanObject<Object> createClone() {
        return new JavaClassInstance(contanEngine, based);
    }
    
    @Override
    public long asLong() {
        if (based instanceof String) {
            return ContanString.asLong((String) based);
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
    public double asDouble() {
        if (based instanceof String) {
            return ContanString.asDouble((String) based);
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
            return true;
        } else if (based instanceof String) {
            return ((String) based).matches("[+-]?\\d+(?:\\.\\d+)?");
        } else {
            return false;
        }
    }
    
    @Override
    public boolean convertibleToDouble() {
        return convertibleToLong();
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
                        double original = variable.asDouble();
                        NumberType numberType = NumberType.getType(original);
                        
                        if (parameterType == int.class || parameterType == Integer.class) {
                            if (numberType != NumberType.INTEGER) {
                                continue methodLoop;
                            }
                            
                            convertedArgs[i] = (int) original;
                        } else if (parameterType == long.class || parameterType == Long.class) {
                            if (numberType != NumberType.LONG && numberType != NumberType.INTEGER) {
                                continue methodLoop;
                            }
                            
                            convertedArgs[i] = (long) original;
                        } else if (parameterType == float.class || parameterType == Float.class) {
                            if (numberType != NumberType.FLOAT && numberType != NumberType.INTEGER) {
                                continue methodLoop;
                            }
                            
                            convertedArgs[i] = (float) original;
                        } else if (parameterType == double.class || parameterType == Double.class) {
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
                
                Object returned = method.invoke(based, convertedArgs);
                if (returned == null) {
                    return ContanVoid.INSTANCE;
                } else {
                    return new JavaClassInstance(contanEngine, returned);
                }
            }
        } catch (Exception e){
            ContanRuntimeError.E0007.throwError(System.lineSeparator() + "Method : " + functionName.getText()
                    + (based == null ? "" : System.lineSeparator() + "Class : " + based.getClass().getName())
                    + System.lineSeparator() + "Arguments : " + Arrays.toString(variables), e, functionName);
        }
    
        ContanRuntimeError.E0007.throwError(System.lineSeparator() + "Method : " + functionName.getText()
                + (based == null ? "" : System.lineSeparator() + "Class : " + based.getClass().getName())
                + System.lineSeparator() + "Arguments : " + Arrays.toString(variables), null, functionName);
        
        return null;
    }
    
}
