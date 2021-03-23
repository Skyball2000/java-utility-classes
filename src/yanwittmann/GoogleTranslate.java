package yanwittmann;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Use an object of this class to translate a text using the Google Translate API.<br>
 * Use bulk requests to speed up the translation process and to prevent error 429 (you can only perform a certain amount of requests in a time window).<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 */
public class GoogleTranslate {

    private String fromLanguage = "en";
    private String toLanguage = "de";

    public String translate(String text) {
        try {
            String[] response = FileUtils.getResponseURL(prepareTranslateURL(text));
            if (response.length != 0) {
                String asOneLine = GeneralUtils.makeOneLine(response);
                if (asOneLine.matches(TRANSLATE_RESULT_REGEX))
                    return asOneLine.replaceAll(TRANSLATE_RESULT_REGEX, "$1");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    public String translate(String from, String to, String text) {
        this.fromLanguage = from;
        this.toLanguage = to;
        return translate(text);
    }

    private HashMap<Object, String> bulkRequests;
    private HashMap<Integer, Object> bulkRequestsObjects;
    private boolean initializedBulkMap = false;

    public void addRequest(Object key, String text) {
        if (!initializedBulkMap) {
            bulkRequests = new HashMap<>();
            bulkRequestsObjects = new HashMap<>();
            initializedBulkMap = true;
        }
        bulkRequestsObjects.put(bulkRequests.size(), key);
        bulkRequests.put(key, text);
    }

    public HashMap<Object, String> performRequests() {
        if (!initializedBulkMap || bulkRequests.size() == 0) return bulkRequests;
        LineBuilder request = new LineBuilder();
        for (Map.Entry<Object, String> individualRequest : bulkRequests.entrySet()) {
            Integer key = bulkRequestsObjects.entrySet().stream().filter(connection -> connection.getValue().equals(individualRequest.getKey())).findFirst().map(Map.Entry::getKey).orElse(null);
            request.append(key + "==" + individualRequest.getValue());
        }
        request.setLinebreakSymbol("||");
        String[] results = translate(request.toString()).split(" ?\\|\\| ?");
        for (String result : results) {
            String[] splitted = result.split(" ?(?:==|(?:\\\\u003d){2}) ?", 2);
            if (splitted.length >= 2)
                bulkRequests.put(bulkRequestsObjects.get(Integer.parseInt(splitted[0])), splitted[1]);
        }
        clearRequests();
        return bulkRequests;
    }

    public void clearRequests() {
        initializedBulkMap = false;
    }

    public void setLanguages(String fromLanguage, String toLanguage) {
        this.fromLanguage = fromLanguage;
        this.toLanguage = toLanguage;
    }

    public void setFromLanguage(String fromLanguage) {
        this.fromLanguage = fromLanguage;
    }

    public void setToLanguage(String toLanguage) {
        this.toLanguage = toLanguage;
    }

    private String prepareTranslateURL(String text) {
        return TRANSLATE_URL.replace("SOURCE", fromLanguage).replace("DEST", toLanguage).replace("TEXT", text);
    }

    public final static String TRANSLATE_URL = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=SOURCE&tl=DEST&dt=t&q=TEXT";
    public final static String TRANSLATE_RESULT_REGEX = "\\[+\"(.+?)\",\".*";

    public final static String Afrikaans = "af";
    public final static String Albanian = "sq";
    public final static String Amharic = "am";
    public final static String Arabic = "ar";
    public final static String Armenian = "hy";
    public final static String Azerbaijani = "az";
    public final static String Basque = "eu";
    public final static String Belarusian = "be";
    public final static String Bengali = "bn";
    public final static String Bosnian = "bs";
    public final static String Bulgarian = "bg";
    public final static String Catalan = "ca";
    public final static String Cebuano = "ceb (ISO-639-2)";
    public final static String ChineseSimplified = "zh-CN";
    public final static String ChineseTraditional = "zh-TW";
    public final static String Corsican = "co";
    public final static String Croatian = "hr";
    public final static String Czech = "cs";
    public final static String Danish = "da";
    public final static String Dutch = "nl";
    public final static String English = "en";
    public final static String Esperanto = "eo";
    public final static String Estonian = "et";
    public final static String Finnish = "fi";
    public final static String French = "fr";
    public final static String Frisian = "fy";
    public final static String Galician = "gl";
    public final static String Georgian = "ka";
    public final static String German = "de";
    public final static String Greek = "el";
    public final static String Gujarati = "gu";
    public final static String HaitianCreole = "ht";
    public final static String Hausa = "ha";
    public final static String Hawaiian = "haw";
    public final static String Hebrew = "he or iw";
    public final static String Hindi = "hi";
    public final static String Hmong = "hmn";
    public final static String Hungarian = "hu";
    public final static String Icelandic = "is";
    public final static String Igbo = "ig";
    public final static String Indonesian = "id";
    public final static String Irish = "ga";
    public final static String Italian = "it";
    public final static String Japanese = "ja";
    public final static String Javanese = "jv";
    public final static String Kannada = "kn";
    public final static String Kazakh = "kk";
    public final static String Khmer = "km";
    public final static String Kinyarwanda = "rw";
    public final static String Korean = "ko";
    public final static String Kurdish = "ku";
    public final static String Kyrgyz = "ky";
    public final static String Lao = "lo";
    public final static String Latin = "la";
    public final static String Latvian = "lv";
    public final static String Lithuanian = "lt";
    public final static String Luxembourgish = "lb";
    public final static String Macedonian = "mk";
    public final static String Malagasy = "mg";
    public final static String Malay = "ms";
    public final static String Malayalam = "ml";
    public final static String Maltese = "mt";
    public final static String Maori = "mi";
    public final static String Marathi = "mr";
    public final static String Mongolian = "mn";
    public final static String MyanmarBurmese = "my";
    public final static String Nepali = "ne";
    public final static String Norwegian = "no";
    public final static String NyanjaChichewa = "ny";
    public final static String OdiaOriya = "or";
    public final static String Pashto = "ps";
    public final static String Persian = "fa";
    public final static String Polish = "pl";
    public final static String PortuguesePortugal = "pt";
    public final static String PortugueseBrazil = "pt";
    public final static String Punjabi = "pa";
    public final static String Romanian = "ro";
    public final static String Russian = "ru";
    public final static String Samoan = "sm";
    public final static String ScotsGaelic = "gd";
    public final static String Serbian = "sr";
    public final static String Sesotho = "st";
    public final static String Shona = "sn";
    public final static String Sindhi = "sd";
    public final static String SinhalaSinhalese = "si";
    public final static String Slovak = "sk";
    public final static String Slovenian = "sl";
    public final static String Somali = "so";
    public final static String Spanish = "es";
    public final static String Sundanese = "su";
    public final static String Swahili = "sw";
    public final static String Swedish = "sv";
    public final static String TagalogFilipino = "tl";
    public final static String Tajik = "tg";
    public final static String Tamil = "ta";
    public final static String Tatar = "tt";
    public final static String Telugu = "te";
    public final static String Thai = "th";
    public final static String Turkish = "tr";
    public final static String Turkmen = "tk";
    public final static String Ukrainian = "uk";
    public final static String Urdu = "ur";
    public final static String Uyghur = "ug";
    public final static String Uzbek = "uz";
    public final static String Vietnamese = "vi";
    public final static String Welsh = "cy";
    public final static String Xhosa = "xh";
    public final static String Yiddish = "yi";
    public final static String Yoruba = "yo";
    public final static String Zulu = "zu";

}
