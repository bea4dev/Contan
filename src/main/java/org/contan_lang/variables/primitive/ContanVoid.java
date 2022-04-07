package org.contan_lang.variables.primitive;

import org.contan_lang.variables.ContanVariable;

public class ContanVoid extends ContanPrimitiveVariable<Boolean> {
    
    public static final ContanVoid INSTANCE = new ContanVoid();
    
    private ContanVoid() {super(false);}
    
    @Override
    public ContanVariable<Boolean> createClone() {return INSTANCE;}
    
    @Override
    public String toString() {
        return "NULL";
    }
}
