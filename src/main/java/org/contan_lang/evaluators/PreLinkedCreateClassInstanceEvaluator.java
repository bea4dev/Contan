package org.contan_lang.evaluators;

import org.contan_lang.environment.Environment;
import org.contan_lang.syntax.exception.UnexpectedSyntaxException;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanVariable;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class PreLinkedCreateClassInstanceEvaluator implements Evaluator {

    private final String classPath;

    private final Token nameToken;

    private final Evaluator[] args;

    private ClassBlock classBlock = null;

    public PreLinkedCreateClassInstanceEvaluator(@Nullable String classPath, Token nameToken, Evaluator... args) {
        this.classPath = classPath;
        this.nameToken = nameToken;
        this.args = args;
    }

    public void link(Collection<ClassBlock> classBlocks, Collection<String> collidedClassName) throws UnexpectedSyntaxException {
        if (classPath == null) {
            if (collidedClassName.contains(nameToken.getText())) {
                throw new UnexpectedSyntaxException("");
            }
        } else {
            for (ClassBlock classBlock : classBlocks) {
                if (classBlock.getClassName().getText().equals(nameToken.getText())) {
                    this.classBlock = classBlock;
                    return;
                }
            }
        }

        for (ClassBlock classBlock : classBlocks) {
            if (!classBlock.getClassName().getText().equals(nameToken.getText())) {
                continue;
            }

            if (classPath != null) {
                if (!classBlock.getClassPath().equals(classPath)) {
                    continue;
                }
            }

            this.classBlock = classBlock;
            return;
        }

        throw new UnexpectedSyntaxException("");
    }

    @Override
    public ContanVariable<?> eval(Environment environment) {
        ContanVariable<?>[] variables = new ContanVariable<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            variables[i] = args[i].eval(environment).createClone();
        }

        return classBlock.createInstance(null, variables);
    }

}
