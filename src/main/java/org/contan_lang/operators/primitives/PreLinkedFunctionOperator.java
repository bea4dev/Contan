package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.ContanObjectReference;
import org.contan_lang.environment.CoroutineStatus;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.ClassBlock;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.evaluators.FunctionBlock;
import org.contan_lang.operators.Operator;
import org.contan_lang.runtime.ContanRuntimeUtil;
import org.contan_lang.runtime.JavaContanFuture;
import org.contan_lang.standard.classes.StandardClasses;
import org.contan_lang.standard.functions.StandardFunctions;
import org.contan_lang.syntax.exception.ContanParseException;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanClassInstance;
import org.contan_lang.variables.primitive.ContanFunctionExpression;
import org.contan_lang.variables.primitive.ContanYieldObject;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class PreLinkedFunctionOperator extends Operator {
    
    private final ContanEngine contanEngine;
    
    private final Token functionName;
    
    private final Evaluator left;
    
    private final Evaluator[] args;
    
    private FunctionBlock functionBlock;
    
    private Environment moduleEnvironment;
    
    public PreLinkedFunctionOperator(ContanEngine contanEngine, Token functionName, @Nullable Evaluator left, Evaluator... args) {
        super(contanEngine, functionName, args);
        this.contanEngine = contanEngine;
        this.functionName = functionName;
        this.left = left;
        this.args = args;
    }
    
    public Token getFunctionName() {return functionName;}
    
    public Evaluator[] getArgs() {return args;}
    
    
    public void link(Collection<FunctionBlock> moduleFunctions, Environment moduleEnvironment) throws ContanParseException {
        if (left != null) return;
        
        for (FunctionBlock functionBlock : moduleFunctions) {
            if (!functionBlock.getFunctionName().getText().equals(this.functionName.getText())) continue;
            
            int functionArgLength = functionBlock.getArgs().length;
            int thisArgLength = args.length;
            
            if (functionArgLength == thisArgLength) {
                this.functionBlock = functionBlock;
                this.moduleEnvironment = moduleEnvironment;
                return;
            }
        }
        
        this.functionBlock = StandardFunctions.FUNCTIONS.get(this.functionName.getText());
        /*
        if (functionBlock == null) {
            ParserError.E0014.throwError("", functionName);
        }*/
    }
    
    @Override
    public ContanObject<?> eval(Environment environment) {
    
        ContanThread contanThread = environment.getContanThread();
    
        int startIndex = 0;
        CoroutineStatus coroutineStatus = environment.getCoroutineStatus(this);
        ContanObject<?>[] variables = new ContanObject<?>[args.length];
    
        if (coroutineStatus != null) {
            startIndex = (int) coroutineStatus.count;
            
            //Cached return value
            if (startIndex == args.length) {
                return coroutineStatus.results[0];
            }
            
            System.arraycopy(coroutineStatus.results, 0, variables, 0, startIndex);
        }
    
        for (int i = startIndex; i < args.length; i++) {
            ContanObject<?> result = args[i].eval(environment).createClone();
            result = ContanRuntimeUtil.dereference(token, result);
        
            if (environment.hasYieldReturnValue() || result == ContanYieldObject.INSTANCE) {
                ContanObject<?>[] results = new ContanObject<?>[i + 1];
                System.arraycopy(variables, 0, results, 0, i);
            
                environment.setCoroutineStatus(this, i, results);
                return ContanYieldObject.INSTANCE;
            }

            variables[i] = result;
        }
        
        if (functionBlock != null) {
            ContanObject<?> returned = functionBlock.eval(moduleEnvironment, functionName, contanThread, variables);
            returned = ContanRuntimeUtil.dereference(token, returned);

            //Cache returned Completable
            if (returned.getBasedJavaObject() == StandardClasses.FUTURE) {
                environment.setCoroutineStatus(this, args.length, returned);
            }
            return returned;
        }

        
        if (left == null) {
            //For class method
            ContanObjectReference reference = environment.getVariable("this");
            if (reference != null) {
                ContanObject<?> classInstance = ContanRuntimeUtil.dereference(functionName, reference);
        
                if (!(classInstance instanceof ContanClassInstance)) {
                    ContanRuntimeError.E0000.throwError("\n'this' is not class instance.", null, functionName);
                    return null;
                }
        
                ContanClassInstance instance = (ContanClassInstance) classInstance;
                ClassBlock classBlock = instance.getBasedJavaObject();
        
                if (classBlock.hasFunction(functionName.getText(), variables.length)) {
                    return instance.invokeFunction(contanThread, functionName, variables);
                }
            }
    
            //For function or lambda expression
            ContanObjectReference resultReference = environment.getVariable(functionName.getText());
            
            if (resultReference == null) {
                ContanRuntimeError.E0011.throwError("", null, functionName);
                return null;
            }
            
            ContanObject<?> result = ContanRuntimeUtil.dereference(functionName, resultReference);
            
            if (result instanceof ContanFunctionExpression) {
                ContanObject<?> returned = ((ContanFunctionExpression) result).eval(contanThread, functionName, variables);

                //Cache returned Completable
                if (returned.getBasedJavaObject() == StandardClasses.FUTURE) {
                    environment.setCoroutineStatus(this, args.length, ContanRuntimeUtil.dereference(functionName, returned));
                }

                return returned;
            } else {
                ContanRuntimeError.E0011.throwError("", null, functionName);
                return null;
            }
        }
    
        ContanObject<?> leftResult = left.eval(environment);
        
        //For 'Completable.await()'
        leftResult = ContanRuntimeUtil.dereference(functionName, leftResult);
        if (leftResult.getBasedJavaObject() == StandardClasses.FUTURE) {
            if (functionName.getText().equals("await")) {
                ContanObject<?> contanObject = ((ContanClassInstance) leftResult).getEnvironment().getVariable("javaFuture");
    
                if (contanObject == null) {
                    ContanRuntimeError.E0000.throwError("", null, functionName);
                    return null;
                }
    
                if (!(contanObject.getBasedJavaObject() instanceof JavaContanFuture)) {
                    ContanRuntimeError.E0000.throwError("", null, functionName);
                    return null;
                }
    
                JavaContanFuture javaContanFuture = (JavaContanFuture) contanObject.getBasedJavaObject();

                try {
                    javaContanFuture.LOCK.lock();
                    
                    if (javaContanFuture.isDone()) {
                        //Reset the return value later.
                        environment.getContanThread().scheduleTask(() -> {
                            environment.setReturnValue(null);
                            return null;
                        });
                        //Rerun later
                        //environment.rerun();
                        //environment.setCoroutineStatus(this, args.length, javaContanFuture.getResult());

                        Environment returnEnv = environment.getReturnableEnvironment();
                        if (returnEnv != null) {
                            returnEnv.rerun();
                            returnEnv.setCoroutineStatus(this, args.length, javaContanFuture.getResult());
                        }
                    } else {
                        javaContanFuture.addAwaitEnvironment(environment);
                    }
                    environment.setReturnValue(ContanYieldObject.INSTANCE);
                    return ContanYieldObject.INSTANCE;
                } finally {
                    javaContanFuture.LOCK.unlock();
                }
            }
        }

        ContanObject<?> returned = leftResult.invokeFunction(contanThread, functionName, variables);
        //Cache returned Completable
        if (returned.getBasedJavaObject() == StandardClasses.FUTURE) {
            environment.setCoroutineStatus(this, args.length, returned);
        }
        return returned;
    }
    
}
