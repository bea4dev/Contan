package org.contan_lang.variables.primitive;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.NumberType;

public class ContanF64 extends ContanPrimitiveObject<Double> {
    
    public ContanF64(ContanEngine contanEngine, Double based) {
        super(contanEngine, based);
    }
    
    @Override
    public ContanObject<Double> createClone() {
        return new ContanF64(contanEngine, based);
    }
    
    @Override
    public long toLong() {
        return (long) ((double) based);
    }
    
    @Override
    public double toDouble() {
        return based;
    }
    
    @Override
    public boolean convertibleToLong() {
        NumberType numberType = NumberType.getType(based);
        return numberType == NumberType.INTEGER || numberType == NumberType.LONG;
    }
    
    @Override
    public boolean convertibleToDouble() {
        return true;
    }
    
    @Override
    public ContanObject<?> invokeFunctionChild(ContanThread contanThread, Token functionName, ContanObject<?>... variables) {
        ContanRuntimeError.E0011.throwError("", null, functionName);
        return null;
    }
    
    @Override
    public String toString() {
        return based.toString();
    }
}
