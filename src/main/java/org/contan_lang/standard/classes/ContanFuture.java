package org.contan_lang.standard.classes;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.ClassBlock;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.runtime.JavaContanFuture;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.*;

public class ContanFuture extends ClassBlock {
    
    public ContanFuture(Token className, String classPath, Environment moduleEnvironment, Evaluator superClassEval, Token... initializeArgs) {
        super(className, classPath, moduleEnvironment, superClassEval, initializeArgs);
    }
    
    @Override
    public ContanClassInstance createInstance(ContanEngine contanEngine, ContanThread contanThread, ContanObject<?>... contanObjects) {
        ContanClassInstance classInstance = super.createInstance(contanEngine, contanThread, contanObjects);
        
        Environment environment = classInstance.getEnvironment();
        environment.createVariable("javaFuture", ContanVoidObject.INSTANCE);
        
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
                
                ContanObject<?> contanObject = classInstanceEnvironment.getVariable("javaFuture");
                
                if (contanObject == null) {
                    return null;
                }
                
                if (!(contanObject.getBasedJavaObject() instanceof JavaContanFuture)) {
                    ContanRuntimeError.E0000.throwError("", null, functionName);
                    return null;
                }
                
                JavaContanFuture javaContanFuture = (JavaContanFuture) contanObject.getBasedJavaObject();
                
                try {
                    javaContanFuture.LOCK.lock();
                    
                    if (javaContanFuture.isDone()){
                        functionExpression.eval(contanThread, functionName, javaContanFuture.getResult());
                    } else {
                        javaContanFuture.addThen(new JavaContanFuture.FunctionExpressionWithThread(contanThread, functionExpression));
                    }
                } finally {
                    javaContanFuture.LOCK.unlock();
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
    
                ContanObject<?> contanObject = classInstanceEnvironment.getVariable("javaFuture");
    
                if (contanObject == null) {
                    return null;
                }
    
                if (!(contanObject.getBasedJavaObject() instanceof JavaContanFuture)) {
                    ContanRuntimeError.E0000.throwError("", null, functionName);
                    return null;
                }
    
                JavaContanFuture javaContanFuture = (JavaContanFuture) contanObject.getBasedJavaObject();
    
                try {
                    javaContanFuture.LOCK.lock();
        
                    if (javaContanFuture.isDone()){
                        functionExpression.eval(contanThread, functionName, javaContanFuture.getResult());
                    } else {
                        javaContanFuture.addCatch(new JavaContanFuture.FunctionExpressionWithThread(contanThread, functionExpression));
                    }
                } finally {
                    javaContanFuture.LOCK.unlock();
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

                ContanObject<?> contanObject = classInstanceEnvironment.getVariable("javaFuture");

                if (contanObject == null) {
                    return null;
                }

                if (!(contanObject.getBasedJavaObject() instanceof JavaContanFuture)) {
                    ContanRuntimeError.E0000.throwError("", null, functionName);
                    return null;
                }

                JavaContanFuture javaContanFuture = (JavaContanFuture) contanObject.getBasedJavaObject();

                javaContanFuture.complete(variables[0]);
                return ContanVoidObject.INSTANCE;
            }
            
            default : {
                ContanRuntimeError.E0011.throwError("", null, functionName);
                return null;
            }
        }
    }
}
