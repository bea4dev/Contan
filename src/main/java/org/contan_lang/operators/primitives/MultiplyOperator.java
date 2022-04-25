package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.operators.Operator;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanFloat;
import org.contan_lang.variables.primitive.ContanInteger;

public class MultiplyOperator extends Operator {
    
    public MultiplyOperator(ContanEngine contanEngine, Token token, Evaluator... operators) {
        super(contanEngine, token, operators);
    }
    
    @Override
    public ContanObject<?> eval(Environment environment) {
        Object first = operators[0].eval(environment).getBasedJavaObject();
        Object second = operators[0].eval(environment).getBasedJavaObject();
    
        if ((first instanceof Integer || first instanceof Long || first instanceof Float || first instanceof Double) &&
                (second instanceof Integer || second instanceof Long || second instanceof Float || second instanceof Double)) {
        
            if (first instanceof Float || first instanceof Double || second instanceof Float || second instanceof Double) {
                double sum = 0.0;
            
                if (first instanceof Integer) {
                    sum += (Integer) first;
                } else if (first instanceof Long) {
                    sum += (Long) first;
                } else if (first instanceof Float) {
                    sum += (Float) first;
                } else {
                    sum += (Double) first;
                }
            
                if (second instanceof Integer) {
                    sum += (Integer) second;
                } else if (second instanceof Long) {
                    sum += (Long) second;
                } else if (second instanceof Float) {
                    sum += (Float) second;
                } else {
                    sum += (Double) second;
                }
            
                return new ContanFloat(contanEngine, sum);
            }
        
        
            long sum = 0L;
        
            if (first instanceof Integer) {
                sum += (Integer) first;
            } else {
                sum += (Long) first;
            }
        
            if (second instanceof Integer) {
                sum += (Integer) second;
            } else {
                sum += (Long) second;
            }
        
            return new ContanInteger(contanEngine, sum);
        }
    
        ContanRuntimeError.E0002.throwError(System.lineSeparator() + "Left : " + first.toString()
                                                + System.lineSeparator() + "Right : " + second.toString(), null, token);
        return null;
    }
}
