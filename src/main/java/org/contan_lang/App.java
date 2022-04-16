package org.contan_lang;

import org.contan_lang.environment.Environment;
import org.contan_lang.environment.EnvironmentVariable;
import org.contan_lang.evaluators.*;
import org.contan_lang.operators.Operator;
import org.contan_lang.operators.primitives.*;
import org.contan_lang.syntax.Identifier;
import org.contan_lang.syntax.Lexer;
import org.contan_lang.syntax.exception.ContanParseException;
import org.contan_lang.syntax.exception.UnexpectedSyntaxException;
import org.contan_lang.syntax.parser.Parser;
import org.contan_lang.syntax.parser.ScriptTree;
import org.contan_lang.syntax.tokens.IdentifierToken;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.primitive.ContanInteger;
import org.contan_lang.variables.primitive.ContanString;

import java.lang.reflect.Method;
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
        
        
        
        String test = "import org.contan_lang.ContanEngine\n" +
                "import org.contan_lang.syntax.parser.Parser\n" +
                "import org.contan_lang.environment.Environment\n" +
                "\n" +
                "data text = \"print(\\\"Hello, world!\\\")\"\n" +
                "\n" +
                "data contanEngine = new ContanEngine()\n" +
                "data parser = new Parser(contanEngine)\n" +
                "\n" +
                "data global = new Environment(null)\n" +
                "data scriptTree = parser.parse(\"test\", text)\n" +
                "scriptTree.getGlobalEvaluator().eval(global)";

        ContanEngine contanEngine = new ContanEngine();
        Parser parser = new Parser(contanEngine);
        try {
            Environment global = new Environment(null);
            ScriptTree scriptTree = parser.parse("test", test);
            scriptTree.getGlobalEvaluator().eval(global);
            
        } catch (ContanParseException e) {
            e.printStackTrace();
        }
    }
}
