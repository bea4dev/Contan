package org.contan_lang.variables;

public enum NumberType {
    INTEGER,
    LONG,
    FLOAT,
    DOUBLE;
    
    public static NumberType getType(double number) {
        if ((int) number == number) {
            return INTEGER;
        } else if ((float) number == number) {
            return FLOAT;
        } else if ((long) number == number) {
            return LONG;
        } else {
            return DOUBLE;
        }
    }
}
