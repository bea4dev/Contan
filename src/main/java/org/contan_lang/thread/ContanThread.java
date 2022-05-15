package org.contan_lang.thread;

import org.contan_lang.ContanEngine;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public interface ContanThread {
    
    /**
     * Immediately executes the given task and returns the result.
     * @param task
     * @param <T>
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    <T> T runTaskImmediately(Callable<T> task) throws ExecutionException, InterruptedException;
    
    <T> void scheduleTask(Callable<T> task);

    boolean shutdownWithAwait(long timeout, TimeUnit timeUnit) throws InterruptedException;
    
    ContanEngine getContanEngine();
    
}
