package org.contan_lang.syntax.parser.environment;

import org.contan_lang.syntax.exception.UnexpectedSyntaxException;
import org.contan_lang.syntax.tokens.Token;

import java.util.HashSet;
import java.util.Set;

public class ParserEnvironment {

    private final ParserEnvironment parent;

    private final ScopeType scopeType;

    private final Set<String> definedVariables = new HashSet<>();

    public ParserEnvironment(ParserEnvironment parent, ScopeType scopeType) {
        this.parent = parent;
        this.scopeType = scopeType;
    }

    public ScopeType getScopeType() {return scopeType;}

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

    public void checkHasVariable(Token token) throws UnexpectedSyntaxException {
        if (!hasVariable(token.getText())) {
            throw new UnexpectedSyntaxException("UNKNOWN VARIABLE : " + token.getText());
        }
    }

}
