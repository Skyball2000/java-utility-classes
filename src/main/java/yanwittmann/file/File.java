package yanwittmann.file;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

/**
 * A better file object that allows you to perform extra actions related to files.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 *
 * @author Yan Wittmann
 */
public class File extends java.io.File {

    public File(String pathname) {
        super(pathname);
    }

    public File(String parent, String child) {
        super(parent, child);
    }

    public File(java.io.File parent, String child) {
        super(parent, child);
    }

    public File(URI uri) {
        super(uri);
    }

    public void setHidden(boolean hidden) throws IOException {
        FileUtils.setHidden(this, hidden);
    }

    public Font getFont() throws IOException, FontFormatException {
        return FileUtils.getFont(this);
    }

    public void download(URL url) throws IOException {
        FileUtils.downloadFile(this, url);
    }

    public ArrayList<java.io.File> getFiles() {
        return FileUtils.getFiles(this);
    }

    public ArrayList<java.io.File> getFiles(String ending) {
        return FileUtils.getFiles(this, ending);
    }

    public boolean isArchive() throws IOException {
        return FileUtils.isArchive(this);
    }

    public void unpack(java.io.File destination) throws IOException {
        FileUtils.unpack(this, destination);
    }

    public void pack(java.io.File destination) throws IOException {
        FileUtils.pack(this, destination);
    }

    public void open() throws IOException {
        FileUtils.openFile(this);
    }

    public void deleteFilesInDirectory() {
        FileUtils.deleteFilesInDirectory(this);
    }

    public void deleteDirectory() {
        FileUtils.deleteDirectory(this);
    }

    public void copyDirectory(java.io.File destination) throws IOException {
        FileUtils.copyDirectory(this, destination);
    }

    public void copyFile(java.io.File destinationFile) throws IOException {
        FileUtils.copyDirectory(this, destinationFile);
    }

    public void write(byte[] array) throws IOException {
        FileUtils.writeFileFromByteArray(this, array);
    }

    public void write(String line) throws IOException {
        FileUtils.writeFile(this, line);
    }

    public void write(String[] lines) throws IOException {
        FileUtils.writeFile(this, lines);
    }

    public void write(ArrayList<String> lines) throws IOException {
        FileUtils.writeFile(this, lines);
    }

    public String[] readToArray() throws IOException {
        return FileUtils.readFileToStringArray(this);
    }

    public ArrayList<String> readToArrayList() throws IOException {
        return FileUtils.readFileToArrayList(this);
    }

    public byte[] readToByteArray() throws IOException {
        return FileUtils.readFileToByteArray(this);
    }
}
