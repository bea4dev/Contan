package org.contan_lang.runtime;

import org.contan_lang.environment.Environment;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanClassInstance;
import org.contan_lang.variables.primitive.ContanFunctionExpression;

import java.util.ArrayList;
import java.util.List;

public class JavaCompletable {
    
    private final ContanClassInstance completable;
    
    private final List<ContanFunctionExpression> thenList = new ArrayList<>();
    
    private final List<ContanFunctionExpression> catchList = new ArrayList<>();
    
    private boolean isDone = false;
    
    private ContanObject<?> result = null;
    
    private List<Environment> awaitEnvironmentList = new ArrayList<>();
    
    public JavaCompletable(ContanClassInstance completable) {
        this.completable = completable;
    }
    
    public void addThen(ContanFunctionExpression functionExpression) {this.thenList.add(functionExpression);}
    
    public void addCatch(ContanFunctionExpression functionExpression) {this.catchList.add(functionExpression);}
    
    public ContanObject<?> getResult() {return result;}
    
    public ContanClassInstance getContanInstance() {return completable;}
    
    public boolean isDone() {return isDone;}
    
    public void complete(ContanThread contanThread, ContanObject<?> result) {
        this.result = result;
        isDone = true;
        
        for (ContanFunctionExpression functionExpression : thenList) {
            functionExpression.eval(contanThread, null, result);
        }
        
        for (Environment environment : awaitEnvironmentList) {
            Environment returnEnv = environment.getReturnEnvironment();
            if (returnEnv == null) {
                continue;
            }
            
            returnEnv.reRun();
        }
    }
    
}
