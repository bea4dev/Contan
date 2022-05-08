package org.contan_lang.environment;

import org.contan_lang.variables.ContanObject;

public class CoroutineStatus {

    public final int count;
    
    public final ContanObject<?>[] results;
    
    public CoroutineStatus(int count, ContanObject<?>... results) {
        this.count = count;
        this.results = results;
    }
    
}
