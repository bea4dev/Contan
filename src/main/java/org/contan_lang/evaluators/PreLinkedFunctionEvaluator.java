package org.contan_lang.evaluators;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.EnvironmentVariable;
import org.contan_lang.environment.expection.ContanRuntimeException;
import org.contan_lang.standard.functions.StandardFunctions;
import org.contan_lang.syntax.exception.UnexpectedSyntaxException;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanVariable;
import org.contan_lang.variables.primitive.ContanClassInstance;
import org.contan_lang.variables.primitive.JavaClassInstance;

import java.util.Collection;
import java.util.regex.Pattern;

public class PreLinkedFunctionEvaluator implements Evaluator {
    
    private final ContanEngine contanEngine;
    
    private final Token functionName;
    
    private final Evaluator[] args;
    
    private FunctionBlock functionBlock;

    private String[] tokens = null;
    
    public PreLinkedFunctionEvaluator(ContanEngine contanEngine, Token functionName, Evaluator... args) {
        this.contanEngine = contanEngine;
        this.functionName = functionName;
        this.args = args;
    }
    
    public Token getFunctionName() {return functionName;}
    
    public Evaluator[] getArgs() {return args;}
    
    
    public void link(Collection<FunctionBlock> functionBlocks) throws UnexpectedSyntaxException {
        String nameText = functionName.getText();
        if (nameText.contains(".")){
            if (nameText.toCharArray()[0] == '.') {
                tokens = ("data" + nameText).split(Pattern.quote("."));
            } else {
                tokens = nameText.split(Pattern.quote("."));
            }
            return;
        }

        for (FunctionBlock functionBlock : functionBlocks) {
            if (!functionBlock.getFunctionName().getText().equals(this.functionName.getText())) continue;
            
            int functionArgLength = functionBlock.getArgs().length;
            int thisArgLength = args.length;
            
            if (functionArgLength == thisArgLength) {
                this.functionBlock = functionBlock;
                return;
            }
        }
        
        this.functionBlock = StandardFunctions.FUNCTIONS.get(this.functionName.getText());
        if (functionBlock != null) return;
        
        throw new UnexpectedSyntaxException("");//TODO
    }
    
    @Override
    public ContanVariable<?> eval(Environment environment) {

        ContanVariable<?>[] variables = new ContanVariable<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            variables[i] = args[i].eval(environment).createClone();
        }

        if (tokens != null) {
            ContanVariable<?> currentVariable;
            EnvironmentVariable environmentVariable = environment.getVariable(tokens[0]);
            if (environmentVariable != null) {
                currentVariable = environmentVariable.getContanVariable();
    
                for (int i = 1; i < tokens.length; i++) {
                    String token = tokens[i];
        
                    if (i == tokens.length - 1) {
                        return currentVariable.invokeFunction(environment, token, variables);
                    } else {
                        if (currentVariable instanceof ContanClassInstance) {
                            EnvironmentVariable ev = ((ContanClassInstance) currentVariable).getEnvironment().getVariable(token);
                            if (ev == null) {
                                break;
                            }
                
                            currentVariable = ev.getContanVariable();
                        } else {
                            break;
                        }
                    }
                }
            }
    
            Class<?> clazz;
            if (tokens.length == 2) {
                clazz = contanEngine.getJavaClassFromName(tokens[0]);
            } else {
                try {
                    String name = functionName.getText();
                    clazz = Class.forName(name.substring(0, name.length() - tokens[tokens.length - 1].length() - 1));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new ContanRuntimeException("");
                }
            }
            
            return JavaClassInstance.invokeJavaMethod(clazz, null, environment, tokens[tokens.length - 1], variables);
        }

        return functionBlock.eval(environment, variables);
    }
    
}
