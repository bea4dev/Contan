package org.contan_lang.standard.functions;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.evaluators.FunctionBlock;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanString;
import org.contan_lang.variables.primitive.JavaClassObject;
import org.jetbrains.annotations.Nullable;

public class ImportJava extends FunctionBlock {

    public ImportJava(ContanEngine contanEngine, Token functionName, Evaluator evaluator, Token... args) {
        super(contanEngine, functionName, evaluator, args);
    }

    @Override
    public ContanObject<?> eval(@Nullable Environment parentEnvironment, Token token, ContanThread contanThread, ContanObject<?>... contanObjects) {
        if (contanObjects.length != 1) {
            ContanRuntimeError.E0016.throwError("", null, token);
            return null;
        }

        if (!(contanObjects[0] instanceof ContanString)) {
            ContanRuntimeError.E0016.throwError("", null, token);
            return null;
        }

        try {
            Class<?> clazz = Class.forName((String) contanObjects[0].getBasedJavaObject());
            return new JavaClassObject(contanThread.getContanEngine(), clazz);
        } catch (ClassNotFoundException e) {
            ContanRuntimeError.E0022.throwError("", e, token);
            return null;
        }
    }
}
