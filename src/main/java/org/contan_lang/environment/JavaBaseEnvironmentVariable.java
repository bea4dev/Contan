package org.contan_lang.environment;

import org.contan_lang.environment.expection.ContanRuntimeException;
import org.contan_lang.variables.ContanVariable;
import org.contan_lang.variables.primitive.JavaClassInstance;

import java.lang.reflect.Field;

public class JavaBaseEnvironmentVariable extends EnvironmentVariable {

    private final Field field;

    private final Object javaObject;

    public JavaBaseEnvironmentVariable(String name, Environment environment, ContanVariable<?> contanVariable, Field field, Object javaObject) {
        super(name, environment, contanVariable);
        this.field = field;
        this.javaObject = javaObject;
    }

    @Override
    public void setContanVariable(ContanVariable<?> contanVariable) {
        super.setContanVariable(contanVariable);
        try {
            field.set(javaObject, contanVariable.getBasedJavaObject());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ContanRuntimeException("");
        }
    }

    @Override
    public ContanVariable<?> getContanVariable() {
        try {
            return new JavaClassInstance(field.get(javaObject));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ContanRuntimeException("");
        }
    }
}
