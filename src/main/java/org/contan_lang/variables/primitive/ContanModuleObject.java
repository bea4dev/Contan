package org.contan_lang.variables.primitive;

import org.contan_lang.ContanEngine;
import org.contan_lang.ContanModule;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;

public class ContanModuleObject extends ContanPrimitiveObject<ContanModule> {

    public ContanModuleObject(ContanEngine contanEngine, ContanModule based) {
        super(contanEngine, based);
    }

    @Override
    public ContanObject<?> invokeFunctionChild(ContanThread contanThread, Token functionName, ContanObject<?>... variables) {
        return based.invokeFunction(contanThread, functionName, variables);
    }

    @Override
    public ContanObject<ContanModule> createClone() {
        return null;
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
        return "ContanModuleObject{Module='" + based.getRootName() + "'}";
    }
    
}
