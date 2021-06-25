package yanwittmann.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import yanwittmann.utils.ArgParser.Argument;

import static yanwittmann.utils.ArgParser.Argument.ParameterType.*;
import static yanwittmann.utils.ArgParser.Results;

public class ArgParserTest {

    @Test
    public void argParserTest() {

    }

    @Test
    public void syntaxTest() {
        ArgParser argParser1 = new ArgParser();
        argParser1.setPrefix("cmd");
        argParser1.setPrefixRequired(false);
        argParser1.setFailOnDoubleArguments(false);
        argParser1.addArgument(new Argument().setRequired(false).addIdentifier("-arr").setParameterType(STRING_ARRAY).setParameterRequired(false).setParameterName("array").setDefaultParameterValue(new String[]{"Well", "Hello"}));
        argParser1.addArgument(new Argument().setRequired(true).addIdentifier("-c", "--count").setParameterType(INTEGER).setParameterRequired(false).setDescription("A number").addValidParameterValue("12", "-43", "726").setParameterName("count"));
        argParser1.addArgument(new Argument().setRequired(true).addIdentifier("-a", "--argument").setParameterType(STRING).setParameterRequired(true).setParameterName("arg"));
        argParser1.addArgument(new Argument().setRequired(false).addIdentifier("-v", "--verbose").setParameterType(BOOLEAN).setParameterRequired(false).setParameterName("verbose").setDefaultParameterValue(false));
        Assertions.assertEquals("[cmd] -a,--argument <arg:string> -c,--count [count:12|726|-43] [-arr [array:string_array]] [-v,--verbose [verbose:boolean]]", argParser1.commandSyntax());

        ArgParser argParser2 = new ArgParser();
        argParser2.setPrefixRequired(true);
        Assertions.assertTrue(argParser2.addArgument(new Argument().addIdentifier("-t", "test").setParameterName("this is a very test value").setRequired(false).setParameterType(ANY)));
        Assertions.assertFalse(argParser2.addArgument(new Argument().addIdentifier("-t", "test").addIdentifier("hmm").setParameterType(DOUBLE)));
        Assertions.assertTrue(argParser2.addArgument(new Argument().setParameterName("i am a parameter").setRequired(true).setParameterRequired(true).addIdentifier("hmm").setParameterType(DOUBLE)));
        Assertions.assertEquals("hmm <i am a parameter:double> [-t,test [this is a very test value:any]]", argParser2.commandSyntax());

        Assertions.assertFalse(argParser2.matches("-t 0.4 test"));
        Results results2 = argParser2.parse("-t string array value hmm 1.5");
        Assertions.assertArrayEquals(results2.getStringArray("test"), new String[]{"string", "array", "value"});
        Assertions.assertEquals(results2.getDouble("hmm"), 1.5);

        ArgParser argParser3 = new ArgParser();
        argParser3.setPrefix("server");
        argParser3.setPrefixRequired(true);
        argParser3.addArgument(new Argument().addIdentifier("-h", "--help").setRequired(false).setDescription("Prints a help message"));
        argParser3.addArgument(new Argument().addIdentifier("-l", "--login").setRequired(false).setParameterName("username").setParameterType(STRING).setParameterRequired(true).setDescription("Will ask you for the password, then log you into your account"));
        Assertions.assertEquals("Usage: server [-h,--help] [-l,--login <username:string>]\n" +
                                "  [-h,--help]\n" +
                                "     Prints a help message\n" +
                                "  [-l,--login <username:string>]\n" +
                                "     Will ask you for the password, then log you into your account", argParser3.toString());
        Assertions.assertTrue(argParser3.parse("server").isAbsent("-h"));
        Assertions.assertTrue(argParser3.parse("server --help").isPresent("-h"));
        Assertions.assertNull(argParser3.parse("server -h").get("-h"));
        Assertions.assertTrue(argParser3.parse("server -l user").isPresent("-l"));
        Assertions.assertTrue(argParser3.parse("server -l user").isAbsent("-h"));

    }
}
