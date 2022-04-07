package org.contan_lang.evaluators;

import org.contan_lang.environment.Environment;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanVariable;

public class FunctionBlock {
    
    private final Token functionName;
    
    private final Evaluator evaluator;
    
    private final Token[] args;
    
    public FunctionBlock(Token functionName, Evaluator evaluator, Token... args) {
        this.functionName = functionName;
        this.evaluator = evaluator;
        this.args = args;
    }
    
    public Token getFunctionName() {return functionName;}
    
    public Token[] getArgs() {return args;}
    
    
    public ContanVariable<?> eval(ContanVariable<?>... contanVariables) {
        Environment environment = new Environment(null);
        if (args.length != contanVariables.length) {
            throw new IllegalStateException("");//TODO
        }
        
        for (int i = 0; i < args.length; i++) {
            environment.createVariable(args[i].getText(), contanVariables[i]);
        }
        
        return evaluator.eval(environment);
    }
    
}
