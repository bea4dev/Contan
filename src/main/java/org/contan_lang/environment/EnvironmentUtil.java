package org.contan_lang.environment;

import org.contan_lang.environment.expection.ContanRuntimeException;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanVariable;
import org.contan_lang.variables.primitive.ContanClassInstance;

import java.util.regex.Pattern;

public class EnvironmentUtil {
    
    public static EnvironmentVariable getClassEnvironmentVariable(Environment environment, String[] tokens, Token name) {
        ContanVariable<?> currentVariable;
        EnvironmentVariable environmentVariable = environment.getVariable(tokens[0]);
        if (environmentVariable == null) {
            throw new ContanRuntimeException("");
        }
        currentVariable = environmentVariable.getContanVariable();
        
        for (int i = 1; i < tokens.length; i++) {
            String token = tokens[i];
    
            if (currentVariable instanceof ContanClassInstance) {
                EnvironmentVariable ev = ((ContanClassInstance) currentVariable).getEnvironment().getVariable(token);
                if (ev == null) {
                    throw new ContanRuntimeException("");
                }
    
                currentVariable = ev.getContanVariable();
                if (i == tokens.length-1) {
                    return ev;
                }
            } else {
                throw new ContanRuntimeException("");
            }
        }
    
        throw new ContanRuntimeException("");
    }
    
}
