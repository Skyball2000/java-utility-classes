package yanwittmann.api;

import yanwittmann.types.Configuration;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Use this class to easily store files downloaded from urls and update them after a given time.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 *
 * @author Yan Wittmann
 */
public class ApiTools {

    private File storageDirectory;
    private Configuration meta;

    public ApiTools() {
    }

    public ApiTools(File storageDirectory) {
        setStorageDirectory(storageDirectory);
    }

    public void setStorageDirectory(File storageDirectory) {
        this.storageDirectory = new File(storageDirectory, "files");
    }

    public void initializeStorageDirectory() throws IOException {
        if (!this.storageDirectory.exists()) this.storageDirectory.mkdirs();
        this.meta = new Configuration(new File(this.storageDirectory.getParentFile(), "api.meta"));
    }

    public File get(String url, long refreshTime) throws IOException {
        return get(new URL(url), refreshTime);
    }

    public File get(URL url, long refreshTime) throws IOException {
        if (meta == null) initializeStorageDirectory();
        String key = urlToKey(url);
        long currentTimeMillis = System.currentTimeMillis();
        File file = new File(storageDirectory, key);
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        boolean requiresUpdate;
        if (GET_LOCAL_VERSION_OR_DOWNLOAD == refreshTime) requiresUpdate = !file.exists();
        else {
            requiresUpdate = true;
            if (!(FORCE_UPDATE == refreshTime) && meta.hasKey(key) && file.exists()) {
                String lastUpdated = meta.get(key);
                if (lastUpdated.matches("\\d+"))
                    requiresUpdate = Long.parseLong(meta.get(key)) + refreshTime <= currentTimeMillis;
            }
        }
        if (requiresUpdate) downloadFile(file, url);
        if (requiresUpdate || !meta.hasKey(key))
            meta.set(key, currentTimeMillis);
        return file;
    }

    private String urlToKey(URL url) {
        return url.getHost().replaceAll("[\\\\/:*?\"<>|]", "") + "/" +
               (url.hashCode() + url.getFile().replaceAll("(?:.+[\\\\/])+([^\\\\/]+)", "$1") + "")
                       .replaceAll("[\\\\/:*?\"<>|]", "");
    }

    private void downloadFile(java.io.File file, URL url) throws IOException {
        BufferedInputStream in;
        FileOutputStream fout;
        in = new BufferedInputStream(url.openStream());
        fout = new FileOutputStream(file);

        final byte[] data = new byte[1024];
        int count;
        while ((count = in.read(data, 0, 1024)) != -1) {
            fout.write(data, 0, count);
        }
        in.close();
        fout.close();
    }

    public final static long FORCE_UPDATE = -1;
    public final static long GET_LOCAL_VERSION_OR_DOWNLOAD = -2;
}
