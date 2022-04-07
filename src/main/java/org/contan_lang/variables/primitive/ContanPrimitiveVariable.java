package org.contan_lang.variables.primitive;

import org.contan_lang.variables.ContanVariable;

public abstract class ContanPrimitiveVariable<T> implements ContanVariable<T> {
    
    protected T based;
    
    public ContanPrimitiveVariable(T based) { this.based = based; }
    
    @Override
    public T getBasedJavaObject() { return based; }
    
    @Override
    public void setBasedJavaObject(T basedJavaObject) { this.based = basedJavaObject; }
    
}
