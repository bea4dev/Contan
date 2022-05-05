package org.contan_lang.standard.classes;

import org.contan_lang.environment.Environment;
import org.contan_lang.evaluators.ClassBlock;

public class StandardClasses {

    public static ClassBlock COMPLETABLE = new Completable(null, "standard.Completable", new Environment(null, null));

}
