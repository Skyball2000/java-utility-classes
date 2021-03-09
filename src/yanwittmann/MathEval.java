package yanwittmann;

import java.util.ArrayList;

/**
 * Use this class to evaluate mathematical expressions given as strings.<br>
 * Class written by <a href="http://yanwittmann.de/site/">Yan Wittmann</a>.
 */
public class MathEval {

    /**
     * Returns the result of the expression as a double value.<br>
     * Allowed operands in their processing order: <i>( ) ^/pow √/sqrt * / % + -</i><br>
     * An example that uses all features:<br>
     * <i>-3 * (-45 + - 15 % 10) - 3 pow 4 + (12/3) sqrt 2</i><br>
     * returns: <i>71.00000000000723</i>
     */
    public static double evaluate(String expression) throws MathEvalException {
        if (checkValidBrackets(expression)) {
            expression = prepareExpression(expression);
            if (checkValidOperations(expression)) {
                return evaluatePreparedExpression(expression);
            } else {
                throw new InvalidOperationException("Unexpected operand in expression: " + expression);
            }
        } else {
            throw new UnbalancedBracketsException("Unbalanced brackets in expression: " + expression);
        }
    }

    private static double evaluatePreparedExpression(String expression) throws MathEvalException {
        int counter = 0;
        boolean foundOperation;

        do {
            String part = expression.replaceAll(".*(\\( [^()]+ \\)).*", "$1");
            foundOperation = part.length() != expression.length();
            if (foundOperation) {
                expression = expression.replace(part, "" + solvePart(part));
            }
            counter++;
            if (counter > 1000) throw new RecursiveException("Unable to solve expression: " + expression);
        } while (foundOperation);

        try {
            return solvePart(expression);
        } catch (InvalidOperationException e) {
            return toDouble(expression.replaceAll("[() ]", ""));
        }
    }

    private static double solvePart(String expression) throws MathEvalException {
        double result;
        expression = expression.replace("(", "").replace(")", "").trim();
        if(expression.matches(NUMBER) || expression.matches(NUMBER.replace("⁻", "-"))) return toDouble(expression);
        String[] parameters = expression.split(" ");
        if (parameters.length != 3)
            throw new InvalidOperationException("Invalid part of expression: " + expression);
        double value1 = toDouble(parameters[0]);
        double value2 = toDouble(parameters[2]);
        result = switch (parameters[1]) {
            case "+" -> value1 + value2;
            case "-" -> value1 - value2;
            case "*" -> value1 * value2;
            case "/" -> value1 / value2;
            case "^" -> Math.pow(value1, value2);
            case "√" -> nthRoot(value1, value2);
            case "%" -> value1 % value2;
            default -> throw new InvalidOperationException("Invalid operation: " + parameters[1]);
        };
        return result;
    }

    /**
     * Returns a normalized version of the given expression.<br>
     * An example that uses all features:<br>
     * <i>-3 * (-45 + - 15 % 10) - 3 pow 4 + (12/3) sqrt 2</i><br>
     * returns: <i>( ( ( ⁻3 * ( ( ⁻45 + ( ⁻15 % 10 ) ) ) ) - ( 3 ^ 4 ) ) + ( ( 12 / 3 ) √ 2 ) )</i>
     */
    public static String prepareExpression(String expression) throws MathEvalException {
        expression = normalize(expression);
        if (!checkValidCharacters(expression))
            throw new IllegalCharacterException("Illegal character(s) in expression: " + expression);
        return normalize(placeBrackets(expression));
    }

    private final static String MATH_OPERATORS = "[-+*/^√%()]";
    private final static String NUMBER = "(?:⁻)?\\d+(?:\\.?\\d+)?";

    private static String normalize(String expression) {
        return expression.toLowerCase()
                .replace("pow", "^")
                .replaceAll("sqrt(?! ?\\d)", "√ 2")
                .replaceAll("sqrt( ?\\d)", "√ $1")
                .replaceAll("(" +NUMBER + ") +(" +NUMBER + ")", "$1 * $2")
                .replaceAll("(" + MATH_OPERATORS.replace(")", "") + ") *- *(" + NUMBER + ")", "$1 ⁻$2")
                .replaceAll("^ *- *(" + NUMBER + ")", "⁻$1")
                .replaceAll("(" + MATH_OPERATORS + ") *(" + NUMBER + ")", "$1 $2")
                .replaceAll("(" + NUMBER + ") *(" + MATH_OPERATORS + ")", "$1 $2")
                .replaceAll("(" + MATH_OPERATORS + ") *(" + MATH_OPERATORS + ")", "$1 $2")
                .replaceAll("(" + NUMBER + ") *\\(", "$1 * (")
                .replaceAll("\\) *(" + NUMBER + ")", ") * $1");
    }

