package yanwittmann.utils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class can be given a number of arguments and then parse a given args string.<br>
 * Every argument can have the following data:
 * <ul>
 *     <li><code>required</code> (Boolean)</li>
 *     <li><code>identifiers</code> (Set)</li>
 *     <li><code>description</code> (String)</li>
 *     <li><code>parameterType</code> (<code>Argument.ParameterType</code>: String, Integer, Long, Float, Double, Boolean, String[], Any, None)</li>
 *     <li><code>parameterName</code> (String)</li>
 *     <li><code>defaultParameterValue</code> (String, Integer, Long, Float, Double, Boolean, String[])</li>
 *     <li><code>validParameterValues</code> (Set)</li>
 *     <li><code>parameterRequired</code> (Boolean)</li>
 * </ul>
 * When parsing the args string, the syntax is checked first. If any invalid values are found, an exception with a
 * fitting error message is thrown.<br><br>
 * <p>
 * If the args string was parsable, a <code>Results</code> object is returned containing the individual argument data.
 *
 * <br><br>This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 *
 * @author Yan Wittmann
 */
public class ArgParser implements Iterable<ArgParser.Argument> {

    private final Set<Argument> arguments = new HashSet<>();
    private String prefix = null;
    private boolean prefixRequired = false;
    private boolean failOnDoubleArguments = true;

