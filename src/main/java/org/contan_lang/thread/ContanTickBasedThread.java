package org.contan_lang.thread;

import java.util.concurrent.Callable;

public interface ContanTickBasedThread extends ContanThread {
    
    /**
     * Delays the execution of a task by a specified amount of time.
     *
     * @param task  Tasks to be performed with delay.
     *
     * @param delay Time to delay, specified in ticks.
     *              For the tick interval, check the execution environment settings.
     */
    <T> void scheduleTask(Callable<T> task, long delay);
    
}
