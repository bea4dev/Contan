package org.contan_lang.environment;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanClassInstance;
import org.contan_lang.variables.primitive.ContanFunctionExpression;
import org.contan_lang.variables.primitive.ContanVoidObject;
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
            if (contanObject == ContanVoidObject.INSTANCE) {
                Array.set(array, index, null);
            } else {
                if (contanObject instanceof ContanClassInstance || contanObject instanceof ContanFunctionExpression) {
                    Array.set(array, index, contanObject);
                } else {
                    Array.set(array, index, contanObject.getBasedJavaObject());
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            ContanRuntimeError.E0037.throwError("Index : " + index, e, tokens);
        } catch (IllegalArgumentException e) {
            ContanRuntimeError.E0038.throwError("Value type : " + contanObject.getBasedJavaObject().getClass(), e, tokens);
        } catch (Exception e) {
            ContanRuntimeError.E0041.throwError("", e, tokens);
        }
        super.based = contanObject;
    }
    
    @Override
    public ContanObject<?> getContanObject() throws Exception {
        try {
            Object result = Array.get(array, index);
            
            if (result == null) {
                return ContanVoidObject.INSTANCE;
            } else {
                if (result instanceof ContanObject<?>) {
                    return (ContanObject<?>) result;
                } else {
                    return new JavaClassInstance(contanEngine, result);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            ContanRuntimeError.E0037.throwError("Index : " + index, e, tokens);
            return null;
        }
    }
    
}
