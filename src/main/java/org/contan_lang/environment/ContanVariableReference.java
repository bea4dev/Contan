package org.contan_lang.environment;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.variables.ContanVariable;
import org.contan_lang.variables.primitive.ContanPrimitiveVariable;

public class ContanVariableReference extends ContanPrimitiveVariable<ContanVariable<?>> {
    
    protected final String name;
    
    protected final Environment environment;
    
    protected ContanVariable<?> contanVariable;
    
    public ContanVariableReference(ContanEngine contanEngine, String name, Environment environment, ContanVariable<?> contanVariable) {
        super(contanEngine, contanVariable);
        this.name = name;
        this.environment = environment;
        this.contanVariable = contanVariable;
    }
    
    public String getName() {return name;}
    
    public Environment getEnvironment() {return environment;}
    
    public ContanVariable<?> getContanVariable() {return contanVariable;}
    
    public void setContanVariable(ContanVariable<?> contanVariable) {this.contanVariable = contanVariable;}
    
    @Override
    public Object getBasedJavaObject() {
        return based.getBasedJavaObject();
    }
    
    @Override
    public ContanVariable<?> invokeFunction(String functionName, ContanVariable<?>... variables) {
        return based.invokeFunction(functionName, variables);
    }
    
    @Override
    public ContanVariable<ContanVariable<?>> createClone() {
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
