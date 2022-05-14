package org.contan_lang.environment;

import org.contan_lang.ContanEngine;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.JavaClassInstance;

import java.lang.reflect.Array;

public class JavaArrayIndexReference extends ContanObjectReference {
    
    private final Object array;
    private final int index;
    
    public JavaArrayIndexReference(ContanEngine contanEngine, String name, ContanObject<?> contanObject, Object array, int index) {
        super(contanEngine, name, contanObject);
        this.array = array;
        this.index = index;
    }
    
    @Override
    public void setContanObject(ContanObject<?> contanObject) throws Exception {
        super.setContanObject(contanObject);
        Array.set(array, index, contanObject.getBasedJavaObject());
        super.based = contanObject;
    }
    
    @Override
    public ContanObject<?> getContanObject() throws Exception {
        return new JavaClassInstance(contanEngine, Array.get(array, index));
    }
    
}
