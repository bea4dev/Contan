package org.contan_lang.variables.primitive;

import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeException;
import org.contan_lang.variables.ContanVariable;

public class ContanBoolean extends ContanPrimitiveVariable<Boolean> {
    
    public ContanBoolean(Boolean based) {
        super(based);
    }
    
    @Override
    public ContanVariable<Boolean> createClone() {
        return new ContanBoolean(based);
    }

    @Override
    public ContanVariable<?> invokeFunction(Environment environment, String functionName, ContanVariable<?>... variables) {
        throw new ContanRuntimeException("");
    }
}
