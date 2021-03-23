package yanwittmann;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.Timer;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * A wide variety of functions related to reading, writing and watching files.<br>
 * Use setDisplayExceptions(boolean) to decide whether the exceptions thrown should be printed.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 */
public abstract class FileUtils {

    public static String getLastJavaFilePickLocation() {
        return lastJavaFilePickLocation;
    }

    public static boolean makeDirectories(File directory) {
        if (!directory.exists()) return directory.mkdirs();
        return true;
    }

    public static boolean makeDirectories(String directory) {
        File file = new File(directory);
        if (!file.exists()) return file.mkdirs();
        return true;
    }

    public static void writeFile(File file, ArrayList<String> lines) throws IOException {
        makeDirectories(file.getAbsolutePath().replace(file.getName(), ""));
        writeFile(file, lines.toArray(new String[0]));
    }

    public static void writeFile(File file, String[] lines) throws IOException {
        makeDirectories(file.getAbsolutePath().replace(file.getName(), ""));
        BufferedWriter outputWriter;
        outputWriter = new BufferedWriter(new FileWriter(file));
        for (String line : lines) {
            outputWriter.write(line);
            outputWriter.newLine();
        }
        outputWriter.flush();
        outputWriter.close();
    }

    public static void writeFile(File file, String line) throws IOException {
        makeDirectories(file.getAbsolutePath().replace(file.getName(), ""));
        BufferedWriter outputWriter;
        outputWriter = new BufferedWriter(new FileWriter(file));
        outputWriter.write(line);
        outputWriter.flush();
        outputWriter.close();
    }

    public static String[] readFileToStringArray(File file) throws IOException {
        return Files.readAllLines(file.toPath()).toArray(new String[0]);
    }

    public static ArrayList<String> readFileToArrayList(File file) throws IOException {
        return (ArrayList<String>) Files.readAllLines(file.toPath());
    }

    public static byte[] readFileToByteArray(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    public static void writeFileFromByteArray(File file, byte[] array) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(array);
        fos.close();
    }

