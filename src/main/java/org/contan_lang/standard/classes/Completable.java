package org.contan_lang.standard.classes;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.ClassBlock;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.runtime.ContanObjectList;
import org.contan_lang.runtime.ContanRuntimeUtil;
import org.contan_lang.runtime.JavaCompletable;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.*;

public class Completable extends ClassBlock {
    
    public Completable(Token className, String classPath, Environment moduleEnvironment, Evaluator superClassEval, Token... initializeArgs) {
        super(className, classPath, moduleEnvironment, superClassEval, initializeArgs);
    }
    
    @Override
    public ContanClassInstance createInstance(ContanEngine contanEngine, ContanThread contanThread, ContanObject<?>... contanObjects) {
        ContanClassInstance classInstance = super.createInstance(contanEngine, contanThread, contanObjects);
        
        Environment environment = classInstance.getEnvironment();
        environment.createVariable("javaCompletable", ContanVoidObject.INSTANCE);
        
        return classInstance;
    }
    
    @Override
    public ContanObject<?> invokeFunction(ContanThread contanThread, Environment classInstanceEnvironment, Token functionName, boolean ignoreNotFound, ContanObject<?>... variables) {
        switch (functionName.getText()) {
            case "then" : {
                if (variables.length != 1) {
                    ContanRuntimeError.E0016.throwError("", null, functionName);
                }
    
                ContanFunctionExpression functionExpression;
                if (variables[0] instanceof ContanFunctionExpression) {
                    functionExpression = (ContanFunctionExpression) variables[0];
                } else {
                    ContanRuntimeError.E0017.throwError("", null, functionName);
                    return null;
                }

                if (functionExpression.getBasedJavaObject().getArgs().length != 1) {
                    ContanRuntimeError.E0010.throwError("", null, functionName);
                }
                
                ContanObject<?> contanObject = classInstanceEnvironment.getVariable("javaCompletable");
                
                if (contanObject == null) {
                    return null;
                }
                
                if (!(contanObject.getBasedJavaObject() instanceof JavaCompletable)) {
                    ContanRuntimeError.E0000.throwError("", null, functionName);
                    return null;
                }
                
                JavaCompletable javaCompletable = (JavaCompletable) contanObject.getBasedJavaObject();
                
                try {
                    javaCompletable.LOCK.lock();
                    
                    if (javaCompletable.isDone()){
                        functionExpression.eval(contanThread, functionName, javaCompletable.getResult());
                    } else {
                        javaCompletable.addThen(new JavaCompletable.FunctionExpressionWithThread(contanThread, functionExpression));
                    }
                } finally {
                    javaCompletable.LOCK.unlock();
                }
                
                return ContanVoidObject.INSTANCE;
            }
            
            case "catch" : {
                if (variables.length != 1) {
                    ContanRuntimeError.E0016.throwError("", null, functionName);
                }
    
                ContanFunctionExpression functionExpression;
                if (variables[0] instanceof ContanFunctionExpression) {
                    functionExpression = (ContanFunctionExpression) variables[0];
                } else {
                    ContanRuntimeError.E0017.throwError("", null, functionName);
                    return null;
                }

                if (functionExpression.getBasedJavaObject().getArgs().length != 1) {
                    ContanRuntimeError.E0010.throwError("", null, functionName);
                }
    
                ContanObject<?> contanObject = classInstanceEnvironment.getVariable("javaCompletable");
    
                if (contanObject == null) {
                    return null;
                }
    
                if (!(contanObject.getBasedJavaObject() instanceof JavaCompletable)) {
                    ContanRuntimeError.E0000.throwError("", null, functionName);
                    return null;
                }
    
                JavaCompletable javaCompletable = (JavaCompletable) contanObject.getBasedJavaObject();
    
                try {
                    javaCompletable.LOCK.lock();
        
                    if (javaCompletable.isDone()){
                        functionExpression.eval(contanThread, functionName, javaCompletable.getResult());
                    } else {
                        javaCompletable.addCatch(new JavaCompletable.FunctionExpressionWithThread(contanThread, functionExpression));
                    }
                } finally {
                    javaCompletable.LOCK.unlock();
                }
                
                return ContanVoidObject.INSTANCE;
            }
            
            case "isDone" : {
                ContanObject<?> contanObject = classInstanceEnvironment.getVariable("isDone");
                if (contanObject == null) {
                    ContanRuntimeError.E0000.throwError("'isDone' not found.", null, functionName);
                    return null;
                }
                
                return contanObject;
            }
            
            case "complete" : {
                if (variables.length != 1) {
                    ContanRuntimeError.E0010.throwError("", null, functionName);
                    return null;
                }

                ContanObject<?> contanObject = classInstanceEnvironment.getVariable("javaCompletable");

                if (contanObject == null) {
                    return null;
                }

                if (!(contanObject.getBasedJavaObject() instanceof JavaCompletable)) {
                    ContanRuntimeError.E0000.throwError("", null, functionName);
                    return null;
                }

                JavaCompletable javaCompletable = (JavaCompletable) contanObject.getBasedJavaObject();

                javaCompletable.complete(variables[0]);
                return ContanVoidObject.INSTANCE;
            }
            
            default : {
                ContanRuntimeError.E0011.throwError("", null, functionName);
                return null;
            }
        }
    }
}
