package org.contan_lang;

public class ContanRunner {

    private final ContanEngine engine;
    
    public ContanRunner(ContanEngine engine){
        this.engine = engine;
    }
    
    public ContanEngine getEngine() {return engine;}
    
    public static <T> void test(T object, int i){System.out.println("test! " + i);}
    
}
