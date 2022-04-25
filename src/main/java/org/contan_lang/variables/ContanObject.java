package org.contan_lang.variables;

import org.contan_lang.ContanEngine;
import org.contan_lang.evaluators.FunctionInvokable;

public interface ContanObject<T> extends FunctionInvokable {

    ContanEngine getContanEngine();
    
    Object getBasedJavaObject();
    
    ContanObject<T> createClone();
    
    long asLong();
    
    double asDouble();
    
    boolean convertibleToLong();
    
    boolean convertibleToDouble();
    
}