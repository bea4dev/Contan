package org.contan_lang.environment;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.expection.ContanRuntimeException;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanVariable;
import org.contan_lang.variables.primitive.ContanClassInstance;
import org.contan_lang.variables.primitive.JavaClassInstance;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

public class EnvironmentUtil {
    
    public static ContanVariableReference getClassEnvironmentVariable(ContanEngine contanEngine, Environment environment, String[] tokens, Token name) {
        Token nameToken = name;
        if (name.getText().toCharArray()[0] == '.') {
            nameToken = new NameToken("data" + name.getText());
            tokens = nameToken.getText().split(Pattern.quote("."));
        }

        ContanVariable<?> currentVariable;
        ContanVariableReference contanVariableReference = environment.getVariable(tokens[0]);

        if (contanVariableReference == null) {

        }

        if (contanVariableReference != null) {
            currentVariable = contanVariableReference.getContanVariable();

            for (int i = 1; i < tokens.length; i++) {
                String token = tokens[i];

                if (currentVariable instanceof ContanClassInstance) {
                    ContanVariableReference ev = ((ContanClassInstance) currentVariable).getEnvironment().getVariable(token);
                    if (ev == null) {
                        break;
                    }

                    contanVariableReference = ev;
                    currentVariable = ev.getContanVariable();
                    if (i == tokens.length - 1) {
                        return contanVariableReference;
                    }
                } else if (currentVariable instanceof JavaClassInstance) {
                    try {
                        Object based = currentVariable.getBasedJavaObject();
                        Class<?> clazz = based.getClass();

                        Field field = clazz.getField(token);

                        currentVariable = new JavaClassInstance(field.get(based));
                        contanVariableReference = new ContanJavaBaseVariableReference(token, null, null, field, based);
                        if (i == tokens.length - 1) {
                            return contanVariableReference;
                        }
                    } catch (Exception e) {
                        break;
                    }
                } else {
                    break;
                }
            }
        }


        //For static field
        Class<?> clazz;
        if (tokens.length == 2) {
            clazz = contanEngine.getJavaClassFromName(tokens[0]);
        } else {
            try {
                String fieldName = nameToken.getText();
                clazz = Class.forName(fieldName.substring(0, fieldName.length() - tokens[tokens.length - 1].length() - 1));
            } catch (Exception e) {
                e.printStackTrace();
                throw new ContanRuntimeException("");
            }
        }

        if (clazz == null) {
            throw new ContanRuntimeException("");
        }

        Field field;
        try {
            field = clazz.getField(tokens[tokens.length - 1]);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ContanRuntimeException("");
        }

        return new ContanJavaBaseVariableReference(nameToken.getText(), null, null, field, null);
    }
    
}
