package org.contan_lang.variables.primitive;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.syntax.tokens.Token;
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
    public long asLong() {
        return based ? 1L : 0L;
    }
    
    @Override
    public double asDouble() {
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
    public ContanObject<?> invokeFunction(Token functionName, ContanObject<?>... variables) {
        ContanRuntimeError.E0011.throwError("", null, functionName);
        return null;
    }
}
