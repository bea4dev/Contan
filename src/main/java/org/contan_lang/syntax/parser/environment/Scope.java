package org.contan_lang.syntax.parser.environment;

import org.contan_lang.ContanEngine;
import org.contan_lang.evaluators.ClassBlock;
import org.contan_lang.evaluators.IfEvaluator;
import org.contan_lang.syntax.exception.ContanParseException;
import org.contan_lang.syntax.exception.ParserError;
import org.contan_lang.syntax.exception.UnexpectedSyntaxException;
import org.contan_lang.syntax.tokens.Token;

import java.util.HashSet;
import java.util.Set;

public class Scope {

    private final String rootName;

    private final Scope parent;

    private final ScopeType scopeType;

    private final Set<String> definedVariables = new HashSet<>();

    public final ClassBlock classBlock;

    private IfEvaluator previousIfEvaluator = null;

    public Scope(String rootName, Scope parent, ScopeType scopeType) {
        this(rootName, parent, scopeType, null);
    }

    public Scope(String rootName, Scope parent, ScopeType scopeType, ClassBlock classBlock) {
        this.rootName = rootName;
        this.parent = parent;
        this.scopeType = scopeType;
        this.classBlock = classBlock;
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
    
    public void checkHasVariable(ContanEngine contanEngine, Token token) throws ContanParseException {
        if (contanEngine.getRuntimeVariable(token.getText()) != null) {
            return;
        }

        if (hasVariable(token.getText())) {
            return;
        }

        //Find class scope
        Scope currentScope = this;
        do {
            if (currentScope.classBlock != null) {
                currentScope.classBlock.lazyCheckVariables.add(token);
                return;
            }

            currentScope = currentScope.parent;
        } while (currentScope != null);

        ParserError.E0001.throwError("", token);
    }

}
