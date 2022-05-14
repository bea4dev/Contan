package org.contan_lang.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface ContanTickBasedThread extends ContanThread {
    
    /**
     * Delays the execution of a task by a specified amount of time.
     *
     * @param task  Tasks to be performed with delay.
     *
     * @param delay Time to delay, specified in ticks.
     *              For the tick interval, check the execution environment settings.
     *
     * @return {@link Future} of task
     */
    <T> Future<T> scheduleTask(Callable<T> task, long delay);
    
}
