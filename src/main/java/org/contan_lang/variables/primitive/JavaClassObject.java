package org.contan_lang.variables.primitive;

import org.contan_lang.ContanEngine;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;

public class JavaClassObject extends ContanPrimitiveObject<Class<?>> {

    public JavaClassObject(ContanEngine contanEngine, Class<?> based) {
        super(contanEngine, based);
    }

    @Override
    public ContanObject<?> invokeFunction(ContanThread contanThread, Token functionName, ContanObject<?>... variables) {
        //Invoke java class static method
        return JavaClassInstance.invokeJavaMethod(contanEngine, based, null, functionName, variables);
    }

    @Override
    public ContanObject<Class<?>> createClone() {
        return this;
    }

    @Override
    public long asLong() {
        return 0;
    }

    @Override
    public double asDouble() {
        return 0;
    }

    @Override
    public boolean convertibleToLong() {
        return false;
    }

    @Override
    public boolean convertibleToDouble() {
        return false;
    }
}
