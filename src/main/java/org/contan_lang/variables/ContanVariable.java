package org.contan_lang.variables;

public interface ContanVariable<T> {
    
    T getBasedJavaObject();
    
    void setBasedJavaObject(T basedJavaObject);
    
    ContanVariable<T> createClone();
    
}
