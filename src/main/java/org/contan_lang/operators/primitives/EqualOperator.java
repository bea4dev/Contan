package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanVariable;
import org.contan_lang.variables.primitive.ContanBoolean;

public class EqualOperator extends BooleanOperator {
    
    public EqualOperator(ContanEngine contanEngine, Token token, Evaluator... operators) {
        super(contanEngine, token, operators);
    }
    
    @Override
    public ContanVariable<Boolean> eval(Environment environment) {
        Object first = operators[0].eval(environment).getBasedJavaObject();
        Object second = operators[1].eval(environment).getBasedJavaObject();
        
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
