package org.contan_lang.variables.primitive;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;

public class ContanBoolean extends ContanPrimitiveObject<Boolean> {
    
    public ContanBoolean(ContanEngine contanEngine, Boolean based) {
        super(contanEngine, based);
    }
    
    @Override
    public ContanObject<Boolean> createClone() {
        return new ContanBoolean(contanEngine, based);
    }
    
    @Override
    public long toLong() {
        return based ? 1L : 0L;
    }
    
    @Override
    public double toDouble() {
        return based ? 1.0 : 0.0;
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
