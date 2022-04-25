package org.contan_lang.evaluators;

import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;

public interface FunctionInvokable {

    ContanObject<?> invokeFunction(Token functionName, ContanObject<?>... variables);

}
