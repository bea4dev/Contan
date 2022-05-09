package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.ContanObjectReference;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.operators.Operator;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.JavaClassInstance;

public class GetVariableOperator extends Operator {
    
    private final Token targetVariableNameToken;
    
    public GetVariableOperator(ContanEngine contanEngine, Token targetVariableNameToken, Operator... operators) {
        super(contanEngine, targetVariableNameToken, operators);
        this.targetVariableNameToken = targetVariableNameToken;
    }
    
    public Token getTargetVariableNameToken() {return targetVariableNameToken;}
    
    @Override
    public ContanObject<?> eval(Environment environment) {
        ContanObjectReference variable = environment.getVariable(targetVariableNameToken.getText());

        if (variable == null) {
            if (targetVariableNameToken.getText().equals("@THREAD")) {
                return new JavaClassInstance(contanEngine, environment.getContanThread());
            }

            variable = contanEngine.getRuntimeVariable(targetVariableNameToken.getText());
            if (variable != null) {
                return variable;
            }

            ContanRuntimeError.E0001.throwError("", null, targetVariableNameToken);
            return null;
        }
        
        return variable;
    }
    
}
