package org.contan_lang.variables.primitive;

import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeException;
import org.contan_lang.environment.expection.ContanTypeConvertException;
import org.contan_lang.evaluators.ClassBlock;
import org.contan_lang.variables.ContanVariable;

public class ContanClassInstance extends ContanPrimitiveVariable<ClassBlock> {

    private final Environment environment;

    public ContanClassInstance(ClassBlock based, Environment environment) {
        super(based);
        this.environment = environment;
    }

    @Override
    public ContanVariable<ClassBlock> createClone() {
        return this;
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
    
    public Environment getEnvironment() {return environment;}

    @Override
    public ContanVariable<?> invokeFunction(Environment environment, String functionName, ContanVariable<?>... variables) {
        return based.invokeFunction(this.environment, functionName, variables);
    }

}
