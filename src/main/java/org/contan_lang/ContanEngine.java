package org.contan_lang;

import org.contan_lang.evaluators.ClassBlock;
import org.contan_lang.evaluators.PreLinkedCreateClassInstanceEvaluator;
import org.contan_lang.syntax.exception.UnexpectedSyntaxException;

import java.util.HashSet;
import java.util.Set;

public class ContanEngine {

    private final Set<ClassBlock> classBlocks;

    private final Set<String> classNames;

    private final Set<String> collidedClassNames;

    public ContanEngine() {
        this.classBlocks = new HashSet<>();
        this.classNames = new HashSet<>();
        this.collidedClassNames = new HashSet<>();
    }

    public void addClassBlock(ClassBlock classBlock) {
        if (classNames.contains(classBlock.getClassName().getText())) {
            collidedClassNames.add(classBlock.getClassName().getText());
        }

        classBlocks.add(classBlock);
        classNames.add(classBlock.getClassName().getText());
    }

    public void linkClass(PreLinkedCreateClassInstanceEvaluator classInstanceEvaluator) throws UnexpectedSyntaxException {
        classInstanceEvaluator.link(classBlocks, collidedClassNames);
    }

}
