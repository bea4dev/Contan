package org.contan_lang.environment;

import org.contan_lang.variables.ContanVariable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Environment {

    protected final Environment parent;
    
    protected final Map<String, EnvironmentVariable> variableMap = new HashMap<>();
    
    protected ContanVariable<?> returnValue = null;

    public Environment(@Nullable Environment parent) {
        this.parent = parent;
    }
    
    public @Nullable Environment getParent() {return parent;}
    
    public ContanVariable<?> getReturnValue() {return returnValue;}
    
    public boolean hasReturnValue() {return returnValue != null;}
    
    public void setReturnValue(ContanVariable<?> returnValue) {this.returnValue = returnValue;}
    
    public @Nullable EnvironmentVariable getVariable(String name) {
        EnvironmentVariable variable = variableMap.get(name);
        if(variable != null) return variable;
        if(parent == null) return null;
        
        return parent.getVariable(name);
    }
    
    public EnvironmentVariable createVariable(String name, ContanVariable<?> contanVariable) {
        EnvironmentVariable environmentVariable = new EnvironmentVariable(name, this, contanVariable);
        variableMap.put(name, environmentVariable);
        return environmentVariable;
    }

    public Environment createMergedEnvironment(Environment environment) {
        Environment newEnv = new Environment(this);
        newEnv.variableMap.putAll(environment.variableMap);
        return newEnv;
    }
    
}
