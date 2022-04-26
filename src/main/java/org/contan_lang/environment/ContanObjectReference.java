package org.contan_lang.environment;

import org.contan_lang.ContanEngine;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanPrimitiveObject;

public class ContanObjectReference extends ContanPrimitiveObject<ContanObject<?>> {
    
    protected final String name;
    
    protected final Environment environment;
    
    public ContanObjectReference(ContanEngine contanEngine, String name, Environment environment, ContanObject<?> contanObject) {
        super(contanEngine, contanObject);
        this.name = name;
        this.environment = environment;
    }
    
    public String getName() {return name;}
    
    public Environment getEnvironment() {return environment;}
    
    public ContanObject<?> getContanVariable() throws IllegalAccessException {return based;}
    
    public void setContanVariable(ContanObject<?> contanObject) throws IllegalAccessException {this.based = contanObject;}
    
    @Override
    public Object getBasedJavaObject() {
        return based.getBasedJavaObject();
    }
    
    @Override
    public ContanObject<?> invokeFunction(Token functionName, ContanObject<?>... variables) {
        return based.invokeFunction(functionName, variables);
    }
    
    @Override
    public ContanObject<ContanObject<?>> createClone() {
        return this;
    }
    
    @Override
    public long asLong() {
        return based.asLong();
    }
    
    @Override
    public double asDouble() {
        return based.asDouble();
    }
    
    @Override
    public boolean convertibleToLong() {
        return based.convertibleToLong();
    }
    
    @Override
    public boolean convertibleToDouble() {
        return based.convertibleToDouble();
    }
    
}
