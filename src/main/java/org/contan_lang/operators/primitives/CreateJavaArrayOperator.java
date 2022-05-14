package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.CoroutineStatus;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.operators.Operator;
import org.contan_lang.runtime.ContanRuntimeUtil;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanYieldObject;
import org.contan_lang.variables.primitive.JavaClassInstance;

import java.lang.reflect.Array;

public class CreateJavaArrayOperator extends Operator {
    
    private final Evaluator typeEval;
    private final Evaluator indexEval;
    private final Evaluator[] arguments;
    
    public CreateJavaArrayOperator(ContanEngine contanEngine, Token token, Evaluator typeEval, Evaluator indexEval, Evaluator[] arguments) {
        super(contanEngine, token);
        this.typeEval = typeEval;
        this.indexEval = indexEval;
        this.arguments = arguments;
    }
    
    @Override
    public ContanObject<?> eval(Environment environment) {
        if (indexEval == null) {
            int startIndex = 0;
            CoroutineStatus coroutineStatus = environment.getCoroutineStatus(this);
            ContanObject<?>[] variables = new ContanObject<?>[arguments.length];
    
            if (coroutineStatus != null) {
                startIndex = (int) coroutineStatus.count;
                System.arraycopy(coroutineStatus.results, 0, variables, 0, startIndex);
            }
    
            for (int i = startIndex; i < arguments.length; i++) {
                ContanObject<?> result = arguments[i].eval(environment).createClone();
                result = ContanRuntimeUtil.removeReference(token, result);
        
                if (environment.hasYieldReturnValue() || result == ContanYieldObject.INSTANCE) {
                    ContanObject<?>[] results = new ContanObject<?>[i + 1];
                    System.arraycopy(variables, 0, results, 0, i);
            
                    environment.setCoroutineStatus(this, i, results);
                    return ContanYieldObject.INSTANCE;
                }
        
                variables[i] = result;
            }
    
    
            ContanObject<?> typeResult = typeEval.eval(environment);
    
            if (environment.hasYieldReturnValue()) {
                environment.setCoroutineStatus(this, arguments.length, variables);
                return ContanYieldObject.INSTANCE;
            }
            
            Class<?> clazz;
            if (typeResult.getBasedJavaObject() instanceof Class<?>) {
                clazz = (Class<?>) typeResult.getBasedJavaObject();
            } else {
                ContanRuntimeError.E0026.throwError("", null, token);
                return null;
            }
            
            Object array = Array.newInstance(clazz, arguments.length);
            
            for (int i = 0; i < arguments.length; i++) {
                try {
                    Array.set(array, i, variables[i].getBasedJavaObject());
                } catch (Exception e)  {
                    ContanRuntimeError.E0027.throwError("  Index : " + i, e, token);
                }
            }
            
            return new JavaClassInstance(contanEngine, array);
        } else {
            CoroutineStatus coroutineStatus = environment.getCoroutineStatus(this);
            
            ContanObject<?> indexResult;
            if (coroutineStatus == null) {
                indexResult = indexEval.eval(environment);
            } else {
                indexResult = coroutineStatus.results[0];
            }
            indexResult = ContanRuntimeUtil.removeReference(token, indexResult);
            
            if (environment.hasYieldReturnValue()) {
                return ContanYieldObject.INSTANCE;
            }
            
            if (indexResult.convertibleToLong()) {
                ContanRuntimeError.E0028.throwError("", null, token);
                return null;
            }
            
            long index = indexResult.asLong();
            
            if (index < 0L) {
                ContanRuntimeError.E0029.throwError("", null, token);
                return null;
            }
            
            if (index != (int) index) {
                ContanRuntimeError.E0030.throwError("", null, token);
                return null;
            }
    
            
            ContanObject<?> typeResult = typeEval.eval(environment);
            typeResult = ContanRuntimeUtil.removeReference(token, typeResult);
    
            if (environment.hasYieldReturnValue()) {
                environment.setCoroutineStatus(this, 0, indexResult);
                return ContanYieldObject.INSTANCE;
            }
    
            Class<?> clazz;
            if (typeResult.getBasedJavaObject() instanceof Class<?>) {
                clazz = (Class<?>) typeResult.getBasedJavaObject();
            } else {
                ContanRuntimeError.E0026.throwError("", null, token);
                return null;
            }
    
            Object array = Array.newInstance(clazz, (int) index);
            
            return new JavaClassInstance(contanEngine, array);
        }
    }
    
}
