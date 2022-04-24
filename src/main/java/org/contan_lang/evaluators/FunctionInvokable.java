package org.contan_lang.evaluators;

import org.contan_lang.environment.Environment;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanVariable;

public interface FunctionInvokable {

    ContanVariable<?> invokeFunction(Token functionName, ContanVariable<?>... variables);

}
