package org.contan_lang.variables.primitive;

import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;

public class ContanVoidObject extends ContanPrimitiveObject<Boolean> {
    
    public static final ContanVoidObject INSTANCE = new ContanVoidObject();
    
    private ContanVoidObject() {super(null, false);}
    
    @Override
    public ContanObject<Boolean> createClone() {return INSTANCE;}
    
    @Override
    public String toString() {return "NULL";}
    
    @Override
    public ContanObject<?> invokeFunctionChild(ContanThread contanThread, Token functionName, ContanObject<?>... variables) {
        ContanRuntimeError.E0011.throwError("", null, functionName);
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
    public Boolean getBasedJavaObject() {
        return based;
    }
    
}
