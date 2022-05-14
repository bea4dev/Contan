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
    
    private final List<FunctionExpressionWithThread> thenList = new ArrayList<>();
    
    private final List<FunctionExpressionWithThread> catchList = new ArrayList<>();
    
    private boolean isDone = false;
    
    private ContanObject<?> result = null;
    
    private final List<Environment> awaitEnvironmentList = new ArrayList<>();
    
    public JavaCompletable(ContanClassInstance completable) {
        this.completable = completable;
    }
    
    public void addThen(FunctionExpressionWithThread functionExpression) {this.thenList.add(functionExpression);}
    
    public void addCatch(FunctionExpressionWithThread functionExpression) {this.catchList.add(functionExpression);}

    public void addAwaitEnvironment(Environment environment) {this.awaitEnvironmentList.add(environment);}
    
    public ContanObject<?> getResult() {return result;}
    
    public ContanClassInstance getContanInstance() {return completable;}
    
    public boolean isDone() {return isDone;}
    
    public void complete(ContanThread contanThread, ContanObject<?> result) {
        this.result = result;
        isDone = true;
        
        for (FunctionExpressionWithThread functionExpression : thenList) {
            functionExpression.contanThread.scheduleTask(() ->
                functionExpression.functionExpression.eval(functionExpression.contanThread, null, result)
            );
        }
        
        for (Environment environment : awaitEnvironmentList) {
            Environment returnEnv = environment.getReturnEnvironment();
            if (returnEnv == null) {
                continue;
            }

            environment.setReturnValue(null);
            returnEnv.reRun();
        }
    }


    public static class FunctionExpressionWithThread {
        public final ContanThread contanThread;
        public final ContanFunctionExpression functionExpression;

        public FunctionExpressionWithThread(ContanThread contanThread, ContanFunctionExpression functionExpression) {
            this.contanThread = contanThread;
            this.functionExpression = functionExpression;
        }
    }
}
