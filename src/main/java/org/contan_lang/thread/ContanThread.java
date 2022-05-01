package org.contan_lang.thread;

import org.contan_lang.ContanEngine;

import java.util.concurrent.*;

public class ContanThread {
    
    private final ContanEngine contanEngine;
    
    private final ExecutorService javaThread = Executors.newSingleThreadExecutor();
    
    public ContanThread(ContanEngine contanEngine) {
        this.contanEngine = contanEngine;
    }
    
    
    public <T> T runTaskImmediately(Callable<T> task) throws ExecutionException, InterruptedException {
        Future<T> future = javaThread.submit(task);
        return future.get();
    }
    
    
    
}
