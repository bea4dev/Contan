package org.contan_lang.variables.primitive;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.NumberType;

public class ContanString extends ContanPrimitiveObject<String> {
    
    public ContanString(ContanEngine contanEngine, String based) {
        super(contanEngine, based);
    }
    
    @Override
    public ContanObject<String> createClone() {
        return new ContanString(contanEngine, based);
    }
    
    @Override
    public long toLong() {
        return toLong(based);
    }
    
    @Override
    public double toDouble() {
        return toDouble(based);
    }
    
    @Override
    public boolean convertibleToLong() {
        if (!based.matches("[+-]?\\d+(?:\\.\\d+)?")) {
            return false;
        }
    
        NumberType numberType = NumberType.getType(Double.parseDouble(based));
        return numberType == NumberType.INTEGER || numberType == NumberType.LONG;
    }
    
    @Override
    public boolean convertibleToDouble() {
        return based.matches("[+-]?\\d+(?:\\.\\d+)?");
    }
    
    @Override
    public ContanObject<?> invokeFunctionChild(ContanThread contanThread, Token functionName, ContanObject<?>... variables) {
        ContanRuntimeError.E0011.throwError("", null, functionName);
        return null;
    }
    
    @Override
    public String toString() {
        return based;
    }
    
    public static long toLong(String based) {
        if (!based.matches("[+-]?\\d+(?:\\.\\d+)?")) {
            return 0L;
        } else if (based.contains(".")) {
            return (long) Double.parseDouble(based);
        } else {
            return Long.parseLong(based);
        }
    }
    
    
    public static double toDouble(String based) {
        if (!based.matches("[+-]?\\d+(?:\\.\\d+)?")) {
            return 0.0;
        } else if (based.contains(".")) {
            return Double.parseDouble(based);
        } else {
            return (double) Long.parseLong(based);
        }
    }

}
