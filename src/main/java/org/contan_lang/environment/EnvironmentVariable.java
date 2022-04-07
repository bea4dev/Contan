package org.contan_lang.environment;

import org.contan_lang.variables.ContanVariable;

public class EnvironmentVariable {
    
    protected final String name;
    
    protected final Environment environment;
    
    protected ContanVariable<?> contanVariable;
    
    public EnvironmentVariable(String name, Environment environment, ContanVariable<?> contanVariable) {
        this.name = name;
        this.environment = environment;
        this.contanVariable = contanVariable;
    }
    
    public String getName() {return name;}
    
    public Environment getEnvironment() {return environment;}
    
    public ContanVariable<?> getContanVariable() {return contanVariable;}
    
    public void setContanVariable(ContanVariable<?> contanVariable) {this.contanVariable = contanVariable;}
    
}
