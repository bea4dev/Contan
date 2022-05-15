package org.contan_lang.standard.functions;

import org.contan_lang.ContanEngine;
import org.contan_lang.ContanModule;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.evaluators.FunctionBlock;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanModuleObject;
import org.contan_lang.variables.primitive.ContanString;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;

public class ImportModule extends FunctionBlock {
    
    public ImportModule(ContanEngine contanEngine, Token functionName, Evaluator evaluator, Token... args) {
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
    
        ContanEngine contanEngine = contanThread.getContanEngine();
        
        ContanModule contanModule = contanEngine.getModule((String) contanObjects[0].getBasedJavaObject());
        
        if (contanModule == null) {
            ContanRuntimeError.E0033.throwError("", null, token);
            return null;
        }
        
        try {
            contanModule.initialize();
        } catch (ExecutionException | InterruptedException e) {
            ContanRuntimeError.E0034.throwError("", e, token);
        }
        return new ContanModuleObject(contanEngine, contanModule);
    }
}
