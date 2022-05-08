package org.contan_lang.variables.primitive;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.ClassBlock;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;

public class ContanClassInstance extends ContanPrimitiveObject<ClassBlock> {

    private final Environment environment;

    public ContanClassInstance(ContanEngine contanEngine, ClassBlock based, Environment environment) {
        super(contanEngine, based);
        this.environment = environment;
    }

    @Override
    public ContanObject<ClassBlock> createClone() {
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
    public ContanObject<?> invokeFunction(ContanThread contanThread, Token functionName, ContanObject<?>... variables) {
        return based.invokeFunction(contanThread, environment, functionName, variables);
    }

}
