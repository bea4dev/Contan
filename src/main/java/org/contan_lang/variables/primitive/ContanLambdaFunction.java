package org.contan_lang.variables.primitive;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.FunctionBlock;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;

public class ContanLambdaFunction extends ContanPrimitiveObject<FunctionBlock> {
    
    private final Environment lambdaEnvironment;
    
    public ContanLambdaFunction(ContanEngine contanEngine, FunctionBlock based, Environment lambdaEnvironment) {
        super(contanEngine, based);
        this.lambdaEnvironment = lambdaEnvironment;
    }
    
    @Override
    public ContanObject<?> invokeFunction(Token functionName, ContanObject<?>... variables) {
        ContanRuntimeError.E0011.throwError("", null, functionName);
        return null;
    }
    
    public ContanObject<?> eval(Token token, ContanObject<?>... variables){
        return based.eval(lambdaEnvironment, token, variables);
    }
    
    @Override
    public ContanObject<FunctionBlock> createClone() {
        return this;
    }
    
    @Override
    public long asLong() {
        return 0;
    }
    
    @Override
    public double asDouble() {
        return 0;
    }
    
    @Override
    public boolean convertibleToLong() {
        return false;
    }
    
    @Override
    public boolean convertibleToDouble() {
        return false;
    }
    
}
