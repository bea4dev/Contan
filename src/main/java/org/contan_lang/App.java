package org.contan_lang;

import org.contan_lang.syntax.Lexer;
import org.contan_lang.syntax.exception.ContanParseException;
import org.contan_lang.syntax.parser.Parser;
import org.contan_lang.syntax.tokens.Token;

import java.util.List;

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
        
        
        
        String test = "invoke(test)\n" +
                "\n" +
                "function invoke(lambda){\n" +
                "    lambda()\n" +
                "}\n" +
                "\n" +
                "function test(){\n" +
                "    print(\"Hello World!!\")\n" +
                "}";

        
        ContanEngine contanEngine = new ContanEngine();
        Parser parser = new Parser("test", contanEngine, test);

        try {
            ContanModule contanModule = parser.compile();
            contanModule.eval();
            
        } catch (ContanParseException e) {
            e.printStackTrace();
        }
    }
}
