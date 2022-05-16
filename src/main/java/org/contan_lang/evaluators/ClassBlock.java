package org.contan_lang.evaluators;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.runtime.ContanRuntimeUtil;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanClassInstance;
import org.contan_lang.variables.primitive.ContanClassObject;
import org.contan_lang.variables.primitive.ContanVoidObject;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ClassBlock {

    private final Token className;

    private final String classPath;

    private final List<Evaluator> initializers;

    private final Map<String, List<FunctionBlock>> functionMap;

    private final Token[] initializeArgs;
    
    private ClassBlock superClass = null;
    
    private final Evaluator superClassEval;

    private final Environment moduleEnvironment;

    public ClassBlock(Token className, String classPath, Environment moduleEnvironment, @Nullable Evaluator superClassEval, Token... initializeArgs) {
        this.className = className;
        this.classPath = classPath;
        this.initializers = new ArrayList<>();
        this.initializeArgs = initializeArgs;
        this.moduleEnvironment = moduleEnvironment;
        this.superClassEval = superClassEval;
        this.functionMap = new HashMap<>();
    }

    public Token getClassName() {return className;}

    public String getClassPath() {return classPath;}

    public Token[] getInitializeArgs() {return initializeArgs;}

    public void addInitializer(Evaluator initializers) {this.initializers.add(initializers);}

    public ClassBlock getSuperClass() {return superClass;}

    public void evalSuperClass(Environment environment) {
        if (superClassEval == null) {
            return;
        }
        
        ContanObject<?> extendsResult = superClassEval.eval(environment);
        extendsResult = ContanRuntimeUtil.removeReference(className, extendsResult);
        
        if (!(extendsResult.getBasedJavaObject() instanceof ClassBlock)) {
            ContanRuntimeError.E0035.throwError("", null, className);
            return;
        }
        
        this.superClass = (ClassBlock) extendsResult.getBasedJavaObject();
    }

    public void addFunctionBlock(FunctionBlock functionBlock) {
        List<FunctionBlock> functions = functionMap.computeIfAbsent(functionBlock.getFunctionName().getText(), k -> new ArrayList<>());
        functions.add(functionBlock);
    }

    public ContanClassInstance createInstance(ContanEngine contanEngine, ContanThread contanThread, ContanObject<?>... contanObjects) {
        Environment environment = new Environment(contanEngine, moduleEnvironment, contanThread);

        ContanClassInstance instance = new ContanClassInstance(contanEngine,this, environment);
        environment.createConstVariable("this", instance);

        for (int i = 0; i < initializeArgs.length; i++) {
            if (i < contanObjects.length) {
                environment.createVariable(initializeArgs[i].getText(), contanObjects[i]);
            } else {
                environment.createVariable(initializeArgs[i].getText(), ContanVoidObject.INSTANCE);
            }
        }


        ClassBlock currentClass = this;
        List<ClassBlock> initializeClasses = new ArrayList<>();
        initializeClasses.add(this);

        while (true) {
            currentClass = currentClass.superClass;

            if (currentClass == null) {
                break;
            }

            initializeClasses.add(currentClass);
        }
        Collections.reverse(initializeClasses);

        for (ClassBlock classBlock : initializeClasses) {
            for (Evaluator evaluator : classBlock.initializers) {
                evaluator.eval(environment);
            }
        }

        return instance;
    }


    public ContanObject<?> invokeFunction(ContanThread contanThread, Environment classInstanceEnvironment, Token functionName, boolean ignoreNotFound, ContanObject<?>... variables) {
        List<FunctionBlock> functions = functionMap.get(functionName.getText());
        if (functions == null) {
            if (!ignoreNotFound && superClass == null){
                ContanRuntimeError.E0011.throwError("", null, functionName);
                return ContanVoidObject.INSTANCE;
            }
        } else {
            for (FunctionBlock functionBlock : functions) {
                if (functionBlock.getArgs().length == variables.length) {
                    return functionBlock.eval(classInstanceEnvironment, functionName, contanThread, variables);
                }
            }
        }
        
        if (superClass != null) {
            return superClass.invokeFunction(contanThread, classInstanceEnvironment, functionName, ignoreNotFound, variables);
        }
    
        if (!ignoreNotFound){
            ContanRuntimeError.E0011.throwError("", null, functionName);
        }
        return ContanVoidObject.INSTANCE;
    }

    public boolean hasFunction(String functionName, int variableLength) {
        List<FunctionBlock> functions = functionMap.get(functionName);

        if (functions == null) {
            if (superClass == null) {
                return false;
            }
        } else {
            for (FunctionBlock functionBlock : functions) {
                if (functionBlock.getArgs().length == variableLength) {
                    return true;
                }
            }
        }

        if (superClass != null) {
            return superClass.hasFunction(functionName, variableLength);
        }

        return false;
    }

}
