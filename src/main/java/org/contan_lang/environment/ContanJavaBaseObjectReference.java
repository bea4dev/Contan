package org.contan_lang.environment;

import org.contan_lang.ContanEngine;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanVoidObject;
import org.contan_lang.variables.primitive.JavaClassInstance;

import java.lang.reflect.Field;

public class ContanJavaBaseObjectReference extends ContanObjectReference {

    private final Field field;

    private final Object javaObject;

    public ContanJavaBaseObjectReference(ContanEngine contanEngine, String name, ContanObject<?> contanObject, Field field, Object javaObject) {
        super(contanEngine, name, contanObject);
        this.field = field;
        this.javaObject = javaObject;
    }

    @Override
    public void setContanObject(ContanObject<?> contanObject) throws Exception {
        super.setContanObject(contanObject);
        field.set(javaObject, contanObject.getBasedJavaObject());
        super.based = contanObject;
    }

    @Override
    public ContanObject<?> getContanObject() throws Exception {
        Object result = field.get(javaObject);

        if (result == null) {
            return ContanVoidObject.INSTANCE;
        } else if (result instanceof ContanObject<?>) {
            return (ContanObject<?>) result;
        } else {
            return new JavaClassInstance(contanEngine, result);
        }
    }
    
}
