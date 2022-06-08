package org.contan_lang.variables.primitive;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.ClassBlock;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;

public class ContanClassObject extends ContanPrimitiveObject<ClassBlock> {

    public ContanClassObject(ContanEngine contanEngine, ClassBlock based) {
        super(contanEngine, based);
    }

    @Override
    public ContanObject<?> invokeFunctionChild(ContanThread contanThread, Token functionName, ContanObject<?>... variables) {
        ContanRuntimeError.E0011.throwError("", null, functionName);
        return null;
    }

    @Override
    public ContanObject<ClassBlock> createClone() {
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
        return "ContanClassObject{Class=" + based.getClassPath() + "}";
    }
}
