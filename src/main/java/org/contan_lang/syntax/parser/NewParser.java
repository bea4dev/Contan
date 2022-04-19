package org.contan_lang.syntax.parser;

import org.contan_lang.ContanEngine;
import org.contan_lang.evaluators.*;
import org.contan_lang.syntax.Identifier;
import org.contan_lang.syntax.Lexer;
import org.contan_lang.syntax.exception.ContanParseException;
import org.contan_lang.syntax.exception.ParseError;
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



    public Evaluator parseBlock(Scope scope, @Nullable List<Token> firstTokens, List<Token> blockTokens) throws ContanParseException {
        int length = blockTokens.size();

        if (firstTokens != null) {
            Token first = firstTokens.get(0);
            Identifier identifier = first.getIdentifier();

            if (identifier == null) {
                ParseError.E0000.throwError("", first);
            } else {
                switch (identifier) {
                    case CLASS: {
                        Token classNameToken = firstTokens.get(1);

                        if (scope.getScopeType() != ScopeType.MODULE) {
                            ParseError.E0004.throwError("", classNameToken);
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
                        Scope initializeScope = new Scope(moduleName + ".initialize", scope, ScopeType.INITIALIZE);

                        Evaluator blockEval = parseBlock(initializeScope, null, blockTokens);
                        classInitializers.add(blockEval);

                        return null;
                    }

                    case FUNCTION: {
                        Token functionNameToken = firstTokens.get(1);

                        if (scope.getScopeType() != ScopeType.MODULE && scope.getScopeType() != ScopeType.CLASS) {
                            ParseError.E0005.throwError("", functionNameToken);
                        }

                        List<Token> args = ParserUtil.getDefinedArguments(firstTokens.subList(2, firstTokens.size()));

                        Scope functionScope = new Scope(moduleName + "." + functionNameToken.getText(), scope, ScopeType.FUNCTION);
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

                    }
                }
            }
        }

        List<Evaluator> blockEvaluators = new ArrayList<>();
        List<Token> expressionTokens = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            Token token = blockTokens.get(i);
            Identifier identifier = token.getIdentifier();



            //Parse expression
            if (identifier != Identifier.EXPRESSION_SPLIT) {
                expressionTokens.add(token);
            }

            if (identifier == Identifier.EXPRESSION_SPLIT || i == length - 1) {
                blockEvaluators.add(parseExpression(scope, expressionTokens));
                expressionTokens.clear();
            }
        }
    }


    public Evaluator parseExpression(Scope scope, List<Token> tokens) throws ContanParseException {

    }

}
