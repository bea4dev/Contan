package org.contan_lang.syntax.parser;

import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.evaluators.FunctionBlock;
import org.contan_lang.evaluators.FunctionInvokable;
import org.contan_lang.variables.ContanVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptTree implements FunctionInvokable {
    
    private final String rootName;
    
    private final Map<String, List<FunctionBlock>> functionMap;
    
    private final List<FunctionBlock> functionBlocks;

    private final Evaluator globalEvaluator;
    
    public ScriptTree(String rootName, List<FunctionBlock> functionBlocks, Evaluator globalEvaluator) {
        this.rootName = rootName;
        this.functionBlocks = functionBlocks;
        this.globalEvaluator = globalEvaluator;
        this.functionMap = new HashMap<>();
        
        for (FunctionBlock functionBlock : functionBlocks) {
            List<FunctionBlock> functions = functionMap.computeIfAbsent(functionBlock.getFunctionName().getText(), k -> new ArrayList<>());
            functions.add(functionBlock);
        }
    }

    public String getRootName() {return rootName;}

    public List<FunctionBlock> getFunctionBlocks() {return functionBlocks;}

    public Evaluator getGlobalEvaluator() {return globalEvaluator;}

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
