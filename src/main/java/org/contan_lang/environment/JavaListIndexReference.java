package org.contan_lang.environment;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanClassInstance;
import org.contan_lang.variables.primitive.ContanFunctionExpression;
import org.contan_lang.variables.primitive.ContanVoidObject;
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
            if (contanObject == ContanVoidObject.INSTANCE) {
                list.getClass().getMethod("set", int.class, Object.class).invoke(list, index, null);
            } else {
                if (contanObject instanceof ContanClassInstance || contanObject instanceof ContanFunctionExpression) {
                    list.getClass().getMethod("set", int.class, Object.class).invoke(list, index, contanObject);
                } else {
                    list.getClass().getMethod("set", int.class, Object.class).invoke(list, index, contanObject.getBasedJavaObject());
                }
            }
        } catch (IndexOutOfBoundsException e) {
            ContanRuntimeError.E0037.throwError("Index : " + index, e, tokens);
        } catch (IllegalArgumentException e) {
            ContanRuntimeError.E0038.throwError("Value type : " + contanObject.getBasedJavaObject().getClass(), e, tokens);
        } catch (Exception e) {
            ContanRuntimeError.E0042.throwError("", e, tokens);
        }
        super.based = contanObject;
    }
    
    @Override
    public ContanObject<?> getContanObject() {
        try {
            Object result = list.get(index);
            
            if (result == null) {
                return ContanVoidObject.INSTANCE;
            } else {
                if (result instanceof ContanObject<?>) {
                    return (ContanObject<?>) result;
                } else {
                    return new JavaClassInstance(contanEngine, result);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            ContanRuntimeError.E0037.throwError("Index : " + index, e, tokens);
            return null;
        }
    }
    
    
}
