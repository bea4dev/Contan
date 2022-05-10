package org.contan_lang.evaluators;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanYieldObject;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

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
    
    
    public ContanObject<?> eval(@Nullable Environment parentEnvironment, Token token, ContanThread contanThread, ContanObject<?>... contanObjects) {

        Environment environment = new Environment(contanEngine, parentEnvironment, contanThread, evaluator, true);
        if (args.length != contanObjects.length) {
            ContanRuntimeError.E0016.throwError("", null, token);
        }
        
        for (int i = 0; i < args.length; i++) {
            environment.createVariable(args[i].getText(), contanObjects[i]);
        }
        
        ContanObject<?> variable = evaluator.eval(environment);
        if (environment.hasReturnValue()) {
            if (environment.hasYieldReturnValue()) {
                return environment.getCompletable().getContanInstance();
            } else {
                return environment.getReturnValue();
            }
        } else {
            return variable;
        }
    }
    
}
