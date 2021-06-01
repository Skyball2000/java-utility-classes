package yanwittmann.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import yanwittmann.log.Log;
import yanwittmann.math.MathEval;

public class MathEvalTest {

    @Test
    public void mathEvalTest() throws MathEval.MathEvalException {
        String expression = "-3 * (-45 + - 15 % 10) -3 pow 4 + (12/3) sqrt 2";
        Log.info(MathEval.prepareExpression(expression));
        Log.info(MathEval.evaluate(expression));
        Assertions.assertEquals("( ( ( ⁻3 * ( ( ⁻45 + ( ⁻15 % 10 ) ) ) ) - ( 3 ^ 4 ) ) + ( ( 12 / 3 ) √ 2 ) )", MathEval.prepareExpression(expression));
        Assertions.assertEquals(71, Math.round(MathEval.evaluate(expression)));
    }
}
