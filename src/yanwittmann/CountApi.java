package yanwittmann;

/**
 * Use this class to access the <a href="https://countapi.xyz/">https://countapi.xyz/</a> which serves as an online counter.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 */
public class CountApi {
    private final static String BASE_URL = "https://api.countapi.xyz";
    public final static int DEFAULT_RETURN_VALUE = -1;

    private final String namespace;
    private final String key;

    public CountApi(String namespace, String key) {
        this.namespace = namespace;
        this.key = key;
    }

    public Integer get() {
        try {
            String response = performCall(BASE_URL + "/get/" + getKeyNamespace());
            if (response.matches("\\{\"value\":(.+)}")) {
                String extractedValue = response.replaceAll("\\{\"value\":(.+)}", "$1");
                return Integer.parseInt(extractedValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DEFAULT_RETURN_VALUE;
    }

    public Integer hit() {
        try {
            String response = performCall(BASE_URL + "/hit/" + getKeyNamespace());
            if (response.matches("\\{\"value\":(.+)}")) {
                String extractedValue = response.replaceAll("\\{\"value\":(.+)}", "$1");
                return Integer.parseInt(extractedValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DEFAULT_RETURN_VALUE;
    }

    public boolean set(int value) {
        return !performCall(BASE_URL + "/set/" + getKeyNamespace() + "?value=" + value).contains("null");
    }

    public boolean create(boolean allowSet) {
        return !performCall(BASE_URL + "/create?enable_reset=" + (allowSet ? 1 : 0) + "&key=" + key + "&namespace=" + namespace).contains("null");
    }

    public String info() {
        return performCall(BASE_URL + "/info/" + getKeyNamespace());
    }

    private String keyNamespace = null;

    public String getKeyNamespace() {
        if (keyNamespace == null) {
            if (key.equals(""))
                keyNamespace = namespace;
            else
                keyNamespace = namespace + "/" + key;
        }
        return keyNamespace;
    }

    public String getKey() {
        return key;
    }

    public String getNamespace() {
        return namespace;
    }

    private String performCall(String url) {
        String[] response = FileUtils.getResponseURL(url);
        if (response != null && response.length != 0)
            return GeneralUtils.makeOneLine(response);
        return "null";
    }
}
