package org.contan_lang.syntax.parser.environment;

import org.contan_lang.evaluators.IfEvaluator;
import org.contan_lang.syntax.exception.UnexpectedSyntaxException;
import org.contan_lang.syntax.tokens.Token;

import java.util.HashSet;
import java.util.Set;

public class Scope {

    private final String rootName;

    private final Scope parent;

    private final ScopeType scopeType;

    private final Set<String> definedVariables = new HashSet<>();
    
    private IfEvaluator previousIfEvaluator = null;

    public Scope(String rootName, Scope parent, ScopeType scopeType) {
        this.rootName = rootName;
        this.parent = parent;
        this.scopeType = scopeType;
    }

    public ScopeType getScopeType() {return scopeType;}

    public String getRootName() {return rootName;}

    public boolean hasVariable(String name) {
        if (definedVariables.contains(name)) {
            return true;
        }

        if (parent == null) {
            return false;
        }

        return parent.hasVariable(name);
    }

    public void addVariable(String name) {definedVariables.add(name);}
    
    public IfEvaluator getPreviousIfEvaluator() {return previousIfEvaluator;}
    
    public void setPreviousIfEvaluator(IfEvaluator previousIfEvaluator) {this.previousIfEvaluator = previousIfEvaluator;}
    
    public void checkHasVariable(Token token) throws UnexpectedSyntaxException {
        if (!hasVariable(token.getText()) && !token.getText().contains(".")) {
            throw new UnexpectedSyntaxException("UNKNOWN VARIABLE : " + token.getText());
        }
    }

}
