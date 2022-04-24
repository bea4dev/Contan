package org.contan_lang.variables.primitive;

import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanNullPointerException;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.environment.expection.ContanRuntimeException;
import org.contan_lang.environment.expection.ContanTypeConvertException;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanVariable;

public class ContanVoid extends ContanPrimitiveVariable<Boolean> {
    
    public static final ContanVoid INSTANCE = new ContanVoid();
    
    private ContanVoid() {super(null, false);}
    
    @Override
    public ContanVariable<Boolean> createClone() {return INSTANCE;}
    
    @Override
    public String toString() {return "NULL";}
    
    @Override
    public ContanVariable<?> invokeFunction(Token functionName, ContanVariable<?>... variables) {
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
    
}
