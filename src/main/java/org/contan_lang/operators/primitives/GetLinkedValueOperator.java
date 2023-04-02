package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.*;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.operators.Operator;
import org.contan_lang.runtime.ContanRuntimeUtil;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanVoidObject;
import org.contan_lang.variables.primitive.ContanYieldObject;

import java.util.List;
import java.util.Map;

public class GetLinkedValueOperator extends Operator {
    
    private final Token[] tokens;
    
    public GetLinkedValueOperator(ContanEngine contanEngine, Token[] tokens, Evaluator... operators) {
        super(contanEngine, tokens[0], operators);
        this.tokens = tokens;
    }
    
    @Override
    public ContanObject<?> eval(Environment environment) {
        ContanObject<?> leftResult;
        CoroutineStatus coroutineStatus = environment.getCoroutineStatus(this);
        
        if (coroutineStatus == null) {
            leftResult = operators[0].eval(environment);
        } else {
            leftResult = coroutineStatus.results[0];
        }
        leftResult = ContanRuntimeUtil.dereference(token, leftResult);
        
        if (environment.hasYieldReturnValue()) {
            return ContanYieldObject.INSTANCE;
        }
        
        
        ContanObject<?> keyResult = operators[1].eval(environment);
        keyResult = ContanRuntimeUtil.dereference(token, keyResult);
        
        if (environment.hasYieldReturnValue()) {
            environment.setCoroutineStatus(this, 0, leftResult);
            return ContanYieldObject.INSTANCE;
        }
        
        Object left = leftResult.getBasedJavaObject();
        Object key = keyResult.getBasedJavaObject();
        
        Token lastToken = tokens[tokens.length - 1];
        
        if (left instanceof List) {
            return new JavaListIndexReference(contanEngine, lastToken.getText(), ContanVoidObject.INSTANCE, (List<?>) left, toIndexInteger(key), tokens);
        } else if (left.getClass().isArray()) {
            return new JavaArrayIndexReference(contanEngine, lastToken.getText(), ContanVoidObject.INSTANCE, left, toIndexInteger(key), tokens);
        } else if (left instanceof Map) {
            return new JavaMapReference(contanEngine, lastToken.getText(), ContanVoidObject.INSTANCE, (Map<?, ?>) left, key, tokens);
        }
        
        ContanRuntimeError.E0043.throwError("", null, tokens);
        return ContanVoidObject.INSTANCE;
    }
    
    private int toIndexInteger(Object key) {
        int index;
        if (key instanceof Integer) {
            index = (Integer) key;
        } else if (key instanceof Long) {
            long temp = (Long) key;
        
            if ((Long) key != (int) temp) {
                ContanRuntimeError.E0030.throwError("", null, tokens[tokens.length - 1]);
                return 0;
            }
        
            index = (int) temp;
        } else {
            ContanRuntimeError.E0028.throwError("", null, tokens[tokens.length - 1]);
            return 0;
        }
        
        if (index < 0) {
            ContanRuntimeError.E0029.throwError("", null, tokens[tokens.length - 1]);
        }
        
        return index;
    }
    
}
