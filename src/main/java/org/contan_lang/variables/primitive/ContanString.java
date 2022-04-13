package org.contan_lang.variables.primitive;

import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeException;
import org.contan_lang.variables.ContanVariable;

public class ContanString extends ContanPrimitiveVariable<String> {
    
    public ContanString(String based) {
        super(based);
    }
    
    @Override
    public ContanVariable<String> createClone() {
        return new ContanString(based);
    }

    @Override
    public ContanVariable<?> invokeFunction(Environment environment, String functionName, ContanVariable<?>... variables) {
        throw new ContanRuntimeException("");
    }

}
