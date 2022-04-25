package org.contan_lang;

import org.contan_lang.environment.Environment;
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
        
        
        
        String test = "data a = \"test\"\n" +
                "print(a)\n";

        ContanEngine contanEngine = new ContanEngine();
        Parser parser = new Parser("test", contanEngine, test);

        try {
            ContanModule contanModule = parser.compile();
            contanModule.getGlobalEvaluator().eval(contanModule.getModuleEnvironment());
            
        } catch (ContanParseException e) {
            e.printStackTrace();
        }
    }
}
