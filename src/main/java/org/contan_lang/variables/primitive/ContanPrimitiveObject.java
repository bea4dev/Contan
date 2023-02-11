package org.contan_lang.variables.primitive;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;

public abstract class ContanPrimitiveObject<T> implements ContanObject<T>, Evaluator {

    protected final ContanEngine contanEngine;
    
    protected T based;

    @Override
    public ContanEngine getContanEngine() {return contanEngine;}

    public ContanPrimitiveObject(ContanEngine contanEngine, T based) {
        this.contanEngine = contanEngine;
        this.based = based;
    }
    
    @Override
    public T getBasedJavaObject() { return based; }
    
    @Override
    public ContanObject<?> eval(Environment environment) {
        return ContanVoidObject.INSTANCE;
    }
    
    @Override
    public ContanObject<?> invokeFunction(ContanThread contanThread, Token functionName, ContanObject<?>... variables) {
        if (variables.length == 0) {
            switch (functionName.getText()) {
                
                case "toString": {
                    return new ContanString(contanEngine, this.toString());
                }
                
                case "toLong": {
                    /*
                    if (!this.convertibleToLong()) {
                        ContanRuntimeError.E0046.throwError("", null, functionName);
                    }*/
                    
                    return new ContanI64(contanEngine, this.toLong());
                }
                
                case "toDouble": {
                    /*
                    if (!this.convertibleToDouble()) {
                        ContanRuntimeError.E0047.throwError("", null, functionName);
                    }*/
                    
                    return new ContanF64(contanEngine, this.toDouble());
                }
                
                case "convertibleToLong": {
                    return new ContanBoolean(contanEngine, this.convertibleToLong());
                }
                
                case "convertibleToDouble": {
                    return new ContanBoolean(contanEngine, this.convertibleToDouble());
                }
                
            }
        }
        
        return invokeFunctionChild(contanThread, functionName, variables);
    }
    
    public abstract ContanObject<?> invokeFunctionChild(ContanThread contanThread, Token functionName, ContanObject<?>... variables);
    
}
