package org.contan_lang.evaluators;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanVariable;
import org.jetbrains.annotations.Nullable;

public class FunctionBlock {
    
    private final ContanEngine contanEngine;
    
    private final Token functionName;
    
    private final Evaluator evaluator;
    
    private final Token[] args;
    
    public FunctionBlock(ContanEngine contanEngine, Token functionName, Evaluator evaluator, Token... args) {
        this.contanEngine = contanEngine;
        this.functionName = functionName;
        this.evaluator = evaluator;
        this.args = args;
    }
    
    public Token getFunctionName() {return functionName;}
    
    public Token[] getArgs() {return args;}
    
    
    public ContanVariable<?> eval(@Nullable Environment parentEnvironment, Token token, ContanVariable<?>... contanVariables) {
        Environment environment = new Environment(contanEngine, parentEnvironment, true);
        if (args.length != contanVariables.length) {
            ContanRuntimeError.E0011.throwError("", null, token);
        }
        
        for (int i = 0; i < args.length; i++) {
            environment.createVariable(args[i].getText(), contanVariables[i]);
        }
        
        ContanVariable<?> variable = evaluator.eval(environment);
        if (environment.hasReturnValue()) {
            return environment.getReturnValue();
        } else {
            return variable;
        }
    }
    
}
