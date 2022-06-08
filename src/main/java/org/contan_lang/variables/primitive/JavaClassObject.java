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
    public ContanObject<?> invokeFunctionChild(ContanThread contanThread, Token functionName, ContanObject<?>... variables) {
        //Invoke java class static method
        return JavaClassInstance.invokeJavaMethod(contanEngine, based, null, functionName, variables);
    }

    @Override
    public ContanObject<Class<?>> createClone() {
        return this;
    }

    @Override
    public long toLong() {
        return 0;
    }

    @Override
    public double toDouble() {
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
    
    @Override
    public Object convertToJavaObject() {
        return this;
    }
    
    @Override
    public String toString() {
        return based.toString();
    }
    
}
