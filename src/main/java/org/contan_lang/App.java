package org.contan_lang;

import org.contan_lang.syntax.Lexer;
import org.contan_lang.syntax.exception.ContanParseException;
import org.contan_lang.syntax.parser.Parser;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.thread.ContanThread;

import java.util.List;
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
        
        
        
        String test = "import Thread = importJava(\"java.lang.Thread\")\n" +
                "\n" +
                "data text = async {\n" +
                "    \n" +
                "    print(\"SLEEP!\")\n" +
                "    \n" +
                "    Thread.sleep(1000)\n" +
                "    \n" +
                "    return sync @MAIN_THREAD { \"Hello world from main thread!\" }.await()\n" +
                "    \n" +
                "}\n" +
                "\n" +
                "print(\"CHECK!\")\n" +
                "\n" +
                "if (text.await() == \"Hello world from main thread!\") {\n" +
                "    print(text.await())\n" +
                "}\n" +
                "\n" +
                "print(\"COMPLETE!\")";


        ContanEngine contanEngine = new ContanEngine();
        Parser parser = new Parser("test", contanEngine, test);

        try {
            ContanModule contanModule = parser.compile();
            contanModule.eval();
            
        } catch (ContanParseException | ExecutionException | InterruptedException e) {
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
