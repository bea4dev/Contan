package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.ContanObjectReference;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.operators.Operator;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;

public class GetValueOperator extends Operator {
    
    private final Token targetVariableNameToken;
    
    public GetValueOperator(ContanEngine contanEngine, Token targetVariableNameToken, Operator... operators) {
        super(contanEngine, targetVariableNameToken, operators);
        this.targetVariableNameToken = targetVariableNameToken;
    }
    
    public Token getTargetVariableNameToken() {return targetVariableNameToken;}
    
    @Override
    public ContanObject<?> eval(Environment environment) {
        ContanObjectReference variable = environment.getVariable(targetVariableNameToken.getText());
        
        if (variable == null) {
            ContanRuntimeError.E0001.throwError("", null, targetVariableNameToken);
            return null;
        }
        
        return variable;
    }
    
}
