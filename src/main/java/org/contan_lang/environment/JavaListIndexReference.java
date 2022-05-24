package org.contan_lang.environment;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.JavaClassInstance;

import java.util.List;

public class JavaListIndexReference extends ContanObjectReference {
    
    private final List<?> list;
    private final int index;
    private final Token[] tokens;
    
    public JavaListIndexReference(ContanEngine contanEngine, String name, ContanObject<?> contanObject, List<?> list, int index, Token... tokens) {
        super(contanEngine, name, contanObject);
        this.list = list;
        this.index = index;
        this.tokens = tokens;
    }
    
    @Override
    public void setContanObject(ContanObject<?> contanObject) throws Exception {
        super.setContanObject(contanObject);
        try {
            list.getClass().getMethod("set", int.class, Object.class).invoke(list, index, contanObject.getBasedJavaObject());
        } catch (IndexOutOfBoundsException e) {
            ContanRuntimeError.E0038.throwError("Index : " + index, e, tokens);
        } catch (IllegalArgumentException e) {
            ContanRuntimeError.E0039.throwError("Value type : " + contanObject.getBasedJavaObject().getClass(), e, tokens);
        }
        super.based = contanObject;
    }
    
    @Override
    public ContanObject<?> getContanObject() {
        try {
            return new JavaClassInstance(contanEngine, list.get(index));
        } catch (IndexOutOfBoundsException e) {
            ContanRuntimeError.E0038.throwError("Index : " + index, e, tokens);
            return null;
        }
    }
    
    
}
