package yanwittmann.api;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import yanwittmann.file.File;
import yanwittmann.log.Log;

import java.io.IOException;
import java.net.URL;

public class ApiToolsTest {

    private static ApiTools apiTools;

    @BeforeAll
    public static void beforeAll() {
        apiTools = new ApiTools(new File("res/apifiles"));
    }

    @Test
    public void getDataTest() throws IOException {
        new File(apiTools.get(new URL("https://www.w3.org/TR/PNG/iso_8859-1.txt"), ApiTools.FORCE_UPDATE)).readToArrayList().forEach(Log::info);
        new File(apiTools.get("https://github.com/Skyball2000/java-utility-classes", 3 * 1000)).readToArrayList().forEach(Log::info);
    }

    @Test
    public void clearDataTest() {
        apiTools.clearStorageDirectory();
    }
}
