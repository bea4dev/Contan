package org.contan_lang.variables.primitive;

import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanNullPointerException;
import org.contan_lang.environment.expection.ContanRuntimeException;
import org.contan_lang.environment.expection.ContanTypeConvertException;
import org.contan_lang.variables.ContanVariable;

public class ContanVoid extends ContanPrimitiveVariable<Boolean> {
    
    public static final ContanVoid INSTANCE = new ContanVoid();
    
    private ContanVoid() {super(null, false);}
    
    @Override
    public ContanVariable<Boolean> createClone() {return INSTANCE;}
    
    @Override
    public String toString() {return "NULL";}

    @Override
    public ContanVariable<?> invokeFunction(Environment environment, String functionName, ContanVariable<?>... variables) {
        throw new ContanNullPointerException("");
    }
    
    @Override
    public long asLong() {
        throw new ContanTypeConvertException("This type does not support conversion to numeric.");
    }
    
    @Override
    public double asDouble() {
        throw new ContanRuntimeException("This type does not support conversion to numeric.");
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
