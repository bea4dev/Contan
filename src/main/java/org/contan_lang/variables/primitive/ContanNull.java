package org.contan_lang.variables.primitive;

import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;

public class ContanNull extends ContanPrimitiveObject<Boolean> {
    
    public static final ContanNull INSTANCE = new ContanNull();
    
    private ContanNull() {super(null, false);}
    
    @Override
    public ContanObject<Boolean> createClone() {return INSTANCE;}
    
    @Override
    public String toString() {return "NULL";}
    
    @Override
    public ContanObject<?> invokeFunction(Token functionName, ContanObject<?>... variables) {
        ContanRuntimeError.E0011.throwError("", null, functionName);
        return null;
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

    @Override
    public Object getBasedJavaObject() {
        return this;
    }
}
