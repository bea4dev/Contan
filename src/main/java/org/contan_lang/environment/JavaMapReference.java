package org.contan_lang.environment;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanVoidObject;
import org.contan_lang.variables.primitive.JavaClassInstance;

import java.util.Map;

public class JavaMapReference extends ContanObjectReference {
    
    private final Map<?, ?> map;
    private final Object key;
    private final Token[] tokens;
    
    public JavaMapReference(ContanEngine contanEngine, String name, ContanObject<?> contanObject, Map<?, ?> map, Object key, Token... tokens) {
        super(contanEngine, name, contanObject);
        this.map = map;
        this.key = key;
        this.tokens = tokens;
    }
    
    @Override
    public void setContanObject(ContanObject<?> contanObject) throws Exception {
        super.setContanObject(contanObject);
        
        try {
            if (contanObject == ContanVoidObject.INSTANCE) {
                map.getClass().getMethod("put", Object.class, Object.class).invoke(map, key, null);
            } else {
                map.getClass().getMethod("put", Object.class, Object.class).invoke(map, key, contanObject.getBasedJavaObject());
            }
        } catch (ClassCastException | IllegalArgumentException e) {
            ContanRuntimeError.E0038.throwError("", e, tokens);
        } catch (Exception e) {
            ContanRuntimeError.E0040.throwError("", e, tokens);
        }
    }
    
    @Override
    public ContanObject<?> getContanObject() throws Exception {
        try {
            Object result = map.getClass().getMethod("get", Object.class).invoke(map, key);
            
            if (result == null) {
                return ContanVoidObject.INSTANCE;
            } else {
                return new JavaClassInstance(contanEngine, result);
            }
        } catch (ClassCastException e) {
            ContanRuntimeError.E0037.throwError("Value type : " + key.getClass(), e, tokens);
        } catch (Exception e) {
            ContanRuntimeError.E0040.throwError("", e, tokens);
        }
        
        return null;
    }
    
}
