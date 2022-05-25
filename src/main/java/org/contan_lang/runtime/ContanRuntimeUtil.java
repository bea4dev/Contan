package org.contan_lang.runtime;

import org.contan_lang.environment.ContanObjectReference;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanReferenceException;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.environment.expection.ContanRuntimeException;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.JavaClassInstance;
import org.jetbrains.annotations.Nullable;

public class ContanRuntimeUtil {

    public static ContanObject<?> removeReference(Token operationToken, ContanObject<?> contanObject) {

        if (contanObject instanceof ContanObjectReference) {
            try {
                contanObject = ((ContanObjectReference) contanObject).getContanObject();
            } catch (ContanRuntimeException e) {
                throw e;
            } catch (Exception e) {
                ContanRuntimeError.E0015.throwError("", e, operationToken);
            }
        }

        return contanObject;
    }

    public static ContanObject<?>[] removeReference(Token operationToken, ContanObject<?>... contanObjects) {

        for (int i = 0; i < contanObjects.length; i++) {
            ContanObject<?> contanObject = contanObjects[i];

            if (contanObject instanceof ContanObjectReference) {
                try {
                    contanObjects[i] = ((ContanObjectReference) contanObject).getContanObject();
                } catch (ContanRuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    ContanRuntimeError.E0015.throwError("", e, operationToken);
                }
            }
        }

        return contanObjects;
    }
    
    public static ContanObjectList getListInstance(Token operationToken, ContanObject<?> contanObject) {
        Object based = contanObject.getBasedJavaObject();
        if (based instanceof ContanObjectList) {
            return (ContanObjectList) based;
        }
        
        ContanRuntimeError.E0014.throwError("Java[ContanObjectList]", null, operationToken);
        return null;
    }
    
    public static @Nullable Object getJavaObjectFromEnvironment(Environment environment, String variableName) {
        ContanObjectReference reference = environment.getVariable(variableName);
        if (reference == null) {
            return null;
        }
        
        return reference.getBasedJavaObject();
    }
    
    public static void setJavaObjectToEnvironment(Environment environment, Object object, String variableName) {
        environment.createOrSetVariable(variableName, new JavaClassInstance(environment.getContanEngine(), object));
    }

}
