package org.contan_lang.variables.primitive;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeException;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanVariable;

public class ContanFloat extends ContanPrimitiveVariable<Double> {
    
    public ContanFloat(ContanEngine contanEngine, Double based) {
        super(contanEngine, based);
    }
    
    @Override
    public ContanVariable<Double> createClone() {
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
    public ContanVariable<?> invokeFunction(String functionName, ContanVariable<?>... variables) {
        throw new ContanRuntimeException("");
    }

}
