package org.contan_lang.variables.primitive;

import org.contan_lang.ContanEngine;
import org.contan_lang.variables.ContanObject;

public abstract class ContanPrimitiveObject<T> implements ContanObject<T> {

    protected final ContanEngine contanEngine;
    
    protected T based;

    @Override
    public ContanEngine getContanEngine() {return contanEngine;}

    public ContanPrimitiveObject(ContanEngine contanEngine, T based) {
        this.contanEngine = contanEngine;
        this.based = based;
    }
    
    @Override
    public Object getBasedJavaObject() { return based; }
    
    
    @Override
    public String toString() {
        return based.toString();
    }
    
}
