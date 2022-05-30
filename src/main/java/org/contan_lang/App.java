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
        
        String test1 = "\n" +
                "import TestClass2 = importModule(\"test/TestModule2.cntn\").TestClass2\n" +
                "\n" +
                "data instance = new TestClass()\n" +
                "instance.test(\"TEST!!\")\n" +
                "instance.say()\n" +
                "\n" +
                "class TestClass() extends TestClass2 {\n" +
                "\n" +
                "    function test(text) {\n" +
                "        this.text = text\n" +
                "    }\n" +
                "\n" +
                "    function say() {\n" +
                "        print(text)\n" +
                "    }\n" +
                "\n" +
                "}";
        
        String test2 = "\n" +
                "class TestClass2() {\n" +
                "    data text\n" +
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
