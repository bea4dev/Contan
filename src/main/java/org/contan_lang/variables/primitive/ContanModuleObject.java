package org.contan_lang.variables.primitive;

import org.contan_lang.ContanEngine;
import org.contan_lang.syntax.parser.ContanModule;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;

public class ContanModuleObject extends ContanPrimitiveObject<ContanModule> {

    public ContanModuleObject(ContanEngine contanEngine, ContanModule based) {
        super(contanEngine, based);
    }

    @Override
    public ContanObject<?> invokeFunction(Token functionName, ContanObject<?>... variables) {
        return based.invokeFunction(functionName, variables);
    }

    @Override
    public ContanObject<ContanModule> createClone() {
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