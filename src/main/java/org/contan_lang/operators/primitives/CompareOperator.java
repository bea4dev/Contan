package org.contan_lang.operators.primitives;

import org.contan_lang.ContanEngine;
import org.contan_lang.environment.CoroutineStatus;
import org.contan_lang.environment.Environment;
import org.contan_lang.environment.expection.ContanRuntimeError;
import org.contan_lang.evaluators.Evaluator;
import org.contan_lang.runtime.ContanRuntimeUtil;
import org.contan_lang.syntax.exception.ContanParseException;
import org.contan_lang.syntax.exception.ParserError;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanBoolean;
import org.contan_lang.variables.primitive.ContanYieldObject;

public class CompareOperator extends BooleanBaseOperator {

    private final CompareType compareType;

    public CompareOperator(ContanEngine contanEngine, Token token, CompareType compareType, Evaluator... operators) {
        super(contanEngine, token, operators);
        this.compareType = compareType;

    }

    @Override
    public ContanObject<Boolean> eval(Environment environment) {
        ContanObject<?> contanObject0;
        ContanObject<?> contanObject1;

        CoroutineStatus coroutineStatus = environment.getCoroutineStatus(this);

        if (coroutineStatus == null) {
            contanObject0 = operators[0].eval(environment);
            if (environment.hasYieldReturnValue() || contanObject0 == ContanYieldObject.INSTANCE) {
                return ContanYieldObject.INSTANCE;
            }
        } else {
            contanObject0 = coroutineStatus.results[0];
        }

        contanObject1 = operators[1].eval(environment);
        if (environment.hasYieldReturnValue() || contanObject1 == ContanYieldObject.INSTANCE) {
            environment.setCoroutineStatus(this, 0, contanObject0);
            return ContanYieldObject.INSTANCE;
        }

        contanObject0 = ContanRuntimeUtil.dereference(token, contanObject0);
        contanObject1 = ContanRuntimeUtil.dereference(token, contanObject1);

        Object first = contanObject0.getBasedJavaObject();
        Object second = contanObject1.getBasedJavaObject();

        double left;
        if (first instanceof Integer) {
            left = (double) (Integer) first;
        } else if (first instanceof Long) {
            left = (double) (Long) first;
        } else if (first instanceof Float) {
            left = (double) (Float) first;
        } else if (first instanceof Double){
            left = (double) (Double) first;
        } else {
            ContanRuntimeError.E0009.throwError("", null, token);
            left = Double.NaN;
        }

        double right;
        if (second instanceof Integer) {
            right = (double) (Integer) second;
        } else if (second instanceof Long) {
            right = (double) (Long) second;
        } else if (second instanceof Float) {
            right = (double) (Float) second;
        } else if (second instanceof Double){
            right = (double) (Double) second;
        } else {
            ContanRuntimeError.E0009.throwError("", null, token);
            right = Double.NaN;
        }

        boolean bool;
        switch (this.compareType) {
            case IS_LEFT_BIGGER: {
                bool = left > right;
                break;
            }
            case IS_RIGHT_BIGGER: {
                bool = left < right;
                break;
            }
            case IS_LEFT_BIGGER_OR_EQUAL: {
                bool = left >= right;
                break;
            }
            case IS_RIGHT_BIGGER_OR_EQUAL: {
                bool = left <= right;
                break;
            }
            default: {
                bool = false;
                ContanRuntimeError.E0000.throwError("Invalid compare type.", null, token);
            }
        }

        return new ContanBoolean(contanEngine, bool);
    }


    public static enum CompareType {
        IS_LEFT_BIGGER,
        IS_RIGHT_BIGGER,
        IS_LEFT_BIGGER_OR_EQUAL,
        IS_RIGHT_BIGGER_OR_EQUAL;

        public static CompareType getFromIdentifier(Token token) throws ContanParseException {
            CompareType compareType;
            switch (token.getText()) {
                case "<": {
                    compareType = CompareType.IS_RIGHT_BIGGER;
                    break;
                }
                case ">": {
                    compareType = CompareType.IS_LEFT_BIGGER;
                    break;
                }
                case "<=": {
                    compareType = CompareType.IS_RIGHT_BIGGER_OR_EQUAL;
                    break;
                }
                case ">=": {
                    compareType = CompareType.IS_LEFT_BIGGER_OR_EQUAL;
                    break;
                }
                default: {
                    ParserError.E0000.throwError("Unknown compare type.", token);
                    compareType = CompareType.IS_LEFT_BIGGER;
                    break;
                }
            }
            return compareType;
        }

    }

}
