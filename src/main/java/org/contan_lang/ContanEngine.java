package org.contan_lang;

import org.contan_lang.evaluators.ClassBlock;
import org.contan_lang.syntax.exception.ContanParseException;
import org.contan_lang.syntax.parser.Parser;
import org.contan_lang.thread.ContanThread;
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
    
    private final Map<String, ContanModule> moduleMap;
    
    private final ContanThread mainThread;
    
    public ContanEngine(ContanThread mainThread) {
        this.classBlocks = new HashSet<>();
        this.classNames = new HashSet<>();
        this.collidedClassNames = new HashSet<>();
        this.importedJavaClasses = new HashSet<>();
        this.javaClassMap = new HashMap<>();
        this.moduleMap = new HashMap<>();
        this.mainThread = mainThread;
    }
    
    public ContanEngine() {
        this.classBlocks = new HashSet<>();
        this.classNames = new HashSet<>();
        this.collidedClassNames = new HashSet<>();
        this.importedJavaClasses = new HashSet<>();
        this.javaClassMap = new HashMap<>();
        this.moduleMap = new HashMap<>();
        this.mainThread = new ContanThread(this);
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
    
    /**
     * Compile an executable module from source code.
     *
     * @param moduleName Name of the module. The name specified here will be used for module imports, etc. in the code.
     * @param sourceCode Source code of the string to be compiled.
     * @return Compiled executable module.
     * @throws ContanParseException Compile-time exception.
     *                              Thrown when syntax or spelling errors exist.
     */
    public ContanModule compile(String moduleName, String sourceCode) throws ContanParseException {
        Parser parser = new Parser(moduleName, this, sourceCode);
        ContanModule contanModule = parser.compile();
        moduleMap.put(moduleName, contanModule);
        
        return contanModule;
    }
    
    /**
     * Retrieves compiled modules.
     *
     * @param moduleName Module name specified at compile time.
     * @return {@link ContanModule}
     */
    public @Nullable ContanModule getModule(String moduleName) {
        return moduleMap.get(moduleName);
    }
    
}
