package org.contan_lang.evaluators;

import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeException;
import org.contan_lang.syntax.exception.ContanParseException;
import org.contan_lang.syntax.exception.UnexpectedSyntaxException;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanVariable;
import org.contan_lang.variables.NumberType;
import org.contan_lang.variables.primitive.ContanVoid;
import org.contan_lang.variables.primitive.JavaClassInstance;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class PreLinkedCreateClassInstanceEvaluator implements Evaluator {

    private final String classPath;

    private final Token nameToken;

    private final Evaluator[] args;

    private ClassBlock classBlock = null;
    
    private Class<?> javaClass = null;

    public PreLinkedCreateClassInstanceEvaluator(@Nullable String classPath, Token nameToken, Evaluator... args) {
        this.classPath = classPath;
        this.nameToken = nameToken;
        this.args = args;
    }

    public void link(Collection<ClassBlock> classBlocks, Collection<String> collidedClassName, Set<Class<?>> javaClasses) throws ContanParseException {
        if (classPath == null) {
            if (collidedClassName.contains(nameToken.getText())) {
                throw new UnexpectedSyntaxException("");
            }

            for (ClassBlock classBlock : classBlocks) {
                if (classBlock.getClassName().getText().equals(nameToken.getText())) {
                    this.classBlock = classBlock;
                    checkArgLength(classBlock);
                    return;
                }
            }
            
            for (Class<?> clazz : javaClasses) {
                if (clazz.getSimpleName().equals(nameToken.getText())) {
                    this.javaClass = clazz;
                    return;
                }
            }
        } else {
            for (ClassBlock classBlock : classBlocks) {
                if (classBlock.getClassPath().equals(classPath)) {
                    this.classBlock = classBlock;
                    checkArgLength(classBlock);
                    return;
                }
            }
    
            for (Class<?> clazz : javaClasses) {
                if (clazz.getName().equals(classPath)) {
                    this.javaClass = clazz;
                    return;
                }
            }
            
            try {
                this.javaClass = Class.forName(classPath);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        throw new ContanParseException("ClassPath : " + classPath + "  ClassName : " + nameToken.getText());
    }

    public void checkArgLength(ClassBlock classBlock) throws UnexpectedSyntaxException {
        if (this.args.length > classBlock.getInitializeArgs().length) {
            throw new UnexpectedSyntaxException("");
        }
    }

    @Override
    public ContanVariable<?> eval(Environment environment) {
        ContanVariable<?>[] variables = new ContanVariable<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            variables[i] = args[i].eval(environment).createClone();
        }

        if (classBlock != null){
            return classBlock.createInstance(null, variables);
        } else if (javaClass != null) {
            try {
                methodLoop:
                for (Constructor<?> constructor : javaClass.getConstructors()) {
                    if (constructor.getParameterCount() != variables.length) continue;
        
                    Object[] convertedArgs = new Object[variables.length];
                    Parameter[] parameters = constructor.getParameters();
                    for (int i = 0; i < variables.length; i++) {
                        Parameter parameter = parameters[i];
                        ContanVariable<?> variable = variables[i];
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
                    return new JavaClassInstance(instance);
                }
    
                throw new ContanRuntimeException("Not fount java class constructor.");
            } catch (Exception e) {
                e.printStackTrace();
                throw new ContanRuntimeException("A problem has occurred when executing a java method."
                        + System.lineSeparator() + "ClassPath : " + classPath
                        + System.lineSeparator() + "ClassPath : " + nameToken.getText()
                        + System.lineSeparator() + "Arguments : " + Arrays.toString(variables));
            }
            
        }
        
        throw new ContanRuntimeException("Internal error.");
    }

}
