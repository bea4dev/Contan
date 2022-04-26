package org.contan_lang.runtime;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.ContanObjectReference;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;

public class ContanRuntimeUtil {

    public static ContanObject<?> removeReference(Token operationToken, ContanObject<?> contanObject) {

        if (contanObject instanceof ContanObjectReference) {
            try {
                contanObject = ((ContanObjectReference) contanObject).getContanVariable();
            } catch (IllegalAccessException e) {
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
                    contanObjects[i] = ((ContanObjectReference) contanObject).getContanVariable();
                } catch (IllegalAccessException e) {
                    ContanRuntimeError.E0015.throwError("", e, operationToken);
                }
            }
        }

        return contanObjects;
    }

}
