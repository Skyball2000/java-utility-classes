package yanwittmann.api;

import org.junit.jupiter.api.Test;
import yanwittmann.utils.Log;
import yanwittmann.utils.PerformanceTest;

import java.util.Map;

public class GoogleTranslateTest {

    @Test
    public void translateTest() {
        GoogleTranslate translate = new GoogleTranslate();
        translate.setLanguages(GoogleTranslate.German, GoogleTranslate.French);

        System.out.println(new PerformanceTest(3) {
            @Override
            public void perform() {
                translate.addRequest("Eine frage", "Hallo wie geht es dir?");
                translate.addRequest(1, "Dies ist ein zweiter Text");
                translate.addRequest("Was esse ich gerne?", "Ich esse gerne Eis am Strand.");
                for (Map.Entry<Object, String> entry : translate.performRequests().entrySet())
                    Log.info(entry.getKey() + ": " + entry.getValue());
            }
        }.preheat(2).start().toString());
    }
}
