package org.contan_lang.syntax.parser;

import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.*;
import org.contan_lang.operators.primitives.*;
import org.contan_lang.standard.functions.StandardFunctions;
import org.contan_lang.syntax.Identifier;
import org.contan_lang.syntax.Lexer;
import org.contan_lang.syntax.exception.UnexpectedSyntaxException;
import org.contan_lang.syntax.tokens.IdentifierToken;
import org.contan_lang.syntax.tokens.NameToken;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.primitive.ContanFloat;
import org.contan_lang.variables.primitive.ContanInteger;
import org.contan_lang.variables.primitive.ContanString;
import org.contan_lang.variables.primitive.ContanVoid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Parser {
    
    private List<PreLinkedFunctionEvaluator> preLinkedFunctions;
    
    public ScriptTree parse(String text) throws UnexpectedSyntaxException {
        List<Token> tokens = Lexer.split(text);
        List<FunctionBlock> functions = new ArrayList<>();
        preLinkedFunctions = new ArrayList<>();
    
        int length = tokens.size();
        boolean isInFunction = false;
        Token functionName = new NameToken("");
        List<Token> argsTokens = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Token token = tokens.get(i);
            
            Identifier identifier = null;
            if (token instanceof IdentifierToken) {
                identifier = ((IdentifierToken) token).getIdentifier();
            }
            
            //Get function name
            if (token instanceof NameToken && isInFunction) {
                functionName = token;
            }
            
            if (identifier == Identifier.BLOCK_OPERATOR_START) {
                argsTokens = getNestedToken(tokens, i, Identifier.BLOCK_OPERATOR_START, Identifier.BLOCK_OPERATOR_END);
                i += argsTokens.size();
            }
    
            if (identifier == Identifier.BLOCK_START) {
                List<Token> inBlockTokens = getNestedToken(tokens, i, Identifier.BLOCK_START, Identifier.BLOCK_END);
                i += inBlockTokens.size();
                
                List<Token> args = new ArrayList<>();
                for (Token t : argsTokens) {
                    Identifier id = null;
                    if (t instanceof IdentifierToken) {
                        id = ((IdentifierToken) t).getIdentifier();
                    }
        
                    if (id != Identifier.ARGUMENT_SPLIT) {
                        args.add(t);
                    }
                }
                
                functions.add(new FunctionBlock(functionName, new Expressions(parseBlock(inBlockTokens)), args.toArray(new Token[0])));
                
                isInFunction = false;
                functionName = new NameToken("");
                argsTokens.clear();
            }
            
            if (identifier == Identifier.FUNCTION) {
                isInFunction = true;
            }
        }
        
        for (PreLinkedFunctionEvaluator functionEvaluator : preLinkedFunctions) {
            functionEvaluator.link(functions);
        }
        
        return new ScriptTree(this, functions);
    }
    
    public Evaluator parseBlock(List<Token> tokens) throws UnexpectedSyntaxException {
        int length = tokens.size();
        
        List<Evaluator> blockEvaluators = new ArrayList<>();
        
        List<Token> expressionTokens = new ArrayList<>();
        List<Token> nestBlockTokens = new ArrayList<>();
        int blockNest = 0;
        for (int i = 0; i < length; i++) {
            Token token = tokens.get(i);
            
            Identifier identifier = null;
            if (token instanceof IdentifierToken) {
                identifier = ((IdentifierToken) token).getIdentifier();
            }
            
            if (identifier == Identifier.IF) {
                blockNest++;
            }
            
            if (identifier == Identifier.BLOCK_END) {
                blockNest--;
                if (blockNest == 0) {
                    nestBlockTokens.add(token);
                    blockEvaluators.add(parseNestedBlock(nestBlockTokens));
                    nestBlockTokens.clear();
                }
            }
            
            if (blockNest == 0 && identifier != Identifier.EXPRESSION_SPLIT && identifier != Identifier.BLOCK_END) {
                expressionTokens.add(token);
            }
            
            if (identifier == Identifier.EXPRESSION_SPLIT && blockNest == 0) {
                blockEvaluators.add(parseExpression(expressionTokens));
                expressionTokens.clear();
            }
            
            if (blockNest != 0) {
                nestBlockTokens.add(token);
            }
        }
        
        return new Expressions(blockEvaluators.toArray(new Evaluator[0]));
    }
    
    public Evaluator parseExpression(List<Token> tokens) throws UnexpectedSyntaxException {
        int length = tokens.size();
        
        
        //Remove block operator
        if (length >= 2) {
            Token ts = tokens.get(0);
            Token te = tokens.get(length - 1);
            
            if (ts instanceof IdentifierToken && te instanceof IdentifierToken) {
                if (((IdentifierToken) ts).getIdentifier() == Identifier.BLOCK_OPERATOR_START &&
                        ((IdentifierToken) te).getIdentifier() == Identifier.BLOCK_OPERATOR_END) {
                    return parseExpression(getNestedToken(tokens, 0, Identifier.BLOCK_OPERATOR_START, Identifier.BLOCK_OPERATOR_END));
                }
            }
        }
        
        //Function
        if (tokens.size() >= 3) {
            if (tokens.get(0) instanceof NameToken) {
                if (tokens.get(1) instanceof IdentifierToken) {
                    Identifier identifier = ((IdentifierToken) tokens.get(1)).getIdentifier();
                    if (identifier == Identifier.BLOCK_OPERATOR_START) {
                        List<Token> args = tokens.subList(2, length - 1);
                        
                        Token lastToken = tokens.get(length - 1);
                        if (lastToken instanceof IdentifierToken) {
                            if (((IdentifierToken) lastToken).getIdentifier() != Identifier.BLOCK_OPERATOR_END) {
                                throw new UnexpectedSyntaxException(lastToken.getText());
                            }
                        } else {
                            throw new UnexpectedSyntaxException("");
                        }
                        
                        List<Token> evalTokens = new ArrayList<>();
                        List<Evaluator> evaluators = new ArrayList<>();
                        
                        int argLength = args.size();
                        for (int i = 0; i < argLength; i++) {
                            Token token = args.get(i);
                            
                            Identifier id = null;
                            if (token instanceof IdentifierToken) {
                                id = ((IdentifierToken) token).getIdentifier();
                            }
                            
                            if (id != Identifier.ARGUMENT_SPLIT) {
                                evalTokens.add(token);
                                if (i == argLength - 1) {
                                    evaluators.add(parseExpression(evalTokens));
                                    evalTokens.clear();
                                }
                            } else {
                                evaluators.add(parseExpression(evalTokens));
                                evalTokens.clear();
                            }
                        }
                        
                        
                        PreLinkedFunctionEvaluator functionEvaluator = new PreLinkedFunctionEvaluator(tokens.get(0), evaluators.toArray(new Evaluator[0]));
                        preLinkedFunctions.add(functionEvaluator);
                        return functionEvaluator;
                    }
                }
            }
        }
        
        
        int highestIdentifier = 0;
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            
            if (token instanceof IdentifierToken) {
                Identifier identifier = ((IdentifierToken) token).getIdentifier();
                if (identifier == Identifier.BLOCK_OPERATOR_START) {
                    i += getNestedToken(tokens, i, Identifier.BLOCK_OPERATOR_START, Identifier.BLOCK_OPERATOR_END).size();
                }
                
                highestIdentifier = Math.max(highestIdentifier, identifier.priority);
            }
        }
        
        //String
        if (tokens.size() == 3) {
            Token t0 = tokens.get(0);
            Token t1 = tokens.get(1);
            Token t2 = tokens.get(2);
            
            if (t0 instanceof IdentifierToken && t2 instanceof IdentifierToken) {
                if (((IdentifierToken) t0).getIdentifier() == Identifier.DEFINE_STRING_START_OR_END
                        && ((IdentifierToken) t2).getIdentifier() == Identifier.DEFINE_STRING_START_OR_END) {
                    return new Expression(new DefinedValueOperator(new ContanString(t1.getText())));
                }
            }
        }
        
        //Number
        if (highestIdentifier == 0) {
            for (Token token : tokens) {
                if (token instanceof NameToken) {
                    String name = token.getText();
                    if (!name.matches("[+-]?\\d+(?:\\.\\d+)?")) {
                        return new Expression(new GetValueOperator(name));
                    } else if (name.contains(".")) {
                        return new Expression(new DefinedValueOperator(new ContanFloat(Double.parseDouble(name))));
                    } else {
                        return new Expression(new DefinedValueOperator(new ContanInteger(Long.parseLong(name))));
                    }
                }
            }
            return new Expression(new DefinedValueOperator(ContanVoid.INSTANCE));
        }
        
        //Parse expression
        List<Token> treeTokens = new ArrayList<>();
        List<Token> treeRTokens = new ArrayList<>();
        List<Token> treeLTokens = new ArrayList<>();
        Identifier id = null;
        int nest = 0;
        boolean isHitIdentifier = false;
        for (int i = length - 1; i >= 0; i--) {
            Token token = tokens.get(i);

            Identifier identifier = null;
            if (token instanceof IdentifierToken) {
                identifier = ((IdentifierToken) token).getIdentifier();
            }
    
    
            if (identifier == Identifier.BLOCK_OPERATOR_START) {
                nest--;
            }
            
            if (identifier != null) {
                
                if (identifier.priority >= 10) {
                    throw new UnexpectedSyntaxException("");
                }

                if (identifier.priority == highestIdentifier && nest == 0) {
                    if (!isHitIdentifier) {
                        Collections.reverse(treeTokens);
                        id = identifier;
                        treeRTokens = treeTokens;

                        treeTokens = new ArrayList<>();
                        isHitIdentifier = true;
                        continue;
                    }
                }
            }
    
            if (identifier == Identifier.BLOCK_OPERATOR_END) {
                nest++;
            }
            
            treeTokens.add(token);
            

            if (i == 0) {
                Collections.reverse(treeTokens);
                treeLTokens = treeTokens;
                break;
            }
        }
        
        
        if (id == null) {
            return new Expression(new DefinedValueOperator(ContanVoid.INSTANCE));
        }
        
        switch (id) {
            //data
            case DEFINE_DATA: {
                if (treeRTokens.size() < 1) {
                    throw new UnexpectedSyntaxException("");
                }
                
                Token nameToken = treeRTokens.get(0);
                if (!(nameToken instanceof NameToken)) {
                    throw new UnexpectedSyntaxException("");
                }

                if (treeRTokens.size() > 2) {
                    
                    Token subsToken = treeRTokens.get(1);
                    if (!(subsToken instanceof IdentifierToken)) {
                        throw new UnexpectedSyntaxException("");
                    } else {
                        Identifier identifier = ((IdentifierToken) subsToken).getIdentifier();
                        if (identifier != Identifier.SUBSTITUTION) {
                            throw new UnexpectedSyntaxException("");
                        }
                    }

                    List<Token> setValueTokens = treeRTokens.subList(0, treeRTokens.size());
                    Evaluator defineEval = new Expression(new CreateVariableOperator(nameToken.getText()));
                    Evaluator valueEval = parseExpression(setValueTokens);
                    
                    return new Expressions(defineEval, valueEval);
                    
                } else {
                    return new Expression(new CreateVariableOperator(nameToken.getText()));
                }
            }
            
            //=
            case SUBSTITUTION: {
                if (treeRTokens.size() < 1 || treeLTokens.size() < 1 ) {
                    throw new UnexpectedSyntaxException("");
                }

                Token nameToken = treeLTokens.get(0);
                if (!(nameToken instanceof NameToken)) {
                    throw new UnexpectedSyntaxException("");
                }
                
                Evaluator valueEval = parseExpression(treeRTokens);
                
                return new Expression(new SetValueOperator(nameToken.getText(), valueEval));
            }
            
            //+
            case OPERATOR_PLUS: {
                Evaluator left = parseExpression(treeLTokens);
                Evaluator right = parseExpression(treeRTokens);
                
                return new Expression(new AddOperator(left, right));
            }

            //*
            case OPERATOR_MULTIPLY: {
                Evaluator left = parseExpression(treeLTokens);
                Evaluator right = parseExpression(treeRTokens);
        
                return new Expression(new MultiplyOperator(left, right));
            }
            
            //==
            case OPERATOR_EQUAL: {
                Evaluator left = parseExpression(treeLTokens);
                Evaluator right = parseExpression(treeRTokens);
                
                return new Expression(new EqualOperator(left, right));
            }
        }
        
        
        return new Expressions();
    }
    
    
    public Evaluator parseNestedBlock(List<Token> tokens) throws UnexpectedSyntaxException {
        if (tokens.size() == 0) return new Expressions();
        
        Token firstToken = tokens.get(0);
        Identifier firstIdentifier;
        if (firstToken instanceof IdentifierToken) {
            firstIdentifier = ((IdentifierToken) firstToken).getIdentifier();
        } else {
            throw new UnexpectedSyntaxException("");
        }
        
        if (firstIdentifier == Identifier.IF) {
    
            int length = tokens.size();
            if (length < 5) {
                throw new UnexpectedSyntaxException("");
            }
            
            List<Token> ifTokens = null;
            List<Token> blockTokens = null;
            for (int i = 1; i < length; i++) {
                Token token = tokens.get(i);
        
                Identifier identifier = null;
                if (token instanceof IdentifierToken) {
                    identifier = ((IdentifierToken) token).getIdentifier();
                }
    
                if (identifier == Identifier.BLOCK_OPERATOR_START) {
                    ifTokens = getNestedToken(tokens, i, Identifier.BLOCK_OPERATOR_START, Identifier.BLOCK_OPERATOR_END);
                    i += ifTokens.size();
                }
                
                if (identifier == Identifier.BLOCK_START) {
                    blockTokens = getNestedToken(tokens, i, Identifier.BLOCK_START, Identifier.BLOCK_END);
                    i += blockTokens.size();
                }
            }
            
            if (ifTokens == null) {
                throw new UnexpectedSyntaxException("");
            }
            if (blockTokens == null) {
                throw new UnexpectedSyntaxException("");
            }
            
            Evaluator ifEval = parseExpression(ifTokens);
            Evaluator blockEval = parseBlock(blockTokens);
            
            return new IfElseEvaluator(ifEval, blockEval, null);
        }
        
        throw new UnexpectedSyntaxException("");
    }
    
    
    public List<Token> getNestedToken(List<Token> tokens, int startIndex, Identifier start, Identifier end) throws UnexpectedSyntaxException {
        int length = tokens.size();
        List<Token> nestedToken = new ArrayList<>();
        int nest = 0;
        for (int i = startIndex; i < length; i++) {
            Token token = tokens.get(i);
    
            Identifier identifier = null;
            if (token instanceof IdentifierToken) {
                identifier = ((IdentifierToken) token).getIdentifier();
            }
            
            if (identifier == end && i != startIndex) {
                nest--;
                if (nest == 0) {
                    return nestedToken;
                }
            }
            
            if (nest != 0) {
                nestedToken.add(token);
            }
            
            if (identifier == start) {
                nest++;
            }
            
        }
        
        throw new UnexpectedSyntaxException("");
    }
    
    
    
    public Identifier getNextIdentifier(List<Token> tokens, int index) {
        int length = tokens.size();
        for (int i = index; i < length; i++) {
            Token token = tokens.get(i);
        
            Identifier identifier = null;
            if (token instanceof IdentifierToken) {
                identifier = ((IdentifierToken) token).getIdentifier();
            }
        
            if (identifier != null) {
                return identifier;
            }
        }
        
        return null;
    }
    
}
