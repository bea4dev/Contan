package org.contan_lang.variables.primitive;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;

public class ContanI64 extends ContanPrimitiveObject<Long> {
    
    public ContanI64(ContanEngine contanEngine, Long based) {
        super(contanEngine, based);
    }
    
    @Override
    public ContanObject<Long> createClone() {
        return new ContanI64(contanEngine, based);
    }
    
    @Override
    public long toLong() {
        return based;
    }
    
    @Override
    public double toDouble() {
        return (double) ((long) based);
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
    public ContanObject<?> invokeFunctionChild(ContanThread contanThread, Token functionName, ContanObject<?>... variables) {
        ContanRuntimeError.E0011.throwError("", null, functionName);
        return null;
    }
    
    @Override
    public String toString() {
        return based.toString();
    }
}
