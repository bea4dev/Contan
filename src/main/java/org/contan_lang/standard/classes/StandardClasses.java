package org.contan_lang.standard.classes;

import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.ClassBlock;
import org.contan_lang.thread.ContanThread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class StandardClasses {
    
    public static ContanThread EMPTY_THREAD = new ContanThread() {
        @Override
        public <T> T runTaskImmediately(Callable<T> task) throws ExecutionException, InterruptedException {
            return null;
        }
    
        @Override
        public <T> Future<T> scheduleTask(Callable<T> task) {
            return null;
        }
    };

    public static ClassBlock COMPLETABLE = new Completable(null, "standard.Completable", new Environment(null, null, EMPTY_THREAD));

}
