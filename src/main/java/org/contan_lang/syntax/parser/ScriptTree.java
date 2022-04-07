package org.contan_lang.syntax.parser;

import org.contan_lang.evaluators.FunctionBlock;
import org.contan_lang.variables.ContanVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptTree {
    
    private final Parser parser;
    
    private final Map<String, List<FunctionBlock>> functionMap;
    
    private final List<FunctionBlock> functionBlocks;
    
    public ScriptTree(Parser parser, List<FunctionBlock> functionBlocks) {
        this.parser = parser;
        this.functionBlocks = functionBlocks;
        this.functionMap = new HashMap<>();
        
        for (FunctionBlock functionBlock : functionBlocks) {
            List<FunctionBlock> functions = functionMap.computeIfAbsent(functionBlock.getFunctionName().getText(), k -> new ArrayList<>());
            functions.add(functionBlock);
        }
    }
    
    public Parser getParser() {return parser;}
    
    public List<FunctionBlock> getFunctionBlocks() {return functionBlocks;}
    
    public ContanVariable<?> invokeFunction(String functionName, ContanVariable<?>... variables) {
        List<FunctionBlock> functions = functionMap.get(functionName);
        for (FunctionBlock functionBlock : functions) {
            if (functionBlock.getArgs().length == variables.length) {
                return functionBlock.eval(variables);
            }
        }
        
        throw new IllegalStateException("NOT FOUND FUNCTION " + functionName);//TODO
    }
    
}
