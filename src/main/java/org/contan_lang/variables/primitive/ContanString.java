package org.contan_lang.variables.primitive;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;

public class ContanString extends ContanPrimitiveObject<String> {
    
    public ContanString(ContanEngine contanEngine, String based) {
        super(contanEngine, based);
    }
    
    @Override
    public ContanObject<String> createClone() {
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
    public ContanObject<?> invokeFunction(Token functionName, ContanObject<?>... variables) {
        ContanRuntimeError.E0011.throwError("", null, functionName);
        return null;
    }
    
    
    
    public static long asLong(String based) {
        if (!based.matches("[+-]?\\d+(?:\\.\\d+)?")) {
            return 0L;
        } else if (based.contains(".")) {
            return (long) Double.parseDouble(based);
        } else {
            return Long.parseLong(based);
        }
    }
    
    
    public static double asDouble(String based) {
        if (!based.matches("[+-]?\\d+(?:\\.\\d+)?")) {
            return 0.0;
        } else if (based.contains(".")) {
            return Double.parseDouble(based);
        } else {
            return (double) Long.parseLong(based);
        }
    }

}
