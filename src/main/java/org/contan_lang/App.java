package org.contan_lang;

import org.contan_lang.syntax.exception.ContanParseException;
import org.contan_lang.thread.ContanThread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        /*
        for (Token token : tokens) {
            if (token instanceof IdentifierToken) {
                System.out.println(((IdentifierToken) token).getIdentifier().name());
            } else {
                System.out.println(token.getText());
            }
        }*/
    
        long start = System.currentTimeMillis();
        
        double i;
        double s = 0.0;
        for(i = 0; i <= 100000; i+=1.0){
            s += Math.pow(-1.0, i)/(2.0 * i + 1.0);
        }
        System.out.println("Java Result : " + 4.0 * s);
        long end = System.currentTimeMillis();
        System.out.println("Java Time : " + (end - start) + " [ms]");
        
        String test1 = "\n" +
                "import System = importJava(\"java.lang.System\")\n" +
                "\n" +
                "data start = System.currentTimeMillis()\n" +
                "\n" +
                "data sum = 0.0;\n" +
                "data i = 0.0\n" +
                "repeat 100000 {\n" +
                "\n" +
                "    sum += Math.pow(-1.0, i) / (2.0 * i + 1.0);\n" +
                "    i += 1.0\n" +
                "\n" +
                "}\n" +
                "\n" +
                "data end = System.currentTimeMillis()\n" +
                "\n" +
                "print(\"Contan Result : \" + (sum * 4.0))\n" +
                "print(\"Contan Time : \" + (end - start) + \" [ms]\")";
        
        String test2 = "\n" +
                "import Thread = importJava(\"java.lang.Thread\")\n" +
                "\n" +
                "class TestClass(i, j) {\n" +
                "    \n" +
                "    data sum\n" +
                "\n" +
                "    initialize {\n" +
                "        sum = i + j\n" +
                "    }\n" +
                "\n" +
                "    function test(t) {\n" +
                "        return async {\n" +
                "            \n" +
                "            \n" +
                "            return \"Result is \" + (sum + t)\n" +
                "        }.await()\n" +
                "    }\n" +
                "\n" +
                "}";


        ContanEngine contanEngine = new ContanEngine();
        ContanThread mainThread = contanEngine.getMainThread();

        try {
            ContanModule module1 = contanEngine.compile("test/TestModule1.cntn", test1);
            ContanModule module2 = contanEngine.compile("test/TestModule2.cntn", test2);
            
            //module2.initialize(mainThread);
            module1.initialize(mainThread);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            contanEngine.getMainThread().shutdownWithAwait(1, TimeUnit.SECONDS);

            for (ContanThread contanThread : contanEngine.getAsyncThreads()) {
                contanThread.shutdownWithAwait(1, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }
}
