package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.operators.Operator;
import org.contan_lang.variables.ContanVariable;
import org.contan_lang.variables.primitive.ContanFloat;
import org.contan_lang.variables.primitive.ContanInteger;
import org.contan_lang.variables.primitive.ContanString;
import org.contan_lang.variables.primitive.ContanVoid;

import java.util.ArrayList;
import java.util.List;

public class AddOperator extends Operator {
    
    public AddOperator(ContanEngine contanEngine, Evaluator... operators) {
        super(contanEngine, operators);
    }
    
    @Override
    public ContanVariable<?> eval(Environment environment) {
        double sum = 0.0;
        List<Object> results = new ArrayList<>();
        boolean hasString = false;
        boolean asFloat = false;
        for (Evaluator arg : operators) {
            ContanVariable<?> variable = arg.eval(environment);
            Object result = variable.getBasedJavaObject();
            
            if (variable instanceof ContanVoid) {
                results.add(variable.toString());
            } else {
                results.add(result);
            }
            
            if (!hasString) {
                boolean isInteger = result instanceof Integer;
                boolean isLong = result instanceof Long;
                boolean isFloat = result instanceof Float;
                boolean isDouble = result instanceof Double;
                if (!(isInteger || isLong || isFloat || isDouble)) {
                    hasString = true;
                }
                
                if (isFloat || isDouble) {
                    asFloat = true;
                }
            }
    
            if (!hasString) {
                if (result instanceof Integer) {
                    sum += (int) result;
                } else if (result instanceof Long) {
                    sum += (long) result;
                } else if (result instanceof Float) {
                    sum += (float) result;
                } else {
                    sum += (double) result;
                }
            }
        }
        
        if (hasString) {
            StringBuilder stringBuilder = new StringBuilder();
            results.forEach(stringBuilder::append);
            return new ContanString(stringBuilder.toString());
        } else {
            if (asFloat) {
                return new ContanFloat(sum);
            } else {
                return new ContanInteger((long) sum);
            }
        }
    }
    
}
