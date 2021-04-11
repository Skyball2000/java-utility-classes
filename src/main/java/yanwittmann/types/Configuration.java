package yanwittmann.types;

import yanwittmann.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Easily store string values in a file using a key-value pair.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 * @author Yan Wittmann
 */
public class Configuration {
    private final HashMap<String, String> values = new HashMap<>();
    private final File file;
    private boolean autosave = true;

    public Configuration(File file) throws IOException {
        this.file = file;
        load();
    }

    public Configuration(File file, boolean autosave) throws IOException {
        this.file = file;
        this.autosave = autosave;
        load();
    }

    private void load() throws IOException {
        if (file.exists()) {
            ArrayList<String> input = FileUtils.readFileToArrayList(file);
            if (input == null) return;
            for (String line : input) {
                if (line.matches("[^:]+:.+")) {
                    values.put(line.replaceAll("([^:]+):(.+)", "$1"), line.replaceAll("([^:]+):(.+)", "$2"));
                } else if (line.matches("[^:]+:?")) {
                    values.put(line.replaceAll("([^:]+):?", "$1"), "");
                } else if (line.length() > 0) {
                    values.put(line, "");
                }
            }
        }
    }

    public void set(String key, String value) {
        key = prepareKey(key);
        values.put(key, value.replace("\n", "CFGEOL"));
        if (autosave) {
            try {
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void remove(String key) {
        key = prepareKey(key);
        values.remove(key);
        if (autosave) {
            try {
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String get(String key) {
        key = prepareKey(key);
        return values.containsKey(key) ? values.get(key).replace("CFGEOL", "\n") : null;
    }

    public String getOrDefault(String key, String def) {
        key = prepareKey(key);
        if (values.containsKey(key)) return values.get(key).replace("CFGEOL", "\n");
        else set(key, def);
        return null;
    }

    private HashMap<String, String> get() {
        return values;
    }

    private String prepareKey(String key) {
        return key.replace(":", "CFGCOLO");
    }

    public void setAutosave(boolean autosave) {
        this.autosave = autosave;
    }

    public void save() throws IOException {
        LineBuilder builder = new LineBuilder();
        for (Map.Entry<String, String> value : values.entrySet())
            builder.append(value.getKey() + ":" + value.getValue());
        FileUtils.writeFile(file, builder.toString());
    }

    public Stream<Map.Entry<String, String>> stream() {
        return values.entrySet().stream();
    }
}