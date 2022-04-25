package org.contan_lang.syntax.parser;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.evaluators.FunctionBlock;
import org.contan_lang.evaluators.FunctionInvokable;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContanModule implements FunctionInvokable {
    
    private final ContanEngine contanEngine;
    
    private final String rootName;
    
    private final Map<String, List<FunctionBlock>> functionMap;
    
    private final List<FunctionBlock> functionBlocks;

    private final Evaluator globalEvaluator;

    private final Environment moduleEnvironment;
    
    public ContanModule(ContanEngine contanEngine, String rootName, List<FunctionBlock> functionBlocks, Evaluator globalEvaluator, Environment moduleEnvironment) {
        this.contanEngine = contanEngine;
        this.rootName = rootName;
        this.functionBlocks = functionBlocks;
        this.globalEvaluator = globalEvaluator;
        this.functionMap = new HashMap<>();
        this.moduleEnvironment = moduleEnvironment;
        
        for (FunctionBlock functionBlock : functionBlocks) {
            List<FunctionBlock> functions = functionMap.computeIfAbsent(functionBlock.getFunctionName().getText(), k -> new ArrayList<>());
            functions.add(functionBlock);
        }
    }

    public String getRootName() {return rootName;}

    public List<FunctionBlock> getFunctionBlocks() {return functionBlocks;}

    public Evaluator getGlobalEvaluator() {return globalEvaluator;}

    public Environment getModuleEnvironment() {return moduleEnvironment;}

    @Override
    public ContanObject<?> invokeFunction(Token functionName, ContanObject<?>... variables) {
        List<FunctionBlock> functions = functionMap.get(functionName.getText());
        for (FunctionBlock functionBlock : functions) {
            if (functionBlock.getArgs().length == variables.length) {
                return functionBlock.eval(new Environment(contanEngine, moduleEnvironment), null, variables);
            }
        }
        
        throw new IllegalStateException("NOT FOUND FUNCTION " + functionName);//TODO
    }
    
}
