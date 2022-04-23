package org.contan_lang.variables;

import org.contan_lang.ContanEngine;
import org.contan_lang.evaluators.FunctionInvokable;

public interface ContanVariable<T> extends FunctionInvokable {

    ContanEngine getContanEngine();
    
    Object getBasedJavaObject();
    
    ContanVariable<T> createClone();
    
    long asLong();
    
    double asDouble();
    
    boolean convertibleToLong();
    
    boolean convertibleToDouble();
    
}
