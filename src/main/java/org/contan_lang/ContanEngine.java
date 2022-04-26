package org.contan_lang;

import org.contan_lang.evaluators.ClassBlock;
import org.contan_lang.operators.primitives.CreateClassInstanceOperator;
import org.contan_lang.syntax.exception.ContanParseException;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ContanEngine {

    private final Set<ClassBlock> classBlocks;

    private final Set<String> classNames;

    private final Set<String> collidedClassNames;
    
    private final Set<Class<?>> importedJavaClasses;
    
    private final Map<String, Class<?>> javaClassMap;
    
    public final Test test = new Test();
    
    public ContanEngine() {
        this.classBlocks = new HashSet<>();
        this.classNames = new HashSet<>();
        this.collidedClassNames = new HashSet<>();
        this.importedJavaClasses = new HashSet<>();
        this.javaClassMap = new HashMap<>();
    }

    public void addClassBlock(ClassBlock classBlock) {
        if (classNames.contains(classBlock.getClassName().getText())) {
            collidedClassNames.add(classBlock.getClassName().getText());
        }

        classBlocks.add(classBlock);
        classNames.add(classBlock.getClassName().getText());
    }
    
    public void addJavaClass(String classPath) throws Exception {
        Class<?> clazz = Class.forName(classPath);
    
        if (classNames.contains(clazz.getSimpleName())) {
            collidedClassNames.add(clazz.getSimpleName());
        }
        
        classNames.add(clazz.getSimpleName());
        importedJavaClasses.add(clazz);
        javaClassMap.put(clazz.getSimpleName(), clazz);
    }
    
    public @Nullable Class<?> getJavaClassFromName(String className) {
        return javaClassMap.get(className);
    }

    public static String test(double i){
        return i == 1.0 ? "JAVA!" : "NO!!";
    }
    
    public static class Test {
        
        public final String test = "TEST!";
        
        public void test() {
            System.out.println("TEST!!!!");
        }
        
    }
    
}
