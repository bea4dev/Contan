package org.contan_lang;

import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanJavaRuntimeException;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.ClassBlock;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.evaluators.FunctionBlock;
import org.contan_lang.evaluators.FunctionInvokable;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanNull;
import org.contan_lang.variables.primitive.JavaClassInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContanModule implements FunctionInvokable {
    
    private final ContanEngine contanEngine;
    
    private final String rootName;
    
    private final Map<String, List<FunctionBlock>> functionMap;
    
    private final List<FunctionBlock> functionBlocks;

    private final Map<String, ClassBlock> classBlockMap;

    private final List<ClassBlock> classBlocks;

    private final Evaluator globalEvaluator;

    private final Environment moduleEnvironment;
    
    public ContanModule(ContanEngine contanEngine, String rootName, List<FunctionBlock> functionBlocks, List<ClassBlock> classBlocks, Evaluator globalEvaluator, Environment moduleEnvironment) {
        this.contanEngine = contanEngine;
        this.rootName = rootName;
        this.functionBlocks = functionBlocks;
        this.classBlocks = classBlocks;
        this.globalEvaluator = globalEvaluator;
        this.functionMap = new HashMap<>();
        this.classBlockMap = new HashMap<>();
        this.moduleEnvironment = moduleEnvironment;
        
        for (FunctionBlock functionBlock : functionBlocks) {
            List<FunctionBlock> functions = functionMap.computeIfAbsent(functionBlock.getFunctionName().getText(), k -> new ArrayList<>());
            functions.add(functionBlock);
        }

        for (ClassBlock classBlock : classBlocks) {
            classBlockMap.put(classBlock.getClassName().getText(), classBlock);
        }
    }

    public String getRootName() {return rootName;}

    public List<FunctionBlock> getFunctionBlocks() {return functionBlocks;}

    public Evaluator getGlobalEvaluator() {return globalEvaluator;}

    public Environment getModuleEnvironment() {return moduleEnvironment;}
    
    public ContanObject<?> eval() {
        return globalEvaluator.eval(moduleEnvironment);
    }

    @Override
    public ContanObject<?> invokeFunction(Token functionName, ContanObject<?>... variables) {
        List<FunctionBlock> functions = functionMap.get(functionName.getText());
        if (functions != null) {
            for (FunctionBlock functionBlock : functions) {
                if (functionBlock.getArgs().length == variables.length) {
                    return functionBlock.eval(new Environment(contanEngine, moduleEnvironment), null, variables);
                }
            }
        }
    
        ContanRuntimeError.E0011.throwError("", null, functionName);
        return ContanNull.INSTANCE;
    }
    
    
    public Object invokeFunction(String functionName, Object... arguments) {
        //Convert all arguments to ContanObject.
        ContanObject<?>[] variables = new ContanObject<?>[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            variables[i] = new JavaClassInstance(contanEngine, arguments[i]);
        }
        
        //Invoke function
        List<FunctionBlock> functions = functionMap.get(functionName);
        if (functions != null) {
            for (FunctionBlock functionBlock : functions) {
                if (functionBlock.getArgs().length == variables.length) {
                    ContanObject<?> result = functionBlock.eval(new Environment(contanEngine, moduleEnvironment), null, variables);
                    if (result instanceof ContanNull) {
                        return null;
                    } else {
                        return result.getBasedJavaObject();
                    }
                }
            }
        }
        
        throw new ContanJavaRuntimeException("Function not found : " + functionName, null);
    }
    
}