    private static String placeBrackets(String expression) throws MathEvalException {
        ArrayList<String> brackets = new ArrayList<>();
        boolean addedBrackets;

        do {
            if (brackets.size() > 1000)
                throw new RecursiveException("Cannot place brackets in expression: " + expression);

            //already existing brackets
            String foundBrackets = expression.replaceAll(".*(\\([^(]+\\)).*", "$1");
            addedBrackets = foundBrackets.length() != expression.length();
            if (addedBrackets) {
                //check if there are multiple operands in brackets
                if (foundBrackets.replaceAll(MATH_OPERATORS.replaceAll("[()]", ""), "").length() != foundBrackets.length() - 1) {
                    String extraBrackets = placeBrackets(foundBrackets);
                    expression = expression.replace(foundBrackets, extraBrackets);
                    foundBrackets = extraBrackets;
                }
                brackets.add(foundBrackets);
                expression = expression.replace(foundBrackets, "br_" + brackets.size() + "_");
                continue;
            }

            expression = " " + expression.trim() + " ";

            // ^ √
            foundBrackets = expression.replaceAll("[^^√]* ((?:br_\\d+_|" + NUMBER + ") [*^√] (?:" + NUMBER + "|br_\\d_)).*", "$1");
            addedBrackets = foundBrackets.length() != expression.length();
            if (addedBrackets) {
                brackets.add("( " + foundBrackets + " )");
                expression = expression.replace(foundBrackets, "br_" + brackets.size() + "_");
                continue;
            }

            // * /
            foundBrackets = expression.replaceAll("[^*/]* ((?:br_\\d+_|" + NUMBER + ") [*/] (?:" + NUMBER + "|br_\\d_)).*", "$1");
            addedBrackets = foundBrackets.length() != expression.length();
            if (addedBrackets) {
                brackets.add("( " + foundBrackets + " )");
                expression = expression.replace(foundBrackets, "br_" + brackets.size() + "_");
                continue;
            }

            // %
            foundBrackets = expression.replaceAll("[^%]* ((?:br_\\d+_|" + NUMBER + ") [%] (?:" + NUMBER + "|br_\\d_)).*", "$1");
            addedBrackets = foundBrackets.length() != expression.length();
            if (addedBrackets) {
                brackets.add("( " + foundBrackets + " )");
                expression = expression.replace(foundBrackets, "br_" + brackets.size() + "_");
            }

            // + -
            foundBrackets = expression.replaceAll("[^+-]* ((?:br_\\d+_|" + NUMBER + ") [+-] (?:" + NUMBER + "|br_\\d_)).*", "$1");
            addedBrackets = foundBrackets.length() != expression.length();
            if (addedBrackets) {
                brackets.add("( " + foundBrackets + " )");
                expression = expression.replace(foundBrackets, "br_" + brackets.size() + "_");
            }
        } while (addedBrackets);

        for (int i = brackets.size() - 1; i >= 0; i--) {
            expression = expression.replace("br_" + (i + 1) + "_", brackets.get(i));
        }

        return expression.trim();
    }

    //https://www.geeksforgeeks.org/n-th-root-number/
    static double nthRoot(double A, double N) {

        // intially guessing a random number between
        // 0 and 9
        double xPre = Math.random() % 10;

        // smaller eps, denotes more accuracy
        double eps = 0.001;

        // initializing difference between two
        // roots by INT_MAX
        double delX = 2147483647;

        // xK denotes current value of x
        double xK = 0.0;

        // loop untill we reach desired accuracy
        while (delX > eps) {
            // calculating current value from previous
            // value by newton's method
            xK = ((N - 1.0) * xPre + A / Math.pow(xPre, N - 1)) / N;
            delX = Math.abs(xK - xPre);
            xPre = xK;
        }

        return xK;
    }

    private static boolean checkValidBrackets(String expression) {
        int currentElevation = 0;
        for (char c : expression.toCharArray()) {
            if (c == '(') currentElevation++;
            else if (c == ')') currentElevation--;
            if (currentElevation < 0) return false;
        }
        return currentElevation == 0;
    }

    private static boolean checkValidOperations(String expression) {
        return !expression.matches(".*[-+*/^%√(] ?[-+*/^%√)].*");
    }

    private final static String[] ILLEGAL_CHARACTERS = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "_"};

    private static boolean checkValidCharacters(String expression) {
        for (String illegalCharacter : ILLEGAL_CHARACTERS) {
            if (expression.contains(illegalCharacter)) return false;
        }
        return true;
    }

    private static double toDouble(String d) {
        return Double.parseDouble(d.replace("⁻", "-"));
    }

    public static class UnbalancedBracketsException extends MathEvalException {
        public UnbalancedBracketsException(String message) {
            super(message);
        }
    }

    public static class InvalidOperationException extends MathEvalException {
        public InvalidOperationException(String message) {
            super(message);
        }
    }

    public static class IllegalCharacterException extends MathEvalException {
        public IllegalCharacterException(String message) {
            super(message);
        }
    }

    public static class RecursiveException extends MathEvalException {
        public RecursiveException(String message) {
            super(message);
        }
    }

    public static class MathEvalException extends Exception {
        public MathEvalException(String message) {
            super(message);
        }
    }
}