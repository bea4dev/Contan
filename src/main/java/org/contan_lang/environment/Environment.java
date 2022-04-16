package org.contan_lang.environment;

import org.contan_lang.environment.expection.ContanRuntimeException;
import org.contan_lang.variables.ContanVariable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Environment {

    protected final Environment parent;

    protected final boolean canHasReturnValue;
    
    protected final Map<String, EnvironmentVariable> variableMap = new HashMap<>();
    
    protected ContanVariable<?> returnValue = null;

    public Environment(@Nullable Environment parent) {
        this.parent = parent;
        this.canHasReturnValue = false;
    }

    public Environment(@Nullable Environment parent, boolean canHasReturnValue) {
        this.parent = parent;
        this.canHasReturnValue = canHasReturnValue;
    }
    
    public @Nullable Environment getParent() {return parent;}
    
    public ContanVariable<?> getReturnValue() {return returnValue;}
    
    public boolean hasReturnValue() {
        if (canHasReturnValue) {
            return returnValue != null;
        }

        if (parent == null) {
            return false;
        }

        return parent.hasReturnValue();
    }
    
    public void setReturnValue(ContanVariable<?> returnValue) {
        if (canHasReturnValue) {
            this.returnValue = returnValue;
            return;
        }

        if (parent == null) {
            throw new ContanRuntimeException("");
        }

        parent.setReturnValue(returnValue);
    }
    
    public @Nullable EnvironmentVariable getVariable(String name) {
        EnvironmentVariable variable = variableMap.get(name);
        if(variable != null) return variable;
        if(parent == null) return null;
        
        return parent.getVariable(name);
    }
    
    public void createVariable(String name, ContanVariable<?> contanVariable) {
        if (variableMap.containsKey(name)) return;
    
        EnvironmentVariable environmentVariable = new EnvironmentVariable(name, this, contanVariable);
        variableMap.put(name, environmentVariable);
    }

    public Environment createMergedEnvironment(Environment environment) {
        Environment newEnv = new Environment(this);
        newEnv.variableMap.putAll(environment.variableMap);
        return newEnv;
    }
    
}
