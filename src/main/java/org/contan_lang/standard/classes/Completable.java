package org.contan_lang.standard.classes;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.ClassBlock;
import org.contan_lang.runtime.ContanObjectList;
import org.contan_lang.runtime.ContanRuntimeUtil;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.*;

public class Completable extends ClassBlock {
    
    public Completable(Token className, String classPath, Environment moduleEnvironment, Token... initializeArgs) {
        super(className, classPath, moduleEnvironment, initializeArgs);
    }
    
    @Override
    public ContanClassInstance createInstance(ContanEngine contanEngine, ContanObject<?>... contanObjects) {
        ContanClassInstance classInstance = super.createInstance(contanEngine, contanObjects);
        
        Environment environment = classInstance.getEnvironment();
        environment.createVariable("then", new JavaClassInstance(contanEngine, new ContanObjectList()));
        environment.createVariable("catch", new JavaClassInstance(contanEngine, new ContanObjectList()));
        environment.createVariable("isDone", new ContanBoolean(contanEngine, false));
        
        return classInstance;
    }
    
    @Override
    public ContanObject<?> invokeFunction(Environment classInstanceEnvironment, Token functionName, ContanObject<?>... variables) {
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
                
                ContanObject<?> contanObject = classInstanceEnvironment.getVariable("then");
                if (contanObject == null) {
                    ContanRuntimeError.E0000.throwError("'then' not found.", null, functionName);
                    return null;
                }
                
                ContanObjectList contanObjectList = ContanRuntimeUtil.getListInstance(functionName, contanObject);
                if (contanObjectList == null) return null;
                
                contanObjectList.add(functionExpression);
                
                return ContanNull.INSTANCE;
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
                
                ContanObject<?> contanObject = classInstanceEnvironment.getVariable("catch");
                if (contanObject == null) {
                    ContanRuntimeError.E0000.throwError("'catch' not found.", null, functionName);
                    return null;
                }
    
                ContanObjectList contanObjectList = ContanRuntimeUtil.getListInstance(functionName, contanObject);
                if (contanObjectList == null) return null;
    
                contanObjectList.add(functionExpression);
                
                return ContanNull.INSTANCE;
            }
            
            case "isDone" : {
                ContanObject<?> contanObject = classInstanceEnvironment.getVariable("isDone");
                if (contanObject == null) {
                    ContanRuntimeError.E0000.throwError("'isDone' not found.", null, functionName);
                    return null;
                }
                
                return contanObject;
            }
            
            default : {
                ContanRuntimeError.E0011.throwError("", null, functionName);
                return null;
            }
        }
    }
}
