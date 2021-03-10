package yanwittmann;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Configuration {
    private final HashMap<String, String> values = new HashMap<>();
    private final File file;

    public Configuration(File file) {
        this.file = file;
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

    public void set(String key, String value) {
        values.put(key, value.replace("\n", "CFGEOL"));
        save();
    }

    public String get(String key) {
        return values.containsKey(key) ? values.get(key).replace("CFGEOL", "\n") : null;
    }

    private void save() {
        LineBuilder builder = new LineBuilder();
        for (Map.Entry<String, String> value : values.entrySet())
            builder.append(value.getKey() + ":" + value.getValue());
        FileUtils.writeFile(file, builder.toString());
    }
}