    public ArgParser() {
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setPrefixRequired(boolean prefixRequired) {
        this.prefixRequired = prefixRequired;
    }

    public void setFailOnDoubleArguments(boolean failOnDoubleArguments) {
        this.failOnDoubleArguments = failOnDoubleArguments;
    }

    public boolean addArgument(Argument argument) {
        for (Argument checkArg : arguments)
            for (String identifier : argument.identifiers)
                if (checkArg.identifiers.contains(identifier))
                    return false;
        return arguments.add(argument);
    }

    public boolean removeArgument(Argument argument) {
        return arguments.remove(argument);
    }

    public boolean matches(String args) {
        try {
            parse(args);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean matches(String[] args) {
        try {
            parse(args);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Results parse(String[] args) {
        return parse(String.join(" ", args));
    }

    public Results parse(String args) {
        if (prefix != null && prefix.length() > 0) {
            if (prefixRequired && !args.matches("^" + prefix + ".*"))
                throw new ArgParserException("Missing prefix '" + prefix + "' for input: " + args);
            args = args.replaceAll("^" + prefix, "");
        }
        return parseInputStringArray(args.trim().split(" "));
    }

    private Results parseInputStringArray(String[] args) {
        List<Argument> remainingArguments = new ArrayList<>(arguments);
        List<Result> results = new ArrayList<>();
        int currentArgumentIndex = 0;

        // find arguments
        for (int i = 0; i < args.length && remainingArguments.size() > 0; i++) {

            int finalI = i;
            Argument currentArgument = remainingArguments.stream().filter(arg -> arg.containsIdentifier(args[finalI])).findFirst().orElse(null);

            if (currentArgument != null) {
                remainingArguments.remove(currentArgument);

                StringBuilder parameterBuilder = new StringBuilder();
                while (true) {
                    i++;
                    if (i < args.length) {
                        int finalI2 = i;
                        if (remainingArguments.stream().anyMatch(arg -> arg.containsIdentifier(args[finalI2]))) {
                            i--;
                            break;
                        } else {
                            if (arguments.stream().filter(arg -> arg.containsIdentifier(args[finalI2])).findFirst().orElse(null) != null) {
                                if (failOnDoubleArguments) {
                                    String highlighted = String.join(" ", args);
                                    for (String identifier : currentArgument.identifiers)
                                        highlighted = highlightIdentifier(highlighted, identifier);
                                    throw new ArgParserException("Argument appears twice in args string\n" + currentArgument + "\n" + highlighted);
                                } else {
                                    i--;
                                    break;
                                }
                            }
                        }
                        if (parameterBuilder.length() > 0) parameterBuilder.append(" ");
                        parameterBuilder.append(args[finalI2]);
                    } else break;
                }

                String parameter = parameterBuilder.toString();
                if (parameter.length() > 0) {
                    if (!currentArgument.hasParameterCorrectType(parameter))
                        throw new ArgParserException("Argument parameter is of wrong type\n" + currentArgument + "\nGot: " + parameter + "\nExpected: " + currentArgument.parameterType.toString().toLowerCase());
                    if (!currentArgument.parameterIsInValidValues(parameter))
                        throw new ArgParserException("Argument parameter is not in allowed values set\n" + currentArgument + "\nGot: " + parameter + "\nExpected: " + String.join(",", currentArgument.validParameterValues));
                } else {
                    if (currentArgument.parameterRequired)
                        throw new ArgParserException("Missing argument parameter\n" + currentArgument + "\nExpected: " + currentArgument.parameterType.toString().toLowerCase());
                    parameter = null;
                }
                if (parameter == null) {
                    if (currentArgument.defaultParameterValue != null)
                        results.add(new Result(currentArgument, currentArgumentIndex, currentArgument.defaultParameterValue));
                } else {
                    results.add(new Result(currentArgument, currentArgumentIndex, parameter));
                }
                currentArgumentIndex++;
            }
        }

        // check if there are still any required arguments
        for (Argument currentArgument : remainingArguments)
            if (currentArgument.required)
                throw new ArgParserException("Invalid argument syntax for '" + currentArgument + "' in '" + String.join(" ", args) + "'");

        // set default values
        for (Argument currentArgument : remainingArguments)
            if (currentArgument.defaultParameterValue != null) {
                results.add(new Result(currentArgument, currentArgumentIndex, currentArgument.defaultParameterValue));
                currentArgumentIndex++;
            }

        return new Results(results);
    }

    public String commandSyntax() {
        List<Argument> sortedArguments = arguments.stream().sorted().collect(Collectors.toList());

        StringBuilder syntax = new StringBuilder();
        if (prefix != null && prefix.length() > 0)
            syntax.append(prefixRequired ? "" : "[").append(prefix).append(prefixRequired ? "" : "]");
        for (Argument argument : sortedArguments) syntax.append(syntax.length() == 0 ? "" : " ").append(argument);

        return syntax.toString();
    }

    @Override
    public String toString() {
        List<Argument> sortedArguments = arguments.stream().sorted().collect(Collectors.toList());

        StringBuilder header = new StringBuilder();
        if (prefix != null && prefix.length() > 0)
            header.append("Usage '").append(prefix).append("': ").append(prefixRequired ? "" : "[").append(prefix).append(prefixRequired ? "" : "]");
        else header.append("Usage:");
        for (Argument argument : sortedArguments) header.append(" ").append(argument);

        StringBuilder args = new StringBuilder();
        for (Argument argument : sortedArguments)
            if (argument.description != null && argument.description.length() > 0) {
                if (args.length() > 0) args.append("\n");
                args.append("  ").append(argument).append("\n");
                args.append("     ").append(argument.description);
            }

        return header + "\n" + args;
    }

    public Stream<Argument> stream() {
        return arguments.stream();
    }

    @Override
    public Iterator<Argument> iterator() {
        return arguments.iterator();
    }

    public static class Results implements Iterable<Result> {
        private final List<Result> results;

        public Results(List<Result> results) {
            this.results = results;
        }

        public List<Result> getResults() {
            return results;
        }

        public Result getResult(String identifier) {
            return results.stream().filter(result -> result.argument.identifiers.contains(identifier)).findFirst().orElse(null);
        }

        public String getParameter(String identifier) {
            return results.stream().filter(result -> result.argument.identifiers.contains(identifier)).findFirst().map(Result::getParameter).orElse(null);
        }

        public String get(String identifier) {
            return results.stream().filter(result -> result.argument.identifiers.contains(identifier)).findFirst().map(Result::getParameter).orElse(null);
        }

        public int getIndex(String identifier) {
            return results.stream().filter(result -> result.argument.identifiers.contains(identifier)).findFirst().map(Result::getIndex).orElse(-1);
        }

        public Argument getArgument(String identifier) {
            return results.stream().filter(result -> result.argument.identifiers.contains(identifier)).findFirst().map(Result::getArgument).orElse(null);
        }

        public String getParameter(Argument argument) {
            return results.stream().filter(result -> result.argument == argument).findFirst().map(Result::getParameter).orElse(null);
        }

        public int getIndex(Argument argument) {
            return results.stream().filter(result -> result.argument == argument).findFirst().map(Result::getIndex).orElse(-1);
        }

        public boolean isPresent(String identifier) {
            return results.stream().anyMatch(result -> result.argument.identifiers.contains(identifier));
        }

        public String getString(String identifier) {
            return results.stream().filter(result -> result.argument.identifiers.contains(identifier)).findFirst().map(Result::getString).orElse(null);
        }

        public String[] getStringArray(String identifier) {
            return results.stream().filter(result -> result.argument.identifiers.contains(identifier)).findFirst().map(Result::getStringArray).orElse(null);
        }

        public boolean getBoolean(String identifier) {
            return results.stream().filter(result -> result.argument.identifiers.contains(identifier)).findFirst().map(Result::getBoolean).orElse(false);
        }

        public float getFloat(String identifier) {
            return results.stream().filter(result -> result.argument.identifiers.contains(identifier)).findFirst().map(Result::getFloat).orElse(-1f);
        }

        public double getDouble(String identifier) {
            return results.stream().filter(result -> result.argument.identifiers.contains(identifier)).findFirst().map(Result::getDouble).orElse(-1d);
        }

        public long getLong(String identifier) {
            return results.stream().filter(result -> result.argument.identifiers.contains(identifier)).findFirst().map(Result::getLong).orElse(-1L);
        }

        public int getInt(String identifier) {
            return results.stream().filter(result -> result.argument.identifiers.contains(identifier)).findFirst().map(Result::getInt).orElse(-1);
        }

        public Stream<Result> stream() {
            return results.stream();
        }

        @Override
        public Iterator<Result> iterator() {
            return results.iterator();
        }
    }

    public static class Result {

        private final Argument argument;
        private final int index;
        private final String parameter;

        private Result(Argument argument, int index, String parameter) {
            this.argument = argument;
            this.index = index;
            this.parameter = parameter;
        }

        public Argument getArgument() {
            return argument;
        }

        public int getIndex() {
            return index;
        }

        public String getParameter() {
            return parameter;
        }

        public String getString() {
            return parameter;
        }

        public String[] getStringArray() {
            return parameter.split(" ");
        }

        public boolean getBoolean() {
            return parameter.toLowerCase().matches("(true|1)");
        }

        public float getFloat() {
            return Float.parseFloat(parameter);
        }

        public double getDouble() {
            return Double.parseDouble(parameter);
        }

        public long getLong() {
            return Long.parseLong(parameter);
        }

        public int getInt() {
            return Integer.parseInt(parameter);
        }

    }

    public static class Argument implements Comparable<Argument> {

        private ParameterType parameterType = ParameterType.NONE;
        private String parameterName = null;
        private String defaultParameterValue = null;
        private final Set<String> validParameterValues = new HashSet<>();
        private boolean parameterRequired = false;
        private boolean required = false;
        private final Set<String> identifiers = new HashSet<>();
        private String description = null;

        public Argument() {
        }

        public Argument setParameterType(ParameterType parameterType) {
            this.parameterType = parameterType;
            return this;
        }

        public Argument setParameterRequired(boolean parameterRequired) {
            this.parameterRequired = parameterRequired;
            return this;
        }

        public Argument setRequired(boolean required) {
            this.required = required;
            return this;
        }

        public Argument setDescription(String description) {
            this.description = description;
            return this;
        }

        public Argument setParameterName(String parameterName) {
            this.parameterName = parameterName;
            return this;
        }

        public Argument setDefaultParameterValue(String defaultParameterValue) {
            this.defaultParameterValue = defaultParameterValue;
            return this;
        }

        public Argument setDefaultParameterValue(boolean defaultParameterValue) {
            this.defaultParameterValue = "" + defaultParameterValue;
            return this;
        }

        public Argument setDefaultParameterValue(double defaultParameterValue) {
            this.defaultParameterValue = "" + defaultParameterValue;
            return this;
        }

        public Argument setDefaultParameterValue(float defaultParameterValue) {
            this.defaultParameterValue = "" + defaultParameterValue;
            return this;
        }

        public Argument setDefaultParameterValue(int defaultParameterValue) {
            this.defaultParameterValue = "" + defaultParameterValue;
            return this;
        }

        public Argument setDefaultParameterValue(long defaultParameterValue) {
            this.defaultParameterValue = "" + defaultParameterValue;
            return this;
        }

        public Argument setDefaultParameterValue(String[] defaultParameterValue) {
            this.defaultParameterValue = String.join(" ", defaultParameterValue);
            return this;
        }

        public Argument addIdentifier(String... identifier) {
            identifiers.addAll(Arrays.stream(identifier).collect(Collectors.toSet()));
            return this;
        }

        public Argument removeIdentifier(String identifier) {
            identifiers.remove(identifier);
            return this;
        }

        public Argument addValidParameterValue(String... value) {
            validParameterValues.addAll(Arrays.stream(value).collect(Collectors.toSet()));
            return this;
        }

        public Argument removeValidParameterValue(String value) {
            validParameterValues.remove(value);
            return this;
        }

        public boolean isRequired() {
            return required;
        }

        public String getDescription() {
            return description;
        }

        public Set<String> getIdentifiers() {
            return identifiers;
        }

        public ParameterType getParameterType() {
            return parameterType;
        }

        public String getDefaultParameterValue() {
            return defaultParameterValue;
        }

        public String getParameterName() {
            return parameterName;
        }

        public boolean containsIdentifier(String identifier) {
            return identifiers.contains(identifier);
        }

        public boolean hasParameterCorrectType(String parameter) {
            switch (parameterType) {
                case STRING:
                case STRING_ARRAY:
                case ANY:
                    return parameter != null;
                case INTEGER:
                    return parameter.matches("-?\\d{1,10}");
                case LONG:
                    return parameter.matches("-?\\d{1,19}");
                case FLOAT:
                case DOUBLE:
                    return parameter.matches("-?[0-9]*\\.?[0-9]+");
                case BOOLEAN:
                    return parameter.toLowerCase().matches("(true|false|1|0)");
            }
            return false;
        }

        public boolean parameterIsInValidValues(String parameter) {
            return validParameterValues.size() == 0 || validParameterValues.contains(parameter);
        }

        @Override
        public String toString() {
            StringBuilder arg = new StringBuilder();
            arg.append(required ? "" : "[");

            arg.append(identifiers.stream().sorted(Comparator.comparing(o -> o.replace("-", ""))).collect(Collectors.joining(",")));
            if (parameterType != ParameterType.NONE) {
                arg.append(parameterRequired ? " <" : " [");
                if (parameterName != null) arg.append(parameterName).append(":");
                if (validParameterValues.size() > 0)
                    arg.append(String.join("|", validParameterValues));
                else
                    arg.append(parameterType.toString().toLowerCase());
                arg.append(parameterRequired ? ">" : "]");
            }

            arg.append(required ? "" : "]");
            return arg.toString();
        }

        @Override
        public int compareTo(Argument o) {
            int required = Boolean.compare(o.required, this.required);
            if (required == 0 && identifiers.size() > 0 && o.identifiers.size() > 0)
                return identifiers.stream().findAny().orElse("z").replace("-", "").compareTo(o.identifiers.stream().findAny().orElse("z").replace("-", ""));
            return required;
        }

        public enum ParameterType {
            STRING,
            STRING_ARRAY,
            INTEGER,
            LONG,
            FLOAT,
            DOUBLE,
            BOOLEAN,
            ANY,
            NONE
        }
    }

    private static class ArgParserException extends IllegalArgumentException {
        public ArgParserException(String message) {
            super(message);
        }
    }

    private static String highlightIdentifier(String str, String highlight) {
        return str.replaceAll("(^| )" + highlight + "($| )", " --> " + highlight + " <-- ");
    }
}
