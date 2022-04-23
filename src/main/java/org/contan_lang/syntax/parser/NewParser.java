package org.contan_lang.syntax.parser;

import org.contan_lang.ContanEngine;
import org.contan_lang.evaluators.*;
import org.contan_lang.syntax.Identifier;
import org.contan_lang.syntax.Lexer;
import org.contan_lang.syntax.exception.ContanParseException;
import org.contan_lang.syntax.exception.ParserError;
import org.contan_lang.syntax.parser.environment.Scope;
import org.contan_lang.syntax.parser.environment.ScopeType;
import org.contan_lang.syntax.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NewParser {

    private final String moduleName;

    private final ContanEngine contanEngine;

    private final Lexer lexer;

    public NewParser(String moduleName, @NotNull ContanEngine contanEngine, @NotNull String text) {
        this.moduleName = moduleName;
        this.contanEngine = contanEngine;
        this.lexer = new Lexer(text);
    }



    private List<PreLinkedFunctionEvaluator> preLinkedFunctions;

    private List<FunctionBlock> moduleFunctions;

    private List<FunctionBlock> classFunctionBlocks;

    private List<Evaluator> classInitializers = new ArrayList<>();

    private List<PreLinkedCreateClassInstanceEvaluator> preLinkedCreateClassInstanceEvaluators;

    private Scope moduleScope;

    public synchronized ContanModule compile() throws ContanParseException {
        List<Token> tokens = lexer.split();

        moduleFunctions = new ArrayList<>();
        preLinkedFunctions = new ArrayList<>();
        classFunctionBlocks = new ArrayList<>();
        classInitializers = new ArrayList<>();
        moduleScope = new Scope(moduleName, null, ScopeType.MODULE);

        preLinkedCreateClassInstanceEvaluators = new ArrayList<>();

        Evaluator globalEvaluator = parseBlock(moduleScope, null, tokens);

        for (PreLinkedFunctionEvaluator functionEvaluator : preLinkedFunctions) {
            functionEvaluator.link(moduleFunctions);
        }

        for (PreLinkedCreateClassInstanceEvaluator classInstanceEvaluator : preLinkedCreateClassInstanceEvaluators) {
            contanEngine.linkClass(classInstanceEvaluator);
        }

        return new ContanModule(moduleName, moduleFunctions, globalEvaluator);
    }
    
    
    /**
     * The given token is traversed one by one from the front to generate an evaluator
     * by dividing it into sentences and block parts.
     *
     * @param scope Scope of blocks to be scanned
     * @param firstTokens Token list for the pre-evaluation portion of the block.
     *                    <p>
     *                    For example, in the case of an if block,
     *                    it refers to the "if (a == b)" part of "if (a == b) {//do something}".
     *
     * @param blockTokens Token list for the execution part of the block.
     *                    <p>
     *                    For example, in the case of an if block,
     *                    it refers to the "{//do something}" part of "if (a == b) {//do something}".
     *
     * @return Parsed {@link Evaluator}
     * @throws ContanParseException
     */
    public Evaluator parseBlock(Scope scope, @Nullable List<Token> firstTokens, List<Token> blockTokens)
            throws ContanParseException {

        if (firstTokens != null) {
            //Parses a sequence of tokens already separated by the first and second halves.
            
            Token first = firstTokens.get(0);
            Identifier identifier = first.getIdentifier();

            if (identifier == null) {
                ParserError.E0000.throwError("", first);
            } else {
                
                //Create each block from the split tokens.
                switch (identifier) {
                    case CLASS: {
                        Token classNameToken = firstTokens.get(1);

                        if (scope.getScopeType() != ScopeType.MODULE) {
                            ParserError.E0004.throwError("", first);
                        }

                        List<Token> args = ParserUtil.getDefinedArguments(firstTokens.subList(2, firstTokens.size()));

                        Scope classScope = new Scope(moduleName + "." + classNameToken.getText(), scope, ScopeType.CLASS);
                        args.forEach(token -> classScope.addVariable(token.getText()));

                        Evaluator blockEval = parseBlock(classScope, null, blockTokens);

                        ClassBlock classBlock = new ClassBlock(contanEngine, classNameToken, moduleName + "." + classNameToken.getText(), args.toArray(new Token[0]));

                        classFunctionBlocks.forEach(classBlock::addFunctionBlock);
                        classInitializers.forEach(classBlock::addInitializer);
                        classBlock.addInitializer(blockEval);
                        contanEngine.addClassBlock(classBlock);
                        classFunctionBlocks.clear();
                        classInitializers.clear();

                        return null;
                    }

                    case INITIALIZE: {
                        if (scope.getScopeType() != ScopeType.CLASS && scope.getScopeType() == ScopeType.MODULE) {
                            ParserError.E0006.throwError("", first);
                        }
                        
                        Scope initializeScope = new Scope(moduleName + ".initialize", scope, ScopeType.INITIALIZE);

                        Evaluator blockEval = parseBlock(initializeScope, null, blockTokens);
                        classInitializers.add(blockEval);

                        return null;
                    }

                    case FUNCTION: {
                        Token functionNameToken = firstTokens.get(1);

                        if (scope.getScopeType() != ScopeType.MODULE && scope.getScopeType() != ScopeType.CLASS) {
                            ParserError.E0005.throwError("", first);
                        }

                        List<Token> args = ParserUtil.getDefinedArguments(firstTokens.subList(2, firstTokens.size()));

                        Scope functionScope = new Scope(scope.getRootName() + "." + functionNameToken.getText(), scope, ScopeType.FUNCTION);
                        args.forEach(token -> functionScope.addVariable(token.getText()));

                        Evaluator blockEval = parseBlock(functionScope, null, blockTokens);

                        FunctionBlock functionBlock = new FunctionBlock(functionNameToken, blockEval, args.toArray(new Token[0]));

                        if (scope.getScopeType() == ScopeType.MODULE) {
                            moduleFunctions.add(functionBlock);
                        } else if (scope.getScopeType() == ScopeType.CLASS) {
                            classFunctionBlocks.add(functionBlock);
                        }

                        return null;
                    }

                    case IF: {
                        Scope ifScope = new Scope(scope.getRootName() + ".if", scope, ScopeType.FUNCTION);
                        
                        Evaluator termsEval = parseBlock(ifScope, null, firstTokens.subList(1, firstTokens.size()));
                        Evaluator blockEval = parseBlock(ifScope, null, blockTokens);
                        
                        IfEvaluator ifEvaluator = new IfEvaluator(termsEval, blockEval);
                        scope.setPreviousIfEvaluator(ifEvaluator);
                        
                        return ifEvaluator;
                    }
                    
                    default: {
                        ParserError.E0000.throwError("", first);
                    }
                }
            }
        }



        int blockTokenLength = blockTokens.size();

        if (blockTokenLength == 0) {
            return NullEvaluator.INSTANCE;
        }


        if (blockTokens.get(0).getIdentifier() == Identifier.BLOCK_START) {
            List<Token> block = ParserUtil.getNestedToken(blockTokens, 0, Identifier.BLOCK_START, Identifier.BLOCK_END, false, false);

            if (block.size() + 2 == blockTokenLength) {
                return parseBlock(scope, null, blockTokens);
            }
        }

        if (blockTokens.get(0).getIdentifier() == Identifier.BLOCK_OPERATOR_START) {
            List<Token> block = ParserUtil.getNestedToken(blockTokens, 0, Identifier.BLOCK_OPERATOR_START, Identifier.BLOCK_OPERATOR_END, false, false);

            if (block.size() + 2 == blockTokenLength) {
                return parseBlock(scope, null, blockTokens);
            }
        }

        
        List<Evaluator> blockEvaluators = new ArrayList<>();
        List<Token> expressionTokens = new ArrayList<>();

        //Parses a block or statement that has not yet been split.
        for (int i = 0; i < blockTokenLength; i++) {
            Token token = blockTokens.get(i);
            Identifier identifier = token.getIdentifier();

            if (identifier != null) {
                switch (identifier) {
                    case CLASS:

                    case INITIALIZE:

                    case FUNCTION:

                    case IF: {
                        List<Token> first = ParserUtil.getTokensUntilFoundIdentifier(blockTokens, i, Identifier.BLOCK_START);
                        i += first.size();

                        List<Token> block = ParserUtil.getNestedToken(blockTokens, i, Identifier.BLOCK_START, Identifier.BLOCK_END, false, false);
                        i += block.size();

                        parseBlock(scope, first, block);
                        continue;
                    }
                }
            }

            
            //Parse expression
            if (identifier != Identifier.EXPRESSION_SPLIT) {
                expressionTokens.add(token);
            }

            if (identifier == Identifier.EXPRESSION_SPLIT || i == blockTokenLength - 1) {
                blockEvaluators.add(parseExpression(scope, expressionTokens));
                expressionTokens.clear();
            }
        }

        return new Expressions(blockEvaluators);
    }


    public Evaluator parseExpression(Scope scope, List<Token> tokens) throws ContanParseException {
        int tokenLength = tokens.size();

        if (tokenLength == 0) {
            return NullEvaluator.INSTANCE;
        }

        if (tokenLength == 1) {
            Token first = tokens.get(0);


        }
    }

}
