package org.contan_lang.syntax.parser;

import org.contan_lang.ContanEngine;
import org.contan_lang.ContanModule;
import org.contan_lang.environment.CancelStatus;
import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.*;
import org.contan_lang.operators.primitives.*;
import org.contan_lang.syntax.Identifier;
import org.contan_lang.syntax.Lexer;
import org.contan_lang.syntax.exception.ContanParseException;
import org.contan_lang.syntax.exception.ParserError;
import org.contan_lang.syntax.parser.environment.Scope;
import org.contan_lang.syntax.parser.environment.ScopeType;
import org.contan_lang.syntax.tokens.BlockToken;
import org.contan_lang.syntax.tokens.StringToken;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.primitive.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final String moduleName;

    private final ContanEngine contanEngine;

    private final Lexer lexer;

    public Parser(String moduleName, @NotNull ContanEngine contanEngine, @NotNull String text) {
        this.moduleName = moduleName;
        this.contanEngine = contanEngine;
        this.lexer = new Lexer(moduleName, text);
    }



    private List<PreLinkedFunctionOperator> preLinkedFunctions;

    private List<FunctionBlock> moduleFunctions;

    private List<FunctionBlock> classFunctionBlocks;

    private List<Evaluator> classInitializers = new ArrayList<>();

    private List<ClassBlock> moduleClasses = new ArrayList<>();

    private Scope moduleScope;
    
    private Environment moduleEnvironment;

    public synchronized ContanModule compile() throws ContanParseException {
        List<Token> tokens = lexer.split();

        moduleFunctions = new ArrayList<>();
        preLinkedFunctions = new ArrayList<>();
        classFunctionBlocks = new ArrayList<>();
        classInitializers = new ArrayList<>();
        moduleScope = new Scope(moduleName, null, ScopeType.MODULE);
        moduleEnvironment = new Environment(contanEngine, null, contanEngine.getMainThread(), null, true);
        moduleClasses = new ArrayList<>();

        //Register the class name first.
        boolean isClassDefine = false;
        for (Token token : tokens) {
            if (isClassDefine) {
                moduleScope.addVariable(token.getText());
            }

            isClassDefine = token.getIdentifier() == Identifier.CLASS;
        }

        Evaluator globalEvaluator = parseBlock(moduleScope, null, tokens);

        for (PreLinkedFunctionOperator functionEvaluator : preLinkedFunctions) {
            functionEvaluator.link(moduleFunctions, moduleEnvironment);
        }

        moduleEnvironment.setReEval(globalEvaluator);

        return new ContanModule(contanEngine, moduleName, moduleFunctions, moduleClasses, globalEvaluator, moduleEnvironment);
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
                        
                        
                        List<Token> argumentTokens = ParserUtil.getNestedToken(firstTokens, 2, Identifier.BLOCK_OPERATOR_START, Identifier.BLOCK_OPERATOR_END, true, false);
                        int index = 2 + argumentTokens.size();
                        
                        Evaluator superClassEval = null;
                        if (firstTokens.size() > index + 1) {
                            Token extendsToken = firstTokens.get(index);
                            List<Token> superClassTokens = firstTokens.subList(index + 1, firstTokens.size());
                            
                            if (extendsToken.getIdentifier() != Identifier.EXTENDS) {
                                ParserError.E0032.throwError("", extendsToken);
                                return null;
                            }
                            
                            superClassEval = parseExpression(scope, superClassTokens);
                        }

                        List<Token> args = ParserUtil.getDefinedArguments(argumentTokens);

                        Scope classScope = new Scope(moduleName + "." + classNameToken.getText(), scope, ScopeType.CLASS);
                        args.forEach(token -> classScope.addVariable(token.getText()));

                        Evaluator blockEval = parseBlock(classScope, null, blockTokens);

                        ClassBlock classBlock = new ClassBlock(classNameToken, moduleName + "." + classNameToken.getText(), moduleEnvironment, superClassEval, args.toArray(new Token[0]));

                        classFunctionBlocks.forEach(classBlock::addFunctionBlock);
                        classBlock.addInitializer(blockEval);
                        classInitializers.forEach(classBlock::addInitializer);
                        contanEngine.addClassBlock(classBlock);
                        classFunctionBlocks.clear();
                        classInitializers.clear();
                        moduleClasses.add(classBlock);
                        moduleEnvironment.createVariable(classNameToken.getText(), new ContanClassObject(contanEngine, classBlock));
                        moduleScope.addVariable(classNameToken.getText());

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
                        
                        IfEvaluator ifEvaluator = new IfEvaluator(contanEngine, first, termsEval, blockEval);
                        scope.setPreviousIfEvaluator(ifEvaluator);
                        
                        return ifEvaluator;
                    }

                    case REPEAT: {
                        Scope repeatScope = new Scope(scope.getRootName() + ".repeat", scope, ScopeType.FUNCTION);

                        String name = "";
                        Evaluator termsEval = null;

                        Token lastToken = firstTokens.get(firstTokens.size() - 1);
                        if (lastToken.isLabelToken()) {
                            name = lastToken.getText();
                            repeatScope.addVariable(name);

                            if (firstTokens.size() >= 3) {
                                termsEval = parseExpression(scope, firstTokens.subList(1, firstTokens.size() - 1));
                            }
                        } else {
                            if (firstTokens.size() >= 2) {
                                termsEval = parseExpression(scope, firstTokens.subList(1, firstTokens.size()));
                            }
                        }

                        Evaluator blockEval = parseBlock(repeatScope, null, blockTokens);

                        return new RepeatEvaluator(contanEngine, first, termsEval, blockEval, name);
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

                    case INITIALIZE: {
                        List<Token> first = ParserUtil.getTokensUntilFoundIdentifier(blockTokens, i, Identifier.BLOCK_START);
                        i += first.size();
    
                        List<Token> block = ParserUtil.getNestedToken(blockTokens, i, Identifier.BLOCK_START, Identifier.BLOCK_END, false, false);
                        i += block.size() + 1;
    
                        parseBlock(scope, first, block);
                        continue;
                    }

                    case FUNCTION: {
                        List<Token> first = ParserUtil.getTokensUntilFoundIdentifier(blockTokens, i, Identifier.BLOCK_START);
                        
                        //Ignore case of "data func = function() {//do something}"
                        if (first.get(1).getIdentifier() == Identifier.BLOCK_OPERATOR_START) {
                            break;
                        }
                        
                        i += first.size();
    
                        List<Token> block = ParserUtil.getNestedToken(blockTokens, i, Identifier.BLOCK_START, Identifier.BLOCK_END, false, false);
                        i += block.size() + 1;
    
                        parseBlock(scope, first, block);
                        continue;
                    }

                    case IF:

                    case REPEAT: {
                        List<Token> first = ParserUtil.getTokensUntilFoundIdentifier(blockTokens, i, Identifier.BLOCK_START);
                        i += first.size();
    
                        List<Token> block = ParserUtil.getNestedToken(blockTokens, i, Identifier.BLOCK_START, Identifier.BLOCK_END, false, false);
                        i += block.size() + 1;
    
                        blockEvaluators.add(parseBlock(scope, first, block));
                        continue;
                    }
                }
            }
    
    
            //Skip over the contents of the parentheses.
            if (identifier == Identifier.BLOCK_OPERATOR_START) {
                List<Token> nestedTokens = ParserUtil.getNestedToken(blockTokens, i, Identifier.BLOCK_OPERATOR_START, Identifier.BLOCK_OPERATOR_END, true, false);
                i += nestedTokens.size() - 1;
    
                //Ignore line breaks.
                nestedTokens = ParserUtil.removeLineBreaks(nestedTokens);
                
                expressionTokens.addAll(nestedTokens);
            }
            
            //Combine the sequence of tokens within a block into one.
            if (identifier == Identifier.BLOCK_START) {
                List<Token> nestedTokens = ParserUtil.getNestedToken(blockTokens, i, Identifier.BLOCK_START, Identifier.BLOCK_END, false, false);
                i += nestedTokens.size() + 1;
                
                expressionTokens.add(new BlockToken(lexer, nestedTokens, token));
            }
            if (identifier == Identifier.BLOCK_GET_START) {
                List<Token> nestedTokens = ParserUtil.getNestedToken(blockTokens, i, Identifier.BLOCK_GET_START, Identifier.BLOCK_GET_END, false, false);
                i += nestedTokens.size() + 1;
    
                expressionTokens.add(new BlockToken(lexer, nestedTokens, Identifier.DOT, token));
            }
            
            //Parse expression
            if (identifier != Identifier.EXPRESSION_SPLIT && identifier != Identifier.BLOCK_OPERATOR_START && identifier != Identifier.BLOCK_GET_START) {
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
        

        //If there is only a portion enclosed in parentheses, remove the parentheses.
        if (tokens.get(0).getIdentifier() == Identifier.BLOCK_OPERATOR_START && tokens.get(tokenLength - 1).getIdentifier() == Identifier.BLOCK_OPERATOR_END) {
            List<Token> nested = ParserUtil.getNestedTokenIfEnclosed(tokens, Identifier.BLOCK_OPERATOR_START, Identifier.BLOCK_OPERATOR_END);
            if (nested.size() == tokenLength - 2) {
                return parseExpression(scope, nested);
            }
        }
        
        //Determine if it is a variable name or an integer definition.
        if (tokenLength == 1) {
            Token first = tokens.get(0);

            //Checks if a variable name is specified.
            if (first.getIdentifier() == null) {
    
                String name = first.getText();
                if (ParserUtil.isNumber(name)) {
                    if (name.contains(".")) {
                        //Float
                        return new DefineValueOperator(contanEngine, first, new ContanF64(contanEngine, Double.parseDouble(name)));
                    } else {
                        //Integer
                        return new DefineValueOperator(contanEngine, first, new ContanI64(contanEngine, Long.parseLong(name)));
                    }
                } else if (first instanceof StringToken) {
                    //String
                    return new DefineValueOperator(contanEngine, first, new ContanString(contanEngine, first.getText()));
                } else {
                    //Not number
        
                    if (name.contains("#")) {
                        ParserError.E0026.throwError("", first);
                    }
        
                    scope.checkHasVariable(contanEngine, first);
                    return new GetVariableOperator(contanEngine, first);
                }
            }
        }

        
        //From left to right, it searches for the highest-priority Identifier and divides the surrounding sentence.
        //When scanning, skip over the contents of the parentheses.
        Token highestIdentifierToken = null;
        Identifier highestIdentifier = null;
        String word = "";
        int currentHighestPriority = 0;
        int highestIdentifierTokenIndex = 0;

        Identifier previousIdentifier = null;
        
        for (int i = 0; i < tokenLength; i++) {
            Token token = tokens.get(i);
            
            Identifier identifier = token.getIdentifier();
            
            //Skip if the current token is not an Identifier.
            if (identifier == null) {
                previousIdentifier = null;
                continue;
            }
            
            if (identifier.priority >= Identifier.IF.priority) {
                ParserError.E0020.throwError("", token);
            }
    
            //Skip over the contents of the parentheses.
            if (identifier == Identifier.BLOCK_OPERATOR_START) {
                i += ParserUtil.getNestedToken(tokens, i, Identifier.BLOCK_OPERATOR_START, Identifier.BLOCK_OPERATOR_END, true, false).size() - 1;
            }
            
            if (identifier.priority >= currentHighestPriority && identifier.priority != 0) {
                if (identifier == Identifier.OPERATOR_MINUS) {
                    if (previousIdentifier == Identifier.OPERATOR_PLUS || previousIdentifier == Identifier.OPERATOR_MINUS
                    || previousIdentifier == Identifier.OPERATOR_MULTIPLY || previousIdentifier == Identifier.OPERATOR_DIVISION) {
                        continue;
                    }
                }
                highestIdentifierToken = token;
                highestIdentifier = identifier;
                word = token.getText();
                currentHighestPriority = identifier.priority;
                highestIdentifierTokenIndex = i;
            }

            previousIdentifier = identifier;
        }

        if (highestIdentifier == null) {

            //If function invoke
            if (tokens.get(1).getIdentifier() == Identifier.BLOCK_OPERATOR_START) {
                List<Token> argumentTokenList = ParserUtil.getNestedToken(tokens, 1, Identifier.BLOCK_OPERATOR_START, Identifier.BLOCK_OPERATOR_END, false, false);
                Evaluator[] arguments = parseArgumentEvaluators(scope, argumentTokenList);

                PreLinkedFunctionOperator operator = new PreLinkedFunctionOperator(contanEngine, tokens.get(0), null, arguments);
                preLinkedFunctions.add(operator);

                return operator;
            }

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

                scope.addVariable(variableNameToken.getText());
                
                int rightTokenListLength = rightTokenList.size();
                if (rightTokenListLength != 1) {
                    //If the expression is incomplete.
                    if (rightTokenListLength < 3) {
                        ParserError.E0011.throwError("", rightTokenList.toArray(new Token[0]));
                    }
                    
                    Evaluator define = new CreateVariableOperator(contanEngine, rightTokenList.get(0));
                    Evaluator set = parseExpression(scope, rightTokenList);
                    
                    return new Expressions(define, set);
                } else {
                    return new CreateVariableOperator(contanEngine, rightTokenList.get(0));
                }
            }
            
            //20 + 10
            case OPERATOR_PLUS: {
                if (leftTokenList.size() == 0) {
                    return parseExpression(scope, rightTokenList);
                }
                
                Evaluator left = parseExpression(scope, leftTokenList);
                Evaluator right = parseExpression(scope, rightTokenList);
                
                return new AddOperator(contanEngine, highestIdentifierToken, left, right);
            }

            //20 - 10 or -20
            case OPERATOR_MINUS: {
                if (rightTokenList.size() == 0) {
                    ParserError.E0025.throwError("", highestIdentifierToken);
                }

                if (leftTokenList.size() == 0) {
                    //-20 or -value
                    return new InvertSignOperator(contanEngine, highestIdentifierToken, parseExpression(scope, rightTokenList));
                } else {
                    //20 - 10
                    Evaluator left = parseExpression(scope, leftTokenList);
                    Evaluator right = new InvertSignOperator(contanEngine, highestIdentifierToken, parseExpression(scope, rightTokenList));

                    return new AddOperator(contanEngine, highestIdentifierToken, left, right);
                }
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

            //20 / 10
            case OPERATOR_DIVISION: {
                if (leftTokenList.size() == 0 || rightTokenList.size() == 0) {
                    ParserError.E0012.throwError("", highestIdentifierToken);
                }

                Evaluator left = parseExpression(scope, leftTokenList);
                Evaluator right = parseExpression(scope, rightTokenList);

                return new DivisionOperator(contanEngine, highestIdentifierToken, left, right);
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
            case ASSIGNMENT: {
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
                int index = 0;
    
                List<Token> classObjectTokenList = ParserUtil.getTokensUntilFoundIdentifier(rightTokenList, 0, Identifier.BLOCK_OPERATOR_START);
                index += classObjectTokenList.size();
    
                List<Token> argTokenList = ParserUtil.getNestedToken(rightTokenList, index, Identifier.BLOCK_OPERATOR_START, Identifier.BLOCK_OPERATOR_END, false, false);
    
                Evaluator left = parseExpression(scope, classObjectTokenList);
                Evaluator[] arguments = parseArgumentEvaluators(scope, argTokenList);
    
                return new CreateClassInstanceOperator(contanEngine, classObjectTokenList.get(classObjectTokenList.size() - 1), left, arguments);
            }

            case DOT: {
                if (leftTokenList.size() == 0) {
                    ParserError.E0012.throwError("", highestIdentifierToken);
                }
                
                if (highestIdentifierToken instanceof BlockToken) {
                    //For [] operator
                    
                    Token originalToken = ((BlockToken) highestIdentifierToken).originalToken;
                    
                    if (rightTokenList.size() != 0) {
                        ParserError.E0030.throwError("", rightTokenList.get(0));
                        return null;
                    }
                    
                    Evaluator left = parseExpression(scope, leftTokenList);
                    
                    List<Token> keyTokens = ((BlockToken) highestIdentifierToken).tokens;
                    if (keyTokens.size() == 0) {
                        ParserError.E0029.throwError("", originalToken);
                        return null;
                    }
                    
                    Evaluator keyEval = parseExpression(scope, keyTokens);
                    
                    return new GetLinkedValueOperator(contanEngine, keyTokens.toArray(new Token[0]), left, keyEval);
                } else {
                    //For . operator
                    
                    if (rightTokenList.size() == 0) {
                        ParserError.E0012.throwError("", highestIdentifierToken);
                    }
                    
                    Token nameToken = rightTokenList.get(0);
                    
                    if (ParserUtil.containsIdentifier(rightTokenList, Identifier.BLOCK_OPERATOR_START)) {
                        //function
                        List<Token> argumentTokenList = ParserUtil.getNestedToken(rightTokenList, 1, Identifier.BLOCK_OPERATOR_START, Identifier.BLOCK_OPERATOR_END, false, false);
        
                        Evaluator left = parseExpression(scope, leftTokenList);
                        Evaluator[] arguments = parseArgumentEvaluators(scope, argumentTokenList);
        
                        PreLinkedFunctionOperator operator = new PreLinkedFunctionOperator(contanEngine, nameToken, left, arguments);
                        preLinkedFunctions.add(operator);
        
                        return operator;
                    } else {
                        //field
                        Evaluator left = parseExpression(scope, leftTokenList);
                        return new GetFieldOperator(contanEngine, nameToken, left);
                    }
                }
            }
            
            case CONSTANT_VARIABLE: {
                if (leftTokenList.size() != 0) {
                    ParserError.E0015.throwError("", leftTokenList.toArray(new Token[0]));
                }
                
                if (rightTokenList.size() < 3) {
                    ParserError.E0015.throwError("", rightTokenList.toArray(new Token[0]));
                }
                
                Token nameToken = rightTokenList.get(0);
                Token assignment = rightTokenList.get(1);
                
                if (!(nameToken.getIdentifier() == null && assignment.getIdentifier() == Identifier.ASSIGNMENT)) {
                    ParserError.E0015.throwError("", rightTokenList.toArray(new Token[0]));
                }
                
                moduleScope.addVariable(nameToken.getText());
                
                Evaluator right = parseExpression(scope, rightTokenList.subList(2, rightTokenList.size()));
                
                return new CreateConstVariableOperator(contanEngine, nameToken, right);
            }
            
            case LAMBDA: {
                if (leftTokenList.size() == 0 || rightTokenList.size() == 0) {
                    ParserError.E0012.throwError("", highestIdentifierToken);
                }
                
                List<Token> argTokens;
                if (leftTokenList.get(0).getIdentifier() == Identifier.BLOCK_OPERATOR_START) {
                    argTokens = ParserUtil.getNestedToken(leftTokenList, 0, Identifier.BLOCK_OPERATOR_START, Identifier.BLOCK_OPERATOR_END, false, false);
                } else {
                    argTokens = leftTokenList;
                }
                
                argTokens = ParserUtil.getDefinedArguments(argTokens);
                
                Scope newScope = new Scope(scope.getRootName() + ".lambda", scope, ScopeType.FUNCTION);
                argTokens.forEach(token -> newScope.addVariable(token.getText()));
                
                Evaluator right;
                
                if (rightTokenList.get(0) instanceof BlockToken) {
                    right = parseBlock(newScope, null, ((BlockToken) rightTokenList.get(0)).tokens);
                } else {
                    right = parseExpression(newScope, rightTokenList);
                }
                
                FunctionBlock functionBlock = new FunctionBlock(contanEngine, highestIdentifierToken, right, argTokens.toArray(new Token[0]));
                
                return new DefineFunctionExpressionOperator(contanEngine, highestIdentifierToken, functionBlock);
            }
            
            case ASYNC: {
                if (leftTokenList.size() != 0) {
                    ParserError.E0022.throwError("", highestIdentifierToken);
                }
                
                if (rightTokenList.size() == 0) {
                    ParserError.E0023.throwError("", highestIdentifierToken);
                }

                Scope newScope = new Scope(scope.getRootName() + ".async", scope, ScopeType.FUNCTION);

                Evaluator right;

                if (rightTokenList.get(0) instanceof BlockToken) {
                    right = parseBlock(newScope, null, ((BlockToken) rightTokenList.get(0)).tokens);
                } else {
                    ParserError.E0024.throwError("", highestIdentifierToken);
                    return null;
                }

                return new AsyncTaskOperator(contanEngine, highestIdentifierToken, right);
            }

            case SYNC: {
                if (leftTokenList.size() != 0) {
                    ParserError.E0022.throwError("", highestIdentifierToken);
                }

                if (rightTokenList.size() < 2) {
                    ParserError.E0023.throwError("", highestIdentifierToken);
                }

                Scope newScope = new Scope(scope.getRootName() + ".sync", scope, ScopeType.FUNCTION);

                int index = 0;
                for (Token token : rightTokenList) {
                    if (token instanceof BlockToken || token.getIdentifier() == Identifier.BLOCK_START) {
                        break;
                    }

                    if (index == rightTokenList.size() - 1) {
                        ParserError.E0024.throwError("", highestIdentifierToken);
                    }

                    index++;
                }

                Evaluator thread = parseExpression(scope, rightTokenList.subList(0, index));

                Evaluator right;

                if (rightTokenList.get(index) instanceof BlockToken) {
                    right = parseBlock(newScope, null, ((BlockToken) rightTokenList.get(index)).tokens);
                } else {
                    ParserError.E0024.throwError("", highestIdentifierToken);
                    return null;
                }

                return new SyncTaskOperator(contanEngine, highestIdentifierToken, thread, right);
            }
            
            case DELAY: {
                if (leftTokenList.size() != 0) {
                    ParserError.E0022.throwError("", highestIdentifierToken);
                }
    
                if (rightTokenList.size() == 0) {
                    ParserError.E0031.throwError("", highestIdentifierToken);
                }
    
                Scope newScope = new Scope(scope.getRootName() + ".sync", scope, ScopeType.FUNCTION);
    
                int index = 0;
                boolean hasBlock = false;
                for (Token token : rightTokenList) {
                    if (token instanceof BlockToken || token.getIdentifier() == Identifier.BLOCK_START) {
                        hasBlock = true;
                        break;
                    }
        
                    index++;
                }
    
                List<Token> delayTicksToken = rightTokenList.subList(0, index);
                if (delayTicksToken.size() == 0) {
                    ParserError.E0031.throwError("", highestIdentifierToken);
                }
                
                Evaluator delayTicks = parseExpression(scope, delayTicksToken);
    
                if (hasBlock) {
                    Evaluator right;
    
                    if (rightTokenList.get(index) instanceof BlockToken) {
                        right = parseBlock(newScope, null, ((BlockToken) rightTokenList.get(index)).tokens);
                    } else {
                        ParserError.E0024.throwError("", highestIdentifierToken);
                        return null;
                    }
    
                    return new DelayTaskOperator(contanEngine, highestIdentifierToken, delayTicks, right);
                } else {
                    return new DelayOperator(contanEngine, highestIdentifierToken, delayTicks);
                }
            }
            
            //Pattern of "data func = function() {/*do something*/}"
            case FUNCTION: {
                if (rightTokenList.size() == 0) {
                    ParserError.E0021.throwError("", highestIdentifierToken);
                }
                
                if (rightTokenList.get(0).getIdentifier() != Identifier.BLOCK_OPERATOR_START) {
                    ParserError.E0021.throwError("", highestIdentifierToken);
                }
                
                List<Token> argTokens = ParserUtil.getNestedToken(rightTokenList, 0, Identifier.BLOCK_OPERATOR_START, Identifier.BLOCK_OPERATOR_END, false, false);
    
                int index = argTokens.size() + 2;
                
                argTokens = ParserUtil.getDefinedArguments(argTokens);
    
                Scope newScope = new Scope(scope.getRootName() + ".function_expression", scope, ScopeType.FUNCTION);
                argTokens.forEach(token -> newScope.addVariable(token.getText()));
    
                Evaluator blockEval;
    
                if (rightTokenList.get(index) instanceof BlockToken) {
                    blockEval = parseBlock(newScope, null, ((BlockToken) rightTokenList.get(index)).tokens);
                } else {
                    ParserError.E0021.throwError("", highestIdentifierToken);
                    blockEval = null;
                }
    
                FunctionBlock functionBlock = new FunctionBlock(contanEngine, highestIdentifierToken, blockEval, argTokens.toArray(new Token[0]));
    
                return new DefineFunctionExpressionOperator(contanEngine, highestIdentifierToken, functionBlock);
            }
            
            case RETURN: {
                if (leftTokenList.size() != 0) {
                    ParserError.E0019.throwError("", highestIdentifierToken);
                }
                
                Evaluator right;
                if (rightTokenList.size() == 0) {
                    right = NullEvaluator.INSTANCE;
                } else {
                    right = parseExpression(scope, rightTokenList);
                }

                return new SetReturnValueOperator(contanEngine, highestIdentifierToken, right);
            }

            case SKIP:
            
            case STOP: {
                if (leftTokenList.size() != 0) {
                    ParserError.E0019.throwError("", highestIdentifierToken);
                }

                String name = "";
                if (rightTokenList.size() == 1) {
                    Token rightFirst = rightTokenList.get(0);
                    
                    if (rightFirst.getIdentifier() == null) {
                        name = rightFirst.getText();
                        
                        if (!scope.hasVariable(name)) {
                            ParserError.E0028.throwError("", rightFirst);
                        }
                    } else {
                        ParserError.E0028.throwError("", highestIdentifierToken);
                        return null;
                    }
                }
    
                CancelStatus cancelStatus = highestIdentifier == Identifier.STOP ? CancelStatus.STOP : CancelStatus.SKIP;
                
                return new RepeatStopOrSkipOperator(contanEngine, highestIdentifierToken, name, cancelStatus);
            }
        }

        ParserError.E0000.throwError(word, highestIdentifierToken);
        return null;
    }


    private Evaluator[] parseArgumentEvaluators(Scope scope, List<Token> tokens) throws ContanParseException {
        int length = tokens.size();

        List<Token> evalTokens = new ArrayList<>();
        List<Evaluator> evaluators = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            Token token = tokens.get(i);
            Identifier identifier = token.getIdentifier();
    
            //Skip over the contents of the parentheses.
            if (identifier == Identifier.BLOCK_OPERATOR_START) {
                List<Token> nestedTokenList = ParserUtil.getNestedToken(tokens, i, Identifier.BLOCK_OPERATOR_START, Identifier.BLOCK_OPERATOR_END, true, false);
                i += nestedTokenList.size() - 1;
                evalTokens.addAll(nestedTokenList);
            }

            if (identifier != Identifier.ARGUMENT_SPLIT && identifier != Identifier.BLOCK_OPERATOR_START && identifier != Identifier.BLOCK_OPERATOR_END) {
                evalTokens.add(token);
            }

            if (identifier == Identifier.ARGUMENT_SPLIT || i == length - 1) {
                evaluators.add(new RemoveReferenceOperator(contanEngine, tokens.get(length - 1), parseBlock(scope, null, evalTokens)));
                evalTokens.clear();
            }
        }

        return evaluators.toArray(new Evaluator[0]);
    }

}
