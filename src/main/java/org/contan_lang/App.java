package org.contan_lang;

import org.contan_lang.environment.Environment;
import org.contan_lang.syntax.Lexer;
import org.contan_lang.syntax.exception.ContanParseException;
import org.contan_lang.syntax.parser.ContanModule;
import org.contan_lang.syntax.parser.Parser;

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
        
        
        
        String test = "data b = 20\n" +
                "data a = ((20 + 3) *b) + b\n" +
                "print(((20 + 3) * b) + b)";


        ContanEngine contanEngine = new ContanEngine();
        Parser parser = new Parser("test", contanEngine, test);

        try {
            ContanModule contanModule = parser.compile();
            System.out.println("RUN!");
            contanModule.getGlobalEvaluator().eval(contanModule.getModuleEnvironment());
            
        } catch (ContanParseException e) {
            e.printStackTrace();
        }
    }
}
