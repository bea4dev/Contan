package org.contan_lang;

import org.contan_lang.environment.Environment;
import org.contan_lang.syntax.exception.ContanParseException;
import org.contan_lang.syntax.parser.Parser;
import org.contan_lang.syntax.parser.ContanModule;

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
        
        
        
        String test = "import org.contan_lang.TestClass\n" +
                "\n" +
                "print(new TestClass(\"TEST!!\").test.text)";

        ContanEngine contanEngine = new ContanEngine();
        Parser parser = new Parser(contanEngine);
        try {
            Environment global = new Environment(null);
            ContanModule contanModule = parser.parse("test", test);
            contanModule.getGlobalEvaluator().eval(global);
            
        } catch (ContanParseException e) {
            e.printStackTrace();
        }
    }
}
