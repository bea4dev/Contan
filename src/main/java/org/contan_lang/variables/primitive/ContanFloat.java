package org.contan_lang.variables.primitive;

import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeException;
import org.contan_lang.variables.ContanVariable;

public class ContanFloat extends ContanPrimitiveVariable<Double> {
    
    public ContanFloat(Double based) {
        super(based);
    }
    
    @Override
    public ContanVariable<Double> createClone() {
        return new ContanFloat(based);
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
    public ContanVariable<?> invokeFunction(Environment environment, String functionName, ContanVariable<?>... variables) {
        throw new ContanRuntimeException("");
    }

}
