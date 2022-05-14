package org.contan_lang.environment;

import org.contan_lang.ContanEngine;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanPrimitiveObject;

public class ContanObjectReference extends ContanPrimitiveObject<Object> {
    
    protected final String name;

    protected final boolean constant;
    
    public ContanObjectReference(ContanEngine contanEngine, String name, ContanObject<?> contanObject) {
        super(contanEngine, contanObject);
        this.name = name;
        this.constant = false;
    }

    public ContanObjectReference(ContanEngine contanEngine, String name, ContanObject<?> contanObject, boolean constant) {
        super(contanEngine, contanObject);
        this.name = name;
        this.constant = constant;
    }
    
    public String getName() {return name;}

    public boolean isConst() {return constant;}

    public ContanObject<?> getContanObject() throws Exception {return (ContanObject<?>) based;}
    
    public void setContanObject(ContanObject<?> contanObject) throws Exception {this.based = contanObject;}
    
    @Override
    public Object getBasedJavaObject() {
        return ((ContanObject<?>) based).getBasedJavaObject();
    }
    
    @Override
    public ContanObject<?> invokeFunction(ContanThread contanThread, Token functionName, ContanObject<?>... variables) {
        return ((ContanObject<?>) based).invokeFunction(contanThread, functionName, variables);
    }
    
    @Override
    public ContanObject<Object> createClone() {
        return this;
    }
    
    @Override
    public long asLong() {
        return ((ContanObject<?>) based).asLong();
    }
    
    @Override
    public double asDouble() {
        return ((ContanObject<?>) based).asDouble();
    }
    
    @Override
    public boolean convertibleToLong() {
        return ((ContanObject<?>) based).convertibleToLong();
    }
    
    @Override
    public boolean convertibleToDouble() {
        return ((ContanObject<?>) based).convertibleToDouble();
    }
    
}
