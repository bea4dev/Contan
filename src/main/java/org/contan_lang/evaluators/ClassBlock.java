package org.contan_lang.evaluators;

import org.contan_lang.environment.Environment;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanVariable;
import org.contan_lang.variables.primitive.ContanClassInstance;

import java.util.*;

public class ClassBlock implements FunctionInvokable {

    private final Token className;

    private final String classPath;

    private final List<Evaluator> initializers;

    private final Map<String, List<FunctionBlock>> functionMap;

    private final Token[] initializeArgs;

    public ClassBlock(Token className, String classPath, Token... initializeArgs) {
        this.className = className;
        this.classPath = classPath;
        this.initializers = new ArrayList<>();
        this.initializeArgs = initializeArgs;
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

    public ContanClassInstance createInstance(Environment parentEnvironment, ContanVariable<?>... contanVariables) {
        Environment environment = new Environment(parentEnvironment);

        for (int i = 0; i < contanVariables.length; i++) {
            environment.createVariable(initializeArgs[i].getText(), contanVariables[i]);
        }

        for (Evaluator evaluator : initializers) {
            evaluator.eval(environment);
        }

        return new ContanClassInstance(this, environment);
    }

    @Override
    public ContanVariable<?> invokeFunction(Environment environment, String functionName, ContanVariable<?>... variables) {
        List<FunctionBlock> functions = functionMap.get(functionName);
        for (FunctionBlock functionBlock : functions) {
            if (functionBlock.getArgs().length == variables.length) {
                return functionBlock.eval(new Environment(environment), variables);
            }
        }

        throw new IllegalStateException("NOT FOUND FUNCTION " + functionName);//TODO
    }

}
