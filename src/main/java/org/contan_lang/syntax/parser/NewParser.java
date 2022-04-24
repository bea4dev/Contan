package org.contan_lang.syntax.parser;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.*;
import org.contan_lang.operators.primitives.*;
import org.contan_lang.syntax.Identifier;
import org.contan_lang.syntax.Lexer;
import org.contan_lang.syntax.exception.ContanParseException;
import org.contan_lang.syntax.exception.ParserError;
import org.contan_lang.syntax.parser.environment.Scope;
import org.contan_lang.syntax.parser.environment.ScopeType;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.primitive.ContanFloat;
import org.contan_lang.variables.primitive.ContanInteger;
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



    private List<PreLinkedFunctionOperator> preLinkedFunctions;

    private List<FunctionBlock> moduleFunctions;

    private List<FunctionBlock> classFunctionBlocks;

    private List<Evaluator> classInitializers = new ArrayList<>();

    private List<PreLinkedCreateClassInstanceOperator> preLinkedCreateClassInstanceOperators;

    private Scope moduleScope;
    
    private Environment moduleEnvironment;

    public synchronized ContanModule compile() throws ContanParseException {
        List<Token> tokens = lexer.split();

        moduleFunctions = new ArrayList<>();
        preLinkedFunctions = new ArrayList<>();
        classFunctionBlocks = new ArrayList<>();
        classInitializers = new ArrayList<>();
        moduleScope = new Scope(moduleName, null, ScopeType.MODULE);
        moduleEnvironment = new Environment(contanEngine, null);

        preLinkedCreateClassInstanceOperators = new ArrayList<>();

        Evaluator globalEvaluator = parseBlock(moduleScope, null, tokens);

        for (PreLinkedFunctionOperator functionEvaluator : preLinkedFunctions) {
            functionEvaluator.link(moduleFunctions, moduleEnvironment);
        }

        for (PreLinkedCreateClassInstanceOperator classInstanceEvaluator : preLinkedCreateClassInstanceOperators) {
            contanEngine.linkClass(classInstanceEvaluator);
        }

        return new ContanModule(contanEngine, moduleName, moduleFunctions, globalEvaluator, moduleEnvironment);
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

                        FunctionBlock functionBlock = new FunctionBlock(contanEngine, functionNameToken, blockEval, args.toArray(new Token[0]));

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
                        i += block.size() + 2;

                        parseBlock(scope, first, block);
                        continue;
                    }
                }
            }
    
    
            //Skip over the contents of the parentheses.
            if (identifier == Identifier.BLOCK_OPERATOR_START) {
                List<Token> nestedTokens = ParserUtil.getNestedToken(blockTokens, i, Identifier.BLOCK_OPERATOR_START, Identifier.BLOCK_OPERATOR_END, false, false);
                i += nestedTokens.size() + 2;
    
                //Ignore line breaks.
                nestedTokens.removeIf(t -> t.getText().equals("\n"));
                
                expressionTokens.addAll(nestedTokens);
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

        return new Expressions(blockEvaluators.toArray(new Evaluator[0]));
    }


    public Evaluator parseExpression(Scope scope, List<Token> tokens) throws ContanParseException {
        int tokenLength = tokens.size();

        if (tokenLength == 0) {
            return NullEvaluator.INSTANCE;
        }

        
        //Determine if it is a variable name or an integer definition.
        if (tokenLength == 1) {
            Token first = tokens.get(0);

            //Checks if a variable name is specified.
            if (first.getIdentifier() != null) {
                ParserError.E0006.throwError("", first);
            }
    
            
            String name = first.getText();
            if (ParserUtil.isNumber(name)) {
                //Integer
                return new DefinedValueOperator(contanEngine, first, new ContanInteger(contanEngine, Long.parseLong(name)));
            } else {
                //Not number
                scope.checkHasVariable(first);
                return new GetValueOperator(contanEngine, first);
            }
        }
        
        
        //Obtains a numeric value divided into integer and decimal portions by lexer.
        if (tokenLength == 3) {
            Token first = tokens.get(0);
            Token second = tokens.get(1);
            Token third = tokens.get(2);
            
            //Determine if the first and third are numeric.
            if (ParserUtil.isNumber(first.getText()) && ParserUtil.isNumber(third.getText())) {
                //Determine if the second is dot.
                if (second.getIdentifier() == Identifier.DOT) {
                    double value = Double.parseDouble(first.getText() + second.getText() + third.getText());
                    return new DefinedValueOperator(contanEngine, first.marge(second, third), new ContanFloat(contanEngine, value));
                }
            }
        }
        
        
        //From right to left, it searches for the highest-priority Identifier and divides the surrounding sentence.
        //When scanning, skip over the contents of the parentheses.
        Token highestIdentifierToken = null;
        Identifier highestIdentifier = null;
        String word = "";
        int currentHighestPriority = 0;
        int highestIdentifierTokenIndex = 0;
        
        for (int i = tokenLength - 1; i >= 0; i--) {
            Token token = tokens.get(i);
            
            Identifier identifier = token.getIdentifier();
            
            //Skip if the current token is not an Identifier.
            if (identifier == null) {
                continue;
            }
            
            if (identifier.priority >= Identifier.BLOCK_START.priority) {
                ParserError.E0000.throwError("", token);
            }
    
            //Skip over the contents of the parentheses.
            if (identifier == Identifier.BLOCK_OPERATOR_END) {
                i -= ParserUtil.getNestedTokenReverse(tokens, i, Identifier.BLOCK_OPERATOR_END, Identifier.BLOCK_OPERATOR_START, true, false).size();
            }
            
            if (identifier.priority > currentHighestPriority) {
                highestIdentifierToken = token;
                highestIdentifier = identifier;
                word = token.getText();
                currentHighestPriority = identifier.priority;
                highestIdentifierTokenIndex = i;
            }
        }
        
        if (highestIdentifier == null) {
            ParserError.E0008.throwError("", tokens.get(0));
            return null;
        }
        
        //Split sentences.
        List<Token> leftTokenList = tokens.subList(0, highestIdentifierTokenIndex);
        List<Token> rightTokenList = tokens.subList(highestIdentifierTokenIndex + 1, tokenLength);
        
        switch (highestIdentifier) {
            //data value = 0
            case DEFINE_VARIABLE: {
                if (leftTokenList.size() != 0) {
                    ParserError.E0009.throwError("", leftTokenList.toArray(new Token[0]));
                }
                
                Token variableNameToken = rightTokenList.get(0);
                if (variableNameToken.getIdentifier() != null) {
                    ParserError.E0010.throwError("", variableNameToken);
                }
                
                int rightTokenListLength = rightTokenList.size();
                if (rightTokenListLength != 1) {
                    //If the expression is incomplete.
                    if (rightTokenListLength < 3) {
                        ParserError.E0011.throwError("", rightTokenList.toArray(new Token[0]));
                    }
                    
                    Evaluator define = new CreateVariableOperator(contanEngine, rightTokenList.get(0));
                    Evaluator set = parseExpression(scope, rightTokenList.subList(1, rightTokenListLength));
                    
                    return new Expressions(define, set);
                }
            }
            
            //20 + 10 or +10
            case OPERATOR_PLUS: {
                if (leftTokenList.size() == 0) {
                    return parseExpression(scope, rightTokenList);
                }
                
                Evaluator left = parseExpression(scope, leftTokenList);
                Evaluator right = parseExpression(scope, rightTokenList);
                
                return new AddOperator(contanEngine, highestIdentifierToken, left, right);
            }
            
            //20 * 10
            case OPERATOR_MULTIPLY: {
                if (leftTokenList.size() == 0 || rightTokenList.size() == 0) {
                    ParserError.E0012.throwError("", highestIdentifierToken);
                }
    
                Evaluator left = parseExpression(scope, leftTokenList);
                Evaluator right = parseExpression(scope, rightTokenList);
                
                return new MultiplyOperator(contanEngine, highestIdentifierToken, left, right);
            }
            
            //20 == 20
            case OPERATOR_EQUAL: {
                if (leftTokenList.size() == 0 || rightTokenList.size() == 0) {
                    ParserError.E0012.throwError("", highestIdentifierToken);
                }
    
                Evaluator left = parseExpression(scope, leftTokenList);
                Evaluator right = parseExpression(scope, rightTokenList);
                
                return new EqualOperator(contanEngine, highestIdentifierToken, left, right);
            }
            
            //value = 20
            case SUBSTITUTION: {
                if (leftTokenList.size() == 0 || rightTokenList.size() == 0) {
                    ParserError.E0012.throwError("", highestIdentifierToken);
                }
    
                Evaluator left = parseExpression(scope, leftTokenList);
                Evaluator right = parseExpression(scope, rightTokenList);
                
                return new SetValueOperator(contanEngine, highestIdentifierToken, left, right);
            }
            
            //null
            case NULL: {
                return new NullValueOperator(contanEngine, highestIdentifierToken);
            }
            
            //new
            case NEW: {
            
            }
        }
    }

}
