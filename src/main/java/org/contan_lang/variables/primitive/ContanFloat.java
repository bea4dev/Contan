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
    public ContanVariable<?> invokeFunction(Environment environment, String functionName, ContanVariable<?>... variables) {
        throw new ContanRuntimeException("");
    }

}
