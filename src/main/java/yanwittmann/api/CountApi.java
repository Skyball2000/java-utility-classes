package yanwittmann.api;

import yanwittmann.utils.FileUtils;
import yanwittmann.utils.GeneralUtils;

import java.io.IOException;

/**
 * Use this class to access my database which serves as an online counter.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 * @author Yan Wittmann
 */
public class CountApi {
    private final static String BASE_URL = "http://yanwittmann.de/projects/countapi";
    public final static String ERROR_STRING = "ERROR";
    public final static int DEFAULT_RETURN_VALUE = -1;

    private final String namespace;
    private final String key;

    public CountApi(String namespace, String key) {
        this.namespace = namespace;
        this.key = key;
    }

    public int get() {
        try {
            return Integer.parseInt(performCall(BASE_URL + "/get.php?key=" + key + "&namespace=" + namespace));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DEFAULT_RETURN_VALUE;
    }

    public int hit() {
        try {
            return Integer.parseInt(performCall(BASE_URL + "/hit.php?key=" + key + "&namespace=" + namespace));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DEFAULT_RETURN_VALUE;
    }

    public boolean set(int value) {
        return !performCall(BASE_URL + "/set.php?key=" + key + "&namespace=" + namespace + "&value=" + value).contains(ERROR_STRING);
    }

    public boolean create(boolean allowSet) {
        return !performCall(BASE_URL + "/create.php?set=" + (allowSet ? 1 : 0) + "&key=" + key + "&namespace=" + namespace).contains(ERROR_STRING);
    }

    public String getKey() {
        return key;
    }

    public String getNamespace() {
        return namespace;
    }

    private String performCall(String url) {
        try {
            String[] response = FileUtils.getResponseURL(url);
            if (response.length != 0)
                return GeneralUtils.makeOneLine(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ERROR_STRING;
    }
}
