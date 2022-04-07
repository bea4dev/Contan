package org.contan_lang.variables.primitive;

import org.contan_lang.variables.ContanVariable;

public class ContanInteger extends ContanPrimitiveVariable<Long> {
    
    public ContanInteger(Long based) {
        super(based);
    }
    
    @Override
    public ContanVariable<Long> createClone() {
        return new ContanInteger(based);
    }
    
}
