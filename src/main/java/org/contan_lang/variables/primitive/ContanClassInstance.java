package org.contan_lang.variables.primitive;

import org.contan_lang.environment.Environment;
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

    public Environment getEnvironment() {return environment;}

}
