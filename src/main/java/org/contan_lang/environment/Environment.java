package org.contan_lang.environment;

import org.contan_lang.ContanEngine;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.runtime.ContanRuntimeUtil;
import org.contan_lang.runtime.JavaCompletable;
import org.contan_lang.standard.classes.StandardClasses;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanClassInstance;
import org.contan_lang.variables.primitive.ContanYieldObject;
import org.contan_lang.variables.primitive.JavaClassInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Environment {

    protected final ContanEngine contanEngine;
    
    protected final Environment parent;

    protected final boolean canHasReturnValue;
    
    protected final ContanThread contanThread;
    
    protected final Map<String, ContanObjectReference> variableMap = new HashMap<>();
    
    
    protected ContanThread callBackThread = null;
    
    protected JavaCompletable completable = null;
    
    protected Consumer<Environment> scheduleTask = null;
    
    protected ContanObject<?> returnValue = null;
    
    protected Environment returnEnvironmentCache = null;
    
    protected Map<Evaluator, CoroutineStatus> coroutineStatusMap = new HashMap<>();
    
    protected boolean isCoroutineEnvironment = false;

    public Environment(ContanEngine contanEngine, @Nullable Environment parent, @NotNull ContanThread contanThread) {
        this.contanEngine = contanEngine;
        this.parent = parent;
        this.contanThread = contanThread;
        this.canHasReturnValue = false;
    }

    public Environment(ContanEngine contanEngine, @Nullable Environment parent, @NotNull ContanThread contanThread, Consumer<Environment> scheduleTask, boolean canHasReturnValue) {
        this.contanEngine = contanEngine;
        this.parent = parent;
        this.contanThread = contanThread;
        this.canHasReturnValue = canHasReturnValue;
        if (canHasReturnValue) {
            this.completable = new JavaCompletable(StandardClasses.COMPLETABLE.createInstance(contanEngine, contanThread));
            completable.getContanInstance().getEnvironment().createOrSetVariable("javaCompletable", new JavaClassInstance(contanEngine, completable));
            
            if (parent == null) {
                callBackThread = contanEngine.getMainThread();
            } else {
                callBackThread = parent.getContanThread();
            }
            
            this.scheduleTask = scheduleTask;
        }
    }
    
    public @Nullable Environment getParent() {return parent;}
    
    public ContanObject<?> getReturnValue() {return returnValue;}
    
    public ContanEngine getContanEngine() {return contanEngine;}
    
    public ContanThread getContanThread() {return contanThread;}
    
    public JavaCompletable getCompletable() {return completable;}
    
    public void setScheduleTask(Consumer<Environment> scheduleTask) {this.scheduleTask = scheduleTask;}
    
    private boolean returnEnvInitialize = false;
    
    public void complete(ContanObject<?> result) {
        if (!canHasReturnValue) {
            Environment returnEnvironment = getReturnEnvironment();
            if (returnEnvironment == null) {
                return;
            }
            
            returnEnvironment.complete(result);
            return;
        }
        
        completable.complete(contanThread, result);
    }
    
    public void reRun() {
        if (scheduleTask == null) {
            return;
        }
        
        scheduleTask.accept(this);
    }
    
    public @Nullable Environment getReturnEnvironment() {
        if (returnEnvInitialize) {
            return returnEnvironmentCache;
        }
        
        Environment currentEnvironment = this;
        
        while (true) {
            if (currentEnvironment == null) {
                break;
            }
            
            if (currentEnvironment.canHasReturnValue) {
                break;
            }
            
            currentEnvironment = currentEnvironment.parent;
        }
        
        returnEnvironmentCache = currentEnvironment;
        returnEnvInitialize = true;
        
        return returnEnvironmentCache;
    }
    
    public boolean isCoroutineEnvironment() {
        if (canHasReturnValue) {
            return isCoroutineEnvironment;
        }
    
        Environment returnEnvironment = getReturnEnvironment();
        if (returnEnvironment == null) {
            return false;
        }
        
        return returnEnvironment.isCoroutineEnvironment;
    }
    
    public @Nullable CoroutineStatus getCoroutineStatus(Evaluator evaluator) {
        Environment returnEnvironment = getReturnEnvironment();
        if (returnEnvironment == null) {
            return null;
        }
        
        return returnEnvironment.coroutineStatusMap.get(evaluator);
    }
    
    public void setCoroutineStatus(Evaluator evaluator, int count, ContanObject<?>... results) {
        Environment returnEnvironment = getReturnEnvironment();
        if (returnEnvironment == null) {
            return;
        }
        
        returnEnvironment.coroutineStatusMap.put(evaluator, new CoroutineStatus(count, results));
    }
    
    public boolean hasReturnValue() {
        if (canHasReturnValue) {
            return returnValue != null;
        }
        
        Environment returnEnvironment = getReturnEnvironment();
        if (returnEnvironment == null) {
            return false;
        }
        
        return returnEnvironment.hasReturnValue();
    }
    
    public boolean hasYieldReturnValue() {
        if (canHasReturnValue) {
            return returnValue == ContanYieldObject.INSTANCE;
        }
        
        Environment returnEnvironment = getReturnEnvironment();
        if (returnEnvironment == null) {
            return false;
        }
        
        return returnEnvironment.returnValue == ContanYieldObject.INSTANCE;
    }
    
    public void setReturnValue(ContanObject<?> returnValue) {
        if (canHasReturnValue) {
            this.returnValue = returnValue;
            this.isCoroutineEnvironment = true;
            return;
        }
        
        Environment returnEnvironment = getReturnEnvironment();
        if (returnEnvironment == null) {
            return;
        }

        returnEnvironment.setReturnValue(returnValue);
    }
    
    public @Nullable ContanObjectReference getVariable(String name) {
        ContanObjectReference variable = variableMap.get(name);
        if(variable != null) return variable;
        if(parent == null) return null;
        
        return parent.getVariable(name);
    }
    
    public void createVariable(String name, ContanObject<?> contanObject) {
        if (variableMap.containsKey(name)) return;
    
        ContanObjectReference contanVariableReference = new ContanObjectReference(contanEngine, name, contanObject);
        variableMap.put(name, contanVariableReference);
    }
    
    public void createOrSetVariable(String name, ContanObject<?> contanObject) {
        ContanObjectReference reference = variableMap.computeIfAbsent(name, k -> new ContanObjectReference(contanEngine, name, contanObject));
        try {
            reference.setContanObject(contanObject);
        } catch (Exception e) {/**/}
    }

    public Environment createMergedEnvironment(Environment environment) {
        Environment newEnv = new Environment(contanEngine, this, contanThread);
        newEnv.variableMap.putAll(environment.variableMap);
        return newEnv;
    }
    
}
