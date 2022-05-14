package org.contan_lang.environment;

import org.contan_lang.ContanEngine;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.JavaClassInstance;

import java.util.List;

public class JavaListIndexReference extends ContanObjectReference {
    
    private final List<?> list;
    private final int index;
    
    public JavaListIndexReference(ContanEngine contanEngine, String name, ContanObject<?> contanObject, List<?> list, int index) {
        super(contanEngine, name, contanObject);
        this.list = list;
        this.index = index;
    }
    
    @Override
    public void setContanObject(ContanObject<?> contanObject) throws Exception {
        super.setContanObject(contanObject);
        list.getClass().getMethod("set", int.class, Object.class).invoke(list, index, contanObject.getBasedJavaObject());
        super.based = contanObject;
    }
    
    @Override
    public ContanObject<?> getContanObject() {
        return new JavaClassInstance(contanEngine, list.get(index));
    }
    
    
}
