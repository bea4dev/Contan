package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.ContanObjectReference;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.evaluators.FunctionBlock;
import org.contan_lang.operators.Operator;
import org.contan_lang.runtime.ContanRuntimeUtil;
import org.contan_lang.standard.functions.StandardFunctions;
import org.contan_lang.syntax.exception.ContanParseException;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanFunctionExpression;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class PreLinkedFunctionOperator extends Operator {
    
    private final ContanEngine contanEngine;
    
    private final Token functionName;
    
    private final Evaluator left;
    
    private final Evaluator[] args;
    
    private FunctionBlock functionBlock;
    
    private Environment moduleEnvironment;
    
    public PreLinkedFunctionOperator(ContanEngine contanEngine, Token functionName, @Nullable Evaluator left, Evaluator... args) {
        super(contanEngine, functionName, args);
        this.contanEngine = contanEngine;
        this.functionName = functionName;
        this.left = left;
        this.args = args;
    }
    
    public Token getFunctionName() {return functionName;}
    
    public Evaluator[] getArgs() {return args;}
    
    
    public void link(Collection<FunctionBlock> moduleFunctions, Environment moduleEnvironment) throws ContanParseException {
        if (left != null) return;
        
        for (FunctionBlock functionBlock : moduleFunctions) {
            if (!functionBlock.getFunctionName().getText().equals(this.functionName.getText())) continue;
            
            int functionArgLength = functionBlock.getArgs().length;
            int thisArgLength = args.length;
            
            if (functionArgLength == thisArgLength) {
                this.functionBlock = functionBlock;
                this.moduleEnvironment = moduleEnvironment;
                return;
            }
        }
        
        this.functionBlock = StandardFunctions.FUNCTIONS.get(this.functionName.getText());
        /*
        if (functionBlock == null) {
            ParserError.E0014.throwError("", functionName);
        }*/
    }
    
    @Override
    public ContanObject<?> eval(Environment environment) {

        ContanObject<?>[] variables = new ContanObject<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            variables[i] = args[i].eval(environment).createClone();
        }
        
        if (functionBlock != null) {
            ContanObject<?> returned = functionBlock.eval(moduleEnvironment, functionName, variables);
            if (returned instanceof ContanObjectReference) {
                try {
                    return ((ContanObjectReference) returned).getContanVariable();
                } catch (IllegalAccessException e) {
                    ContanRuntimeError.E0013.throwError("", e, token);
                    return null;
                }
            } else {
                return returned;
            }
        }
    
        
        if (left == null) {
            ContanObjectReference resultReference = environment.getVariable(functionName.getText());
            
            if (resultReference == null) {
                ContanRuntimeError.E0011.throwError("", null, functionName);
                return null;
            }
            
            ContanObject<?> result = ContanRuntimeUtil.removeReference(functionName, resultReference);
            
            if (result instanceof ContanFunctionExpression) {
                return ((ContanFunctionExpression) result).eval(functionName, variables);
            } else {
                ContanRuntimeError.E0011.throwError("", null, functionName);
                return null;
            }
        }
    
        ContanObject<?> leftResult = left.eval(environment);
        return leftResult.invokeFunction(functionName, variables);
    }
    
}
