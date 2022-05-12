package org.contan_lang.environment;

import org.contan_lang.variables.ContanObject;

public class CoroutineStatus {

    public final long count;
    
    public final ContanObject<?>[] results;
    
    public CoroutineStatus(long count, ContanObject<?>... results) {
        this.count = count;
        this.results = results;
    }
    
}
