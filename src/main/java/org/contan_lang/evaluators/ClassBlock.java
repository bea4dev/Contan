package org.contan_lang.evaluators;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanClassInstance;
import org.contan_lang.variables.primitive.ContanVoid;

import java.util.*;

public class ClassBlock {

    private final ContanEngine contanEngine;

    private final Token className;

    private final String classPath;

    private final List<Evaluator> initializers;

    private final Map<String, List<FunctionBlock>> functionMap;

    private final Token[] initializeArgs;

    private final Environment moduleEnvironment;

    public ClassBlock(ContanEngine contanEngine, Token className, String classPath, Environment moduleEnvironment, Token... initializeArgs) {
        this.contanEngine = contanEngine;
        this.className = className;
        this.classPath = classPath;
        this.initializers = new ArrayList<>();
        this.initializeArgs = initializeArgs;
        this.moduleEnvironment = moduleEnvironment;
        this.functionMap = new HashMap<>();
    }

    public Token getClassName() {return className;}

    public String getClassPath() {return classPath;}

    public Token[] getInitializeArgs() {return initializeArgs;}

    public void addInitializer(Evaluator initializers) {this.initializers.add(initializers);}

    public void addFunctionBlock(FunctionBlock functionBlock) {
        List<FunctionBlock> functions = functionMap.computeIfAbsent(functionBlock.getFunctionName().getText(), k -> new ArrayList<>());
        functions.add(functionBlock);
    }

    public ContanClassInstance createInstance(ContanObject<?>... contanObjects) {
        Environment environment = new Environment(contanEngine, moduleEnvironment);

        for (int i = 0; i < initializeArgs.length; i++) {
            if (i < contanObjects.length) {
                environment.createVariable(initializeArgs[i].getText(), contanObjects[i]);
            } else {
                environment.createVariable(initializeArgs[i].getText(), ContanVoid.INSTANCE);
            }
        }

        for (Evaluator evaluator : initializers) {
            evaluator.eval(environment);
        }

        return new ContanClassInstance(contanEngine,this, environment);
    }


    public ContanObject<?> invokeFunction(Environment classInstanceEnvironment, Token functionName, ContanObject<?>... variables) {
        List<FunctionBlock> functions = functionMap.get(functionName.getText());
        if (functions == null) {
            ContanRuntimeError.E0011.throwError("", null, functionName);
            return null;
        }

        for (FunctionBlock functionBlock : functions) {
            if (functionBlock.getArgs().length == variables.length) {
                return functionBlock.eval(classInstanceEnvironment, functionName, variables);
            }
        }
    
        ContanRuntimeError.E0011.throwError("", null, functionName);
        return null;
    }

}
