package org.contan_lang.environment;

import org.contan_lang.ContanEngine;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.JavaClassInstance;

import java.lang.reflect.Field;

public class ContanJavaBaseObjectReference extends ContanObjectReference {

    private final Field field;

    private final Object javaObject;

    public ContanJavaBaseObjectReference(ContanEngine contanEngine, String name, Environment environment, ContanObject<?> contanObject, Field field, Object javaObject) {
        super(contanEngine, name, environment, contanObject);
        this.field = field;
        this.javaObject = javaObject;
    }

    @Override
    public void setContanVariable(ContanObject<?> contanObject) throws IllegalAccessException {
        super.setContanVariable(contanObject);
        field.set(javaObject, contanObject.getBasedJavaObject());
    }

    @Override
    public ContanObject<?> getContanVariable() throws IllegalAccessException {
        return new JavaClassInstance(contanEngine, field.get(javaObject));
    }
}