    public static void copyFile(File sourceFile, File destinationFile) throws IOException {
        Files.copy(sourceFile.toPath(), (destinationFile).toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void copyDirectory(File sourceDirectoryLocation, File destinationDirectoryLocation) throws IOException {
        copyDirectory(sourceDirectoryLocation.getAbsolutePath(), destinationDirectoryLocation.getAbsolutePath());
    }

    private static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation) throws IOException {
        Files.walk(Paths.get(sourceDirectoryLocation))
                .forEach(source -> {
                    Path destination = Paths.get(destinationDirectoryLocation, source.toString()
                            .substring(sourceDirectoryLocation.length()));
                    try {
                        Files.copy(source, destination);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public static boolean deleteDirectory(File directory) {
        if (!directoryExists(directory)) return false;
        File[] files = directory.listFiles();
        if (files != null) {
            for (final File file : files) {
                deleteDirectory(file);
            }
        }
        return directory.delete();
    }

    public static boolean deleteFile(File file) {
        return file.delete();
    }

    public static boolean deleteFilesInDirectory(File directory) {
        File[] listFiles = directory.listFiles();
        if (listFiles == null) return false;
        boolean deletedAll = true;
        for (File file : listFiles)
            if (!file.delete()) deletedAll = false;
        return deletedAll;
    }

    public static boolean fileExists(File file) {
        return file.exists();
    }

    public static boolean directoryExists(File directory) {
        return directory.exists();
    }

    /**
     * This only works for executable files.
     *
     * @param file             The file to open.
     * @param workingDirectory The working directory the file should open as.
     */
    public static void openFile(File file, File workingDirectory) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(file.getAbsoluteFile().toString());
        pb.directory(workingDirectory.getAbsoluteFile());
        pb.start();
    }

    public static void openFileUsingProcessBuilder(File file) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(file.getAbsolutePath());
        pb.start();
    }

    public static boolean openFile(File file) throws IOException {
        if (!Desktop.isDesktopSupported())
            return false;
        Desktop desktop = Desktop.getDesktop();
        if (file.exists()) {
            desktop.open(file);
            return true;
        }
        return false;
    }

    /**
     * Opens a Jar file with a set of arguments arguments.<br>
     * Thanks to <a href="https://stackoverflow.com/questions/6811522/changing-the-working-directory-of-command-from-java/6811578">Maurício Linhares</a>
     * and <a href="https://stackoverflow.com/questions/17985036/run-a-jar-file-from-java-program">Aniket Thakur</a> for helping out with this.
     */
    public static void openJar(String jar, String path, String[] args) throws IOException {
        File pathToExecutable = new File(jar);
        String[] args2 = new String[args.length + 3];
        args2[0] = "java";
        args2[1] = "-jar";
        args2[2] = pathToExecutable.getAbsolutePath();
        System.arraycopy(args, 0, args2, 3, args2.length - 3);
        ProcessBuilder builder = new ProcessBuilder(args2);
        builder.directory(new File(path).getAbsoluteFile());
        builder.redirectErrorStream(true);
        builder.start();
    }

    public static void pack(File sourceDirPath, File zipFilePath) throws IOException {
        deleteFile(zipFilePath);
        Path p = Files.createFile(zipFilePath.toPath());
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
            Path pp = sourceDirPath.toPath();
            Files.walk(pp)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    public static void unpack(File zipFile, File destination) throws IOException {
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newUnZipFile(destination, zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                // fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                // write file content
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    private static File newUnZipFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public static boolean isArchive(File file) throws IOException {
        int fileSignature = 0;
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        fileSignature = raf.readInt();
        return fileSignature == 0x504B0304 || fileSignature == 0x504B0506 || fileSignature == 0x504B0708;
    }

    public static long fileSize(File file) {
        return file.length();
    }

    public static int onlineFileSize(String url) throws MalformedURLException {
        URL url1 = new URL(url);
        URLConnection conn = null;
        try {
            conn = url1.openConnection();
            if (conn != null) {
                ((HttpURLConnection) conn).setRequestMethod("HEAD");
            }
            assert conn != null;
            conn.getInputStream();
            return conn.getContentLength();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn instanceof HttpURLConnection) {
                ((HttpURLConnection) conn).disconnect();
            }
        }
    }

    public static ArrayList<File> getFiles(File directory) {
        ArrayList<File> foundFiles = new ArrayList<>();
        return getFiles(directory, "", foundFiles);
    }

    public static ArrayList<File> getFiles(File directory, String ending) {
        ArrayList<File> foundFiles = new ArrayList<>();
        return getFiles(directory, ending, foundFiles);
    }

    private static ArrayList<File> getFiles(File directory, String ending, ArrayList<File> foundFiles) {
        if (directory.isDirectory()) {
            File[] found = directory.listFiles();
            if (found == null) return foundFiles;
            for (File file : found)
                if (file.isDirectory()) getFiles(file, ending, foundFiles);
                else if (ending.length() == 0 || file.getName().endsWith(ending))
                    foundFiles.add(file);
        }
        return foundFiles;
    }

    private static String lastJavaFilePickLocation = "";

    public static File javaFilePicker() {
        JFileChooser chooser;
        if (lastJavaFilePickLocation.equals(""))
            chooser = new JFileChooser(System.getProperty("user.home") + "/Desktop");
        else chooser = new JFileChooser(lastJavaFilePickLocation);
        chooser.showOpenDialog(null);
        lastJavaFilePickLocation = chooser.getSelectedFile().getAbsolutePath();
        return chooser.getSelectedFile();
    }

    public static File[] windowsFilePicker() {
        FileDialog picker = new java.awt.FileDialog((java.awt.Frame) null);
        picker.setVisible(true);
        return picker.getFiles();
    }

    public static String getFilename(File file) {
        return file.getName();
    }

    public static String[] getResponseFromURL(String url) throws IOException {
        url = url.replace(" ", "%20");
        ArrayList<String> lines = new ArrayList<>();
        URL urlObject = new URL(url);
        BufferedReader read = new BufferedReader(new InputStreamReader(urlObject.openStream()));
        String i;
        while ((i = read.readLine()) != null)
            lines.add(i);
        read.close();
        String[] result = new String[lines.size()];
        for (int j = 0; j < lines.size(); j++)
            result[j] = lines.get(j);
        return result;
    }

    public static String[] getResponseURL(String url) throws IOException {
        url = url.replace(" ", "%20");
        ArrayList<String> lines = new ArrayList<>();
        URL urlObject = new URL(url);
        URLConnection request = urlObject.openConnection();
        request.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        request.setRequestProperty("Content-Type", "text/plain; utf-8");
        request.connect();
        BufferedReader read = new BufferedReader(new InputStreamReader((InputStream) request.getContent()));
        String i;
        while ((i = read.readLine()) != null)
            lines.add(i);
        read.close();
        String[] result = new String[lines.size()];
        for (int j = 0; j < lines.size(); j++)
            result[j] = lines.get(j);
        return result;
    }

    public static void downloadFile(File file, String url) throws IOException {
        downloadFile(file, new URL(url));
    }

    public static void downloadFile(File file, URL url) throws IOException {
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

    public static void connectedToInternet() throws IOException {
        URL url = new URL("http://www.google.com");
        URLConnection connection = url.openConnection();
        connection.connect();
    }

    public static Font getFont(File file) throws IOException, FontFormatException {
        return Font.createFont(Font.TRUETYPE_FONT, file).deriveFont(30f);
    }

    private static final HashMap<String, Timer> watchFiles = new HashMap<>();

    public static void addWatchFile(File file, int period, FileWatcher fileWatcher) {
        if (watchFiles.containsKey(file.getAbsolutePath())) return;
        fileWatcher.setFile(file);
        watchFiles.put(file.getAbsolutePath(), saveWatchFile(fileWatcher, period));
    }

    public static void removeWatchFile(File file) {
        if (!watchFiles.containsKey(file.getAbsolutePath())) return;
        Timer t = watchFiles.get(file.getAbsolutePath());
        t.cancel();
        t.purge();
        watchFiles.remove(file.getAbsolutePath());
    }

    //thanks to Réal Gagnon for this part of the code (https://www.rgagnon.com/javadetails/java-0490.html)
    private static Timer saveWatchFile(FileWatcher fileWatcher, int period) {
        Timer timer = new Timer();
        timer.schedule(fileWatcher, new Date(), period);
        return timer;
    }

    public abstract static class FileWatcher extends TimerTask {
        private File file;
        private long lastModified;

        public void setFile(File file) {
            this.file = file;
            this.lastModified = file.lastModified();
        }

        public final void run() {
            long timeStamp = file.lastModified();
            if (this.lastModified != timeStamp) {
                this.lastModified = timeStamp;
                onChange(file);
            }
        }

        protected abstract void onChange(File file);
    }

    public static long lastModified(File file) {
        return file.lastModified();
    }

    public static void setLastModified(File file, long time) throws IOException {
        Files.setLastModifiedTime(file.toPath(), FileTime.fromMillis(time));
    }

    public static boolean isHidden(File file) {
        return file.isHidden();
    }

    public static void setHidden(File file, boolean hidden) throws IOException {
        Files.setAttribute(file.toPath(), "dos:hidden", hidden, LinkOption.NOFOLLOW_LINKS);
    }
}
