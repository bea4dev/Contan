package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.FunctionBlock;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanLambdaFunction;

public class DefineLambdaFunctionOperator extends DefineValueOperator {
    
    private final FunctionBlock functionBlock;
    
    public DefineLambdaFunctionOperator(ContanEngine contanEngine, Token token, FunctionBlock functionBlock) {
        super(contanEngine, token, null);
        this.functionBlock = functionBlock;
    }
    
    @Override
    public ContanObject<?> eval(Environment environment) {
        return new ContanLambdaFunction(contanEngine, functionBlock, environment);
    }
}
