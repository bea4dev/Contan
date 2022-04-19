package org.contan_lang.variables.primitive;

import org.contan_lang.ContanEngine;
import org.contan_lang.evaluators.FunctionBlock;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanVariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ContanPrimitiveVariable<T> implements ContanVariable<T> {

    protected final ContanEngine contanEngine;
    
    protected T based;

    @Override
    public ContanEngine getContanEngine() {return contanEngine;}

    public ContanPrimitiveVariable(ContanEngine contanEngine, T based) {
        this.contanEngine = contanEngine;
        this.based = based;
    }
    
    @Override
    public T getBasedJavaObject() { return based; }
    
    @Override
    public void setBasedJavaObject(T basedJavaObject) { this.based = basedJavaObject; }
    
    @Override
    public String toString() {
        return based.toString();
    }
    
}
