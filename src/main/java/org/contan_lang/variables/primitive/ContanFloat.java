package org.contan_lang.variables.primitive;

import org.contan_lang.variables.ContanVariable;

public class ContanFloat extends ContanPrimitiveVariable<Double> {
    
    public ContanFloat(Double based) {
        super(based);
    }
    
    @Override
    public ContanVariable<Double> createClone() {
        return new ContanFloat(based);
    }
    
}
