package org.contan_lang.evaluators;

import org.contan_lang.environment.Environment;
import org.contan_lang.standard.functions.StandardFunctions;
import org.contan_lang.syntax.exception.UnexpectedSyntaxException;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanVariable;

import java.util.Collection;

public class PreLinkedFunctionEvaluator implements Evaluator {
    
    private final Token functionName;
    
    private final Evaluator[] args;
    
    private FunctionBlock functionBlock;
    
    public PreLinkedFunctionEvaluator(Token functionName, Evaluator... args) {
        this.functionName = functionName;
        this.args = args;
    }
    
    public Token getFunctionName() {return functionName;}
    
    public Evaluator[] getArgs() {return args;}
    
    
    public void link(Collection<FunctionBlock> functionBlocks) throws UnexpectedSyntaxException {
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
        
        return functionBlock.eval(environment, variables);
    }
    
}
