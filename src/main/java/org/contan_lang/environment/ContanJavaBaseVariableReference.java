package org.contan_lang.environment;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeException;
import org.contan_lang.environment.ContanVariableReference;
import org.contan_lang.variables.ContanVariable;
import org.contan_lang.variables.primitive.JavaClassInstance;

import java.lang.reflect.Field;

public class ContanJavaBaseVariableReference extends ContanVariableReference {

    private final Field field;

    private final Object javaObject;

    public ContanJavaBaseVariableReference(ContanEngine contanEngine, String name, Environment environment, ContanVariable<?> contanVariable, Field field, Object javaObject) {
        super(contanEngine, name, environment, contanVariable);
        this.field = field;
        this.javaObject = javaObject;
    }

    @Override
    public void setContanVariable(ContanVariable<?> contanVariable) throws IllegalAccessException {
        super.setContanVariable(contanVariable);
        field.set(javaObject, contanVariable.getBasedJavaObject());
    }

    @Override
    public ContanVariable<?> getContanVariable() throws IllegalAccessException {
        return new JavaClassInstance(contanEngine, field.get(javaObject));
    }
}
