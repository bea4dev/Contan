package org.contan_lang.variables;

import org.contan_lang.evaluators.FunctionInvokable;

public interface ContanVariable<T> extends FunctionInvokable {
    
    T getBasedJavaObject();
    
    void setBasedJavaObject(T basedJavaObject);
    
    ContanVariable<T> createClone();
    
    long asLong();
    
    double asDouble();
    
    boolean convertibleToLong();
    
    boolean convertibleToDouble();
    
}
