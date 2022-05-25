package org.contan_lang.standard.functions;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.evaluators.FunctionBlock;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.JavaClassInstance;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;


public class Map extends FunctionBlock {
    
    public Map(ContanEngine contanEngine, Token functionName, Evaluator evaluator, Token... args) {
        super(contanEngine, functionName, evaluator, args);
    }
    
    @Override
    public ContanObject<?> eval(@Nullable Environment parentEnvironment, Token token, ContanThread contanThread, ContanObject<?>... contanObjects) {
        if (contanObjects.length != 0) {
            ContanRuntimeError.E0016.throwError("", null, token);
            return null;
        }
        
        java.util.Map<Object, Object> map = new HashMap<>();
        return new JavaClassInstance(contanThread.getContanEngine(), map);
    }
    
}
