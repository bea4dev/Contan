package org.contan_lang.operators.primitives;

import org.contan_lang.environment.Environment;
import org.contan_lang.environment.EnvironmentUtil;
import org.contan_lang.environment.EnvironmentVariable;
import org.contan_lang.environment.expection.ContanRuntimeException;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.operators.Operator;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanVariable;
import org.contan_lang.variables.primitive.ContanVoid;

import java.util.regex.Pattern;

public class SetValueOperator extends Operator {
    
    private final Token targetVariableName;
    private final String[] targetVariableTokens;
    private final Evaluator valueEval;
    
    public SetValueOperator(Token targetVariableName, Evaluator valueEval) {
        super();
        this.targetVariableName = targetVariableName;
        this.valueEval = valueEval;
    
        if (targetVariableName.getText().contains(".")) {
            this.targetVariableTokens = targetVariableName.getText().split(Pattern.quote("."));
        } else {
            this.targetVariableTokens = null;
        }
    }
    
    public Token getTargetVariableName() {return targetVariableName;}
    
    public Evaluator getValueEval() {return valueEval;}
    
    @Override
    public ContanVariable<?> eval(Environment environment) {
        EnvironmentVariable variable;
    
        if (targetVariableTokens == null) {
            variable = environment.getVariable(targetVariableName.getText());
        } else {
            variable = EnvironmentUtil.getClassEnvironmentVariable(environment, targetVariableTokens, targetVariableName);
        }
    
        if (variable == null) {
            throw new ContanRuntimeException("");
        }
        
        variable.setContanVariable(valueEval.eval(environment));
        
        return ContanVoid.INSTANCE;
    }
}
