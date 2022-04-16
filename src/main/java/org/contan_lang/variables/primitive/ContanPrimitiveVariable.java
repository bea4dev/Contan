package org.contan_lang.variables.primitive;

import org.contan_lang.evaluators.FunctionBlock;
import org.contan_lang.variables.ContanVariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ContanPrimitiveVariable<T> implements ContanVariable<T> {
    
    protected T based;
    
    public ContanPrimitiveVariable(T based) { this.based = based; }
    
    @Override
    public T getBasedJavaObject() { return based; }
    
    @Override
    public void setBasedJavaObject(T basedJavaObject) { this.based = basedJavaObject; }
    
    @Override
    public String toString() {
        return based.toString();
    }
    
}
