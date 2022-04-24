package org.contan_lang.variables.primitive;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.environment.expection.ContanRuntimeException;
import org.contan_lang.environment.expection.ContanRuntimeExceptions;
import org.contan_lang.environment.expection.ContanTypeConvertException;
import org.contan_lang.evaluators.ClassBlock;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanVariable;

public class ContanClassInstance extends ContanPrimitiveVariable<ClassBlock> {

    private final Environment environment;

    public ContanClassInstance(ContanEngine contanEngine, ClassBlock based, Environment environment) {
        super(contanEngine, based);
        this.environment = environment;
    }

    @Override
    public ContanVariable<ClassBlock> createClone() {
        return this;
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
    
    public Environment getEnvironment() {return environment;}
    
    @Override
    public ContanVariable<?> invokeFunction(Token functionName, ContanVariable<?>... variables) {
        ContanRuntimeError.E0011.throwError("", null, functionName);
        return null;
    }

}
