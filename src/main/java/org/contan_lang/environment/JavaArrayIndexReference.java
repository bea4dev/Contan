package org.contan_lang.environment;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.JavaClassInstance;

import java.lang.reflect.Array;

public class JavaArrayIndexReference extends ContanObjectReference {
    
    private final Object array;
    private final int index;
    private final Token[] tokens;

    public JavaArrayIndexReference(ContanEngine contanEngine, String name, ContanObject<?> contanObject, Object array, int index, Token... tokens) {
        super(contanEngine, name, contanObject);
        this.array = array;
        this.index = index;
        this.tokens = tokens;
    }
    
    @Override
    public void setContanObject(ContanObject<?> contanObject) throws Exception {
        super.setContanObject(contanObject);
        try {
            Array.set(array, index, contanObject.getBasedJavaObject());
        } catch (ArrayIndexOutOfBoundsException e) {
            ContanRuntimeError.E0038.throwError("Index : " + index, e, tokens);
        } catch (IllegalArgumentException e) {
            ContanRuntimeError.E0039.throwError("Value type : " + contanObject.getBasedJavaObject().getClass(), e, tokens);
        }
        super.based = contanObject;
    }
    
    @Override
    public ContanObject<?> getContanObject() throws Exception {
        try {
            return new JavaClassInstance(contanEngine, Array.get(array, index));
        } catch (ArrayIndexOutOfBoundsException e) {
            ContanRuntimeError.E0038.throwError("Index : " + index, e, tokens);
            return null;
        }
    }
    
}
