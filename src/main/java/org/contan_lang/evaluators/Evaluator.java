package org.contan_lang.evaluators;

import org.contan_lang.environment.Environment;
import org.contan_lang.variables.ContanObject;

public interface Evaluator {
    
    ContanObject<?> eval(Environment environment);
    
}
