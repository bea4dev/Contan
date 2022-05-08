package org.contan_lang.evaluators;

import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;

public interface FunctionInvokable {

    ContanObject<?> invokeFunction(ContanThread contanThread, Token functionName, ContanObject<?>... variables);

}
