package yanwittmann.notification;

import org.junit.jupiter.api.Test;
import yanwittmann.utils.GeneralUtils;
import yanwittmann.utils.PerformanceTest;

public class NotificationTest {

    @Test
    public void generateManyTest() {
        String[] options = {"heel", "page", "service", "awful", "ban", "default", "shatter", "candle", "comprehensive", "offset", "classroom", "legend", "simplicity", "prevent", "tablet", "welcome", "carriage", "village", "fist", "vat", "know"};
        new PerformanceTest(100) {
            @Override
            public void perform() {
                new BlurNotification(options[GeneralUtils.randomNumber(0, options.length - 1)]);
            }
        }.start().removeFirst().print();
    }
}
