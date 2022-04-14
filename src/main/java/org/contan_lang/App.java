package org.contan_lang;

import org.contan_lang.environment.Environment;
import org.contan_lang.environment.EnvironmentVariable;
import org.contan_lang.evaluators.*;
import org.contan_lang.operators.Operator;
import org.contan_lang.operators.primitives.*;
import org.contan_lang.syntax.Identifier;
import org.contan_lang.syntax.Lexer;
import org.contan_lang.syntax.exception.UnexpectedSyntaxException;
import org.contan_lang.syntax.parser.Parser;
import org.contan_lang.syntax.parser.ScriptTree;
import org.contan_lang.syntax.tokens.IdentifierToken;
import org.contan_lang.syntax.tokens.Token;
import org.contan_lang.variables.primitive.ContanInteger;
import org.contan_lang.variables.primitive.ContanString;

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
        
        
        String test = "print(check(15, 5))\n" +
                "print(check(200, 5))\n" +
                "\n" +
                "\n" +
                "function check(i, j) {\n" +
                "\n" +
                "    if (i + j == 20) {\n" +
                "        return \"i + j is 20!!!\"\n" +
                "    }\n" +
                "\n" +
                "    return \"i + j is \" + (i + j)\n" +
                "}";

        ContanEngine contanEngine = new ContanEngine();
        Parser parser = new Parser(contanEngine);
        try {
            Environment global = new Environment(null);
            ScriptTree scriptTree = parser.parse("test", test);
            scriptTree.getGlobalEvaluator().eval(global);
            
        } catch (UnexpectedSyntaxException e) {
            e.printStackTrace();
        }
    }
}
