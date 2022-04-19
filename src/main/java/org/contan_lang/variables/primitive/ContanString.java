package org.contan_lang.variables.primitive;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeException;
import org.contan_lang.environment.expection.ContanTypeConvertException;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanVariable;

public class ContanString extends ContanPrimitiveVariable<String> {
    
    public ContanString(ContanEngine contanEngine, String based) {
        super(contanEngine, based);
    }
    
    @Override
    public ContanVariable<String> createClone() {
        return new ContanString(contanEngine, based);
    }
    
    @Override
    public long asLong() {
        return asLong(based);
    }
    
    @Override
    public double asDouble() {
        return asDouble(based);
    }
    
    @Override
    public boolean convertibleToLong() {
        return based.matches("[+-]?\\d+(?:\\.\\d+)?");
    }
    
    @Override
    public boolean convertibleToDouble() {
        return based.matches("[+-]?\\d+(?:\\.\\d+)?");
    }
    
    @Override
    public ContanVariable<?> invokeFunction(Environment environment, String functionName, ContanVariable<?>... variables) {
        throw new ContanRuntimeException("");
    }
    
    
    
    public static long asLong(String based) {
        if (!based.matches("[+-]?\\d+(?:\\.\\d+)?")) {
            throw new ContanTypeConvertException("'" + based + "' is not numerical.");
        } else if (based.contains(".")) {
            return (long) Double.parseDouble(based);
        } else {
            return Long.parseLong(based);
        }
    }
    
    
    public static double asDouble(String based) {
        if (!based.matches("[+-]?\\d+(?:\\.\\d+)?")) {
            throw new ContanTypeConvertException("'" + based + "' is not numerical.");
        } else if (based.contains(".")) {
            return Double.parseDouble(based);
        } else {
            return (double) Long.parseLong(based);
        }
    }

}
