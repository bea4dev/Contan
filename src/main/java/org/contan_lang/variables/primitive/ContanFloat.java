package org.contan_lang.variables.primitive;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;

public class ContanFloat extends ContanPrimitiveObject<Double> {
    
    public ContanFloat(ContanEngine contanEngine, Double based) {
        super(contanEngine, based);
    }
    
    @Override
    public ContanObject<Double> createClone() {
        return new ContanFloat(contanEngine, based);
    }
    
    @Override
    public long asLong() {
        return (long) ((double) based);
    }
    
    @Override
    public double asDouble() {
        return based;
    }
    
    @Override
    public boolean convertibleToLong() {
        return true;
    }
    
    @Override
    public boolean convertibleToDouble() {
        return true;
    }
    
    @Override
    public ContanObject<?> invokeFunction(ContanThread contanThread, Token functionName, ContanObject<?>... variables) {
        ContanRuntimeError.E0011.throwError("", null, functionName);
        return null;
    }

}
