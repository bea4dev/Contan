package org.contan_lang.variables.primitive;

import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeException;
import org.contan_lang.variables.ContanVariable;

public class ContanInteger extends ContanPrimitiveVariable<Long> {
    
    public ContanInteger(Long based) {
        super(based);
    }
    
    @Override
    public ContanVariable<Long> createClone() {
        return new ContanInteger(based);
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
    public ContanVariable<?> invokeFunction(Environment environment, String functionName, ContanVariable<?>... variables) {
        throw new ContanRuntimeException("");
    }

}
