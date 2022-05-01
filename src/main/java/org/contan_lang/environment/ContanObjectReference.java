package org.contan_lang.environment;

import org.contan_lang.ContanEngine;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanPrimitiveObject;

public class ContanObjectReference extends ContanPrimitiveObject<Object> {
    
    protected final String name;
    
    protected final Environment environment;
    
    public ContanObjectReference(ContanEngine contanEngine, String name, Environment environment, ContanObject<?> contanObject) {
        super(contanEngine, contanObject);
        this.name = name;
        this.environment = environment;
    }
    
    public String getName() {return name;}
    
    public Environment getEnvironment() {return environment;}
    
    public ContanObject<?> getContanVariable() throws IllegalAccessException {return (ContanObject<?>) based;}
    
    public void setContanVariable(ContanObject<?> contanObject) throws IllegalAccessException {this.based = contanObject;}
    
    @Override
    public Object getBasedJavaObject() {
        return ((ContanObject<?>) based).getBasedJavaObject();
    }
    
    @Override
    public ContanObject<?> invokeFunction(Token functionName, ContanObject<?>... variables) {
        return ((ContanObject<?>) based).invokeFunction(functionName, variables);
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
