package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.CoroutineStatus;
import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanBoolean;
import org.contan_lang.variables.primitive.ContanYieldObject;

public class EqualOperator extends BooleanOperator {
    
    public EqualOperator(ContanEngine contanEngine, Token token, Evaluator... operators) {
        super(contanEngine, token, operators);
    }
    
    @Override
    public ContanObject<Boolean> eval(Environment environment) {
        CoroutineStatus coroutineStatus = environment.getCoroutineStatus(this);
        ContanObject<?> contanObject0;
        ContanObject<?> contanObject1;
    
        if (coroutineStatus == null) {
            contanObject0 = operators[0].eval(environment);
            if (environment.hasYieldReturnValue() || contanObject0 == ContanYieldObject.INSTANCE) {
                environment.setCoroutineStatus(this, 0, ContanYieldObject.INSTANCE);
                environment.setReturnValue(ContanYieldObject.INSTANCE);
                return ContanYieldObject.INSTANCE;
            }
        
            contanObject1 = operators[1].eval(environment);
            if (environment.hasYieldReturnValue() || contanObject1 == ContanYieldObject.INSTANCE) {
                environment.setCoroutineStatus(this, 1, contanObject0);
                environment.setReturnValue(ContanYieldObject.INSTANCE);
                return ContanYieldObject.INSTANCE;
            }
        } else {
            if (coroutineStatus.count == 0) {
                contanObject0 = operators[0].eval(environment);
            } else {
                contanObject0 = coroutineStatus.results[0];
            }
            contanObject1 = operators[1].eval(environment);
        }
    
        Object first = contanObject0.getBasedJavaObject();
        Object second = contanObject1.getBasedJavaObject();
        
        if ((first instanceof Integer || first instanceof Long || first instanceof Float || first instanceof Double) &&
                (second instanceof Integer || second instanceof Long || second instanceof Float || second instanceof Double)) {
            
            if (first instanceof Float || first instanceof Double || second instanceof Float || second instanceof Double) {
                double temp1;
                
                if (first instanceof Integer) {
                    temp1 = (Integer) first;
                } else if (first instanceof Long) {
                    temp1 = (Long) first;
                } else if (first instanceof Float) {
                    temp1 = (Float) first;
                } else {
                    temp1 = (Double) first;
                }
                
                
                double temp2;
                
                if (second instanceof Integer) {
                    temp2 = (Integer) second;
                } else if (second instanceof Long) {
                    temp2 = (Long) second;
                } else if (second instanceof Float) {
                    temp2 = (Float) second;
                } else {
                    temp2 = (Double) second;
                }
                
                return new ContanBoolean(contanEngine, temp1 == temp2);
            }
            
            
            long temp1;
            
            if (first instanceof Integer) {
                temp1 = (Integer) first;
            } else {
                temp1 = (Long) first;
            }
    
    
            long temp2;
    
            if (second instanceof Integer) {
                temp2 = (Integer) second;
            } else {
                temp2 = (Long) second;
            }
            
            return new ContanBoolean(contanEngine, temp1 == temp2);
        }
        
        return new ContanBoolean(contanEngine, first.equals(second));
    }
    
}
