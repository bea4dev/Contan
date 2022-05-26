package org.contan_lang.variables;

import org.contan_lang.ContanEngine;
import org.contan_lang.evaluators.FunctionInvokable;

public interface ContanObject<T> extends FunctionInvokable {

    ContanEngine getContanEngine();
    
    T getBasedJavaObject();
    
    ContanObject<T> createClone();
    
    long toLong();
    
    double toDouble();
    
    boolean convertibleToLong();
    
    boolean convertibleToDouble();
    
}
