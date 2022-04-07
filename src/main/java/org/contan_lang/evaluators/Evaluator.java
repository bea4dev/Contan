package org.contan_lang.evaluators;

import org.contan_lang.environment.Environment;
import org.contan_lang.variables.ContanVariable;

public interface Evaluator {
    
    ContanVariable<?> eval(Environment environment);
    
}
