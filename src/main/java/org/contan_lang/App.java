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
        
        
        
        String test = "data test1 = i => i\n" +
                "\n" +
                "data test2 = () => 40\n" +
                "\n" +
                "print(test1(50) * test2())";

        
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
