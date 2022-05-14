package org.contan_lang.standard.functions;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.evaluators.FunctionBlock;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.JavaClassInstance;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class List extends FunctionBlock {
    
    public List(ContanEngine contanEngine, Token functionName, Evaluator evaluator, Token... args) {
        super(contanEngine, functionName, evaluator, args);
    }
    
    @Override
    public ContanObject<?> eval(@Nullable Environment parentEnvironment, Token token, ContanThread contanThread, ContanObject<?>... contanObjects) {
        java.util.List<Object> list = new ArrayList<>();
        
        for (ContanObject<?> arg : contanObjects) {
            list.add(arg.getBasedJavaObject());
        }
        
        return new JavaClassInstance(contanThread.getContanEngine(), list);
    }
}
