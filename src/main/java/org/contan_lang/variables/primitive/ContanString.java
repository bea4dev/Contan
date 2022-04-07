package org.contan_lang.variables.primitive;

import org.contan_lang.variables.ContanVariable;

public class ContanString extends ContanPrimitiveVariable<String> {
    
    public ContanString(String based) {
        super(based);
    }
    
    @Override
    public ContanVariable<String> createClone() {
        return new ContanString(based);
    }
    
}
