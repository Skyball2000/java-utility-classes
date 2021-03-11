package yanwittmann;

import javax.swing.*;
import java.awt.*;
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
 * Plenty of functions that are useful for plenty of different cases, such as getting the screen size, moving the mouse
 *   cursor, string functions, clipboard actions and much more.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 */
public abstract class FileUtils {

    public static boolean makeDirectories(File directory) {
        if (!directory.exists()) return directory.mkdirs();
        return true;
    }

    public static boolean writeFile(File file, ArrayList<String> lines) {
        return writeFile(file, lines.toArray(new String[0]));
    }

    public static boolean writeFile(File file, String[] lines) {
        BufferedWriter outputWriter;
        try {
            outputWriter = new BufferedWriter(new FileWriter(file));
            for (String line : lines) {
                outputWriter.write(line);
                outputWriter.newLine();
            }
            outputWriter.flush();
            outputWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeFile(File file, String line) {
        BufferedWriter outputWriter;
        try {
            outputWriter = new BufferedWriter(new FileWriter(file));
            outputWriter.write(line);
            outputWriter.flush();
            outputWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String[] readFileToStringArray(File file) {
        try {
            return Files.readAllLines(file.toPath()).toArray(new String[0]);
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<String> readFileToArrayList(File file) {
        try {
            return (ArrayList<String>) Files.readAllLines(file.toPath());
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] readFileToByteArray(File file) {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean writeFileFromByteArray(File file, byte[] array) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(array);
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copyFile(String sourceFile, String destinationFile) {
        try {
            Files.copy(new File(sourceFile).toPath(), (new File(destinationFile)).toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation) {
        try {
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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean deleteDirectory(File directory) {
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

    public static boolean deleteFilesInDirectory(String directory) {
        try {
            File dir = new File(directory);
            File[] listFiles = dir.listFiles();
            if (listFiles == null) return false;
            boolean deletedAll = true;
            for (File file : listFiles)
                if (!file.delete()) deletedAll = false;
            return deletedAll;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
    public static boolean openFile(File file, File workingDirectory) {
        try {
            ProcessBuilder pb = new ProcessBuilder(file.getAbsoluteFile().toString());
            pb.directory(workingDirectory.getAbsoluteFile());
            pb.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean openFileUsingProcessBuilder(File file) {
        try {
            ProcessBuilder pb = new ProcessBuilder(file.getAbsolutePath());
            pb.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean openFile(File file) {
        try {
            if (!Desktop.isDesktopSupported())
                return false;
            Desktop desktop = Desktop.getDesktop();
            if (file.exists()) {
                desktop.open(file);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Opens a Jar file with a set of arguments arguments.<br>
     * Thanks to <a href="https://stackoverflow.com/questions/6811522/changing-the-working-directory-of-command-from-java/6811578">Maurício Linhares</a>
     * and <a href="https://stackoverflow.com/questions/17985036/run-a-jar-file-from-java-program">Aniket Thakur</a> for helping out with this.
     */
    public static boolean openJar(String jar, String path, String[] args) {
        try {
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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean pack(File sourceDirPath, File zipFilePath) {
        try {
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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void unpack(String zipFile, String destination) throws IOException {
        File destDir = new File(destination);
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newUnZipFile(destDir, zipEntry);
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

    public static boolean isArchive(File file) {
        int fileSignature = 0;
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            fileSignature = raf.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileSignature == 0x504B0304 || fileSignature == 0x504B0506 || fileSignature == 0x504B0708;
    }

    public static long fileSize(String filename) {
        return new File(filename).length();
    }

    public static int onlineFileSize(String url) {
        try {
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
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return -1;
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
        try {
            return chooser.getSelectedFile();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File[] windowsFilePicker() {
        FileDialog picker = new java.awt.FileDialog((java.awt.Frame) null);
        picker.setVisible(true);
        return picker.getFiles();
    }

    public static String getFilename(String path) {
        File f = new File(path);
        return f.getName();
    }

    public static String[] getResponseFromURL(String pUrl) {
        pUrl = pUrl.replace(" ", "%20");
        ArrayList<String> lines = new ArrayList<>();
        try {
            URL url = new URL(pUrl);
            BufferedReader read = new BufferedReader(new InputStreamReader(url.openStream()));
            String i;
            while ((i = read.readLine()) != null)
                lines.add(i);
            read.close();
            String[] result = new String[lines.size()];
            for (int j = 0; j < lines.size(); j++)
                result[j] = lines.get(j);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean downloadFile(File file, String url) {
        try {
            return downloadFile(file, new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean downloadFile(File file, URL url) {
        BufferedInputStream in;
        FileOutputStream fout;
        try {
            in = new BufferedInputStream(url.openStream());
            fout = new FileOutputStream(file);

            final byte[] data = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
            in.close();
            fout.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean connectedToInternet() {
        try {
            URL url = new URL("http://www.google.com");
            URLConnection connection = url.openConnection();
            connection.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Font getFont(String filename) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File(filename)).deriveFont(30f);
        } catch (Exception e) {
            return null;
        }
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

    public static long lastModified(String file) {
        return new File(file).lastModified();
    }

    public static void setLastModified(File file, long time) {
        try {
            Files.setLastModifiedTime(file.toPath(), FileTime.fromMillis(time));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isHidden(String file) {
        return new File(file).isHidden();
    }

    public static void setHidden(String file, boolean hidden) {
        try {
            Files.setAttribute(Paths.get(file), "dos:hidden", hidden, LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
