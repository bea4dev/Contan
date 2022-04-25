package org.contan_lang.environment;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.expection.ContanRuntimeException;
import org.contan_lang.variables.ContanObject;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Environment {

    protected final ContanEngine contanEngine;
    
    protected final Environment parent;

    protected final boolean canHasReturnValue;
    
    protected final Map<String, ContanObjectReference> variableMap = new HashMap<>();
    
    protected ContanObject<?> returnValue = null;

    public Environment(ContanEngine contanEngine, @Nullable Environment parent) {
        this.contanEngine = contanEngine;
        this.parent = parent;
        this.canHasReturnValue = false;
    }

    public Environment(ContanEngine contanEngine, @Nullable Environment parent, boolean canHasReturnValue) {
        this.contanEngine = contanEngine;
        this.parent = parent;
        this.canHasReturnValue = canHasReturnValue;
    }
    
    public @Nullable Environment getParent() {return parent;}
    
    public ContanObject<?> getReturnValue() {return returnValue;}
    
    public boolean hasReturnValue() {
        if (canHasReturnValue) {
            return returnValue != null;
        }

        if (parent == null) {
            return false;
        }

        return parent.hasReturnValue();
    }
    
    public void setReturnValue(ContanObject<?> returnValue) {
        if (canHasReturnValue) {
            this.returnValue = returnValue;
            return;
        }

        if (parent == null) {
            return;//TODO
        }

        parent.setReturnValue(returnValue);
    }
    
    public @Nullable ContanObjectReference getVariable(String name) {
        ContanObjectReference variable = variableMap.get(name);
        if(variable != null) return variable;
        if(parent == null) return null;
        
        return parent.getVariable(name);
    }
    
    public void createVariable(String name, ContanObject<?> contanObject) {
        if (variableMap.containsKey(name)) return;
    
        ContanObjectReference contanVariableReference = new ContanObjectReference(contanEngine, name, this, contanObject);
        variableMap.put(name, contanVariableReference);
    }

    public Environment createMergedEnvironment(Environment environment) {
        Environment newEnv = new Environment(contanEngine, this);
        newEnv.variableMap.putAll(environment.variableMap);
        return newEnv;
    }
    
}
