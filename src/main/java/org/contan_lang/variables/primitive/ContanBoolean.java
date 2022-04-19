package org.contan_lang.variables.primitive;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeException;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanVariable;

public class ContanBoolean extends ContanPrimitiveVariable<Boolean> {
    
    public ContanBoolean(ContanEngine contanEngine, Boolean based) {
        super(contanEngine, based);
    }
    
    @Override
    public ContanVariable<Boolean> createClone() {
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
    public ContanVariable<?> invokeFunction(Environment environment, String functionName, ContanVariable<?>... variables) {
        throw new ContanRuntimeException("");
    }
}
