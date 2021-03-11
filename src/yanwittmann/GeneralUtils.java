package yanwittmann;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A wide variety of functions you can use.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 */
public class GeneralUtils {

    private boolean isInit = false;

    public void init() {
        if (isInit) return;
        new Thread(() -> {
            try {
                screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                isInit = true;
            } catch (Exception e) {
                isInit = false;
            }
        }).start();
    }

    public boolean isInit() {
        return isInit;
    }

    public static int countOccurrences(String text, String find) {
        int count = 0, lastIndex = 0;
        while (lastIndex != -1) {
            lastIndex = text.indexOf(find, lastIndex);
            if (lastIndex != -1) {
                count++;
                lastIndex += find.length();
            }
        }
        return count;
    }

    public static String getWordAtIndex(String str, int index) {
        if (index == -1 || index > str.length()) return "";
        int minIndex = index;
        while (true) {
            minIndex--;
            if (minIndex <= -1) break;
            if (str.charAt(minIndex) == ' ' || str.charAt(minIndex) == '\n') break;
        }
        minIndex++;
        int maxIndex = index;
        while (true) {
            maxIndex++;
            if (maxIndex >= str.length()) break;
            if (str.charAt(maxIndex) == ' ' || str.charAt(maxIndex) == '\n') break;
        }

        return str.substring(minIndex, maxIndex);
    }

    public static String shuffleCharacters(String input) {
        ArrayList<Character> characters = new ArrayList<>();
        for (char c : input.toCharArray()) {
            characters.add(c);
        }
        StringBuilder output = new StringBuilder(input.length());
        while (characters.size() != 0) {
            int randPicker = (int) (Math.random() * characters.size());
            output.append(characters.remove(randPicker));
        }
        return output.toString();
    }

    public static String[] replaceAllLines(String[] array, String find, String replace) {
        for (int i = 0; i < array.length; i++) array[i] = array[i].replace(find, replace);
        return array;
    }

    public static int randomNumber(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    public static long getCurrentMillis() {
        return System.currentTimeMillis();
    }

    public static void sortArray(String[] arr) {
        Arrays.sort(arr);
    }

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ")", replacement);
    }

    private static Dimension screenSize;
    private static double screenWidth = 0;
    private static double screenHeight = 0;

    public static int getScreenWidth() {
        if (screenWidth == 0) screenWidth = screenSize.getWidth();
        return (int) screenWidth;
    }

    public static int getScreenHeight() {
        if (screenHeight == 0) screenHeight = screenSize.getHeight();
        return (int) screenHeight;
    }

    public static int[] getMouseLocation() {
        PointerInfo a = MouseInfo.getPointerInfo();
        Point b = a.getLocation();
        return new int[]{(int) b.getX(), (int) b.getY()};
    }

    public static String getClipboard() {
        try {
            return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void copyString(String text) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    /*  "file.separator" 	Character that separates components of a file path. This is "/" on UNIX and "\" on Windows.
        "java.class.path" 	Path used to find directories and JAR archives containing class files. Elements of the class path are separated by a platform-specific character specified in the path.separator property.
        "java.home" 	    Installation directory for Java Runtime Environment (JRE)
        "java.vendor" 	    JRE vendor name
        "java.vendor.url" 	JRE vendor URL
        "java.version" 	    JRE version number
        "line.separator" 	Sequence used by operating system to separate lines in text files
        "os.arch" 	        Operating system architecture
        "os.name" 	        Operating system name
        "os.version" 	    Operating system version
        "path.separator" 	Path separator character used in java.class.path
        "user.dir" 	        User working directory
        "user.home" 	    User home directory
        "user.name" 	    User account name
     */
    public static String getWindowsUsername() {
        return getWindowsProperty("user.name");
    }

    public static String getWindowsProperty(String property) {
        try {
            return System.getProperty(property);
        } catch (Exception e) {
            return "";
        }
    }

    public static void setWindowsProperty(String key, String value) {
        System.setProperty(key, value);
    }

    public static String[] appendArray(String[] arr, String app) {
        String[] ret = new String[arr.length + 1];
        System.arraycopy(arr, 0, ret, 0, arr.length);
        ret[arr.length] = app;
        return ret;
    }

    public static void printArray(Object[] arr) {
        System.out.println(Arrays.toString(arr));
    }

    public static void openURL(String url) {
        Desktop desktop = java.awt.Desktop.getDesktop();
        try {
            URI oURL = new URI(url);
            desktop.browse(oURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void mailto(String url) {
        try {
            Desktop desktop;
            if (Desktop.isDesktopSupported()
                    && (desktop = Desktop.getDesktop()).isSupported(Desktop.Action.MAIL)) {
                URI mailto = new URI(url);
                desktop.mail(mailto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Color getRandomSaturatedColor() {
        int red, green, blue, main1 = randomNumber(0, 2), main0;
        do {
            main0 = randomNumber(0, 2);
        } while (main0 == main1);
        red = randomNumber(0, 254);
        green = randomNumber(0, 254);
        blue = randomNumber(0, 254);
        if (main1 == 0) red = randomNumber(240, 254);
        else if (main1 == 1) green = randomNumber(240, 254);
        else if (main1 == 2) blue = randomNumber(240, 254);
        if (main0 == 0) red = randomNumber(0, 10);
        else if (main0 == 1) green = randomNumber(0, 10);
        else if (main0 == 2) blue = randomNumber(0, 10);
        return new Color(red, green, blue);
    }

    public static Color getColorFromString(String color) {
        if (color.matches("#?[\\da-f]{6}")) {
            return hex2Rgb(color);
        } else if (color.matches("\\d{1,3}[, ]{1,2}\\d{1,3}[, ]{1,2}\\d{1,3}")) {
            String[] split = color.split("[, ]{1,2}");
            return new Color(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        }
        return new Color(0, 0, 0);
    }

    public static Color getColorFromClipboard() {
        return getColorFromString(getClipboard());
    }

    public static Color hex2Rgb(String colorStr) {
        colorStr = colorStr.replace("#", "");
        return new Color(
                Integer.valueOf(colorStr.substring(0, 2), 16),
                Integer.valueOf(colorStr.substring(2, 4), 16),
                Integer.valueOf(colorStr.substring(4, 6), 16));
    }

    public static String[] filterEmptyLines(String[] arr) {
        ArrayList<String> list = new ArrayList<>(Arrays.asList(arr));
        list.removeAll(Arrays.asList("", null));
        return list.toArray(new String[0]);
    }

    //https://stackoverflow.com/questions/6714045/how-to-resize-jlabel-imageicon; Thanks to trolologuy!
    public static ImageIcon getScaledImage(ImageIcon srcImg, int w, int h) {
        Image image = srcImg.getImage();
        Image newimg = image.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }

    public static BufferedImage readImageFromFile(String filename) {
        try {
            return ImageIO.read(new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage toBufferedImage(Image img, boolean usesAlpha) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), usesAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }
}
