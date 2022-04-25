package org.contan_lang.variables.primitive;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;

public class ContanInteger extends ContanPrimitiveObject<Long> {
    
    public ContanInteger(ContanEngine contanEngine, Long based) {
        super(contanEngine, based);
    }
    
    @Override
    public ContanObject<Long> createClone() {
        return new ContanInteger(contanEngine, based);
    }
    
    @Override
    public long asLong() {
        return based;
    }
    
    @Override
    public double asDouble() {
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
    public ContanObject<?> invokeFunction(Token functionName, ContanObject<?>... variables) {
        ContanRuntimeError.E0011.throwError("", null, functionName);
        return null;
    }

}
