package yanwittmann.types;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * This class is still work in progress, but can be used to read an image file and edit individual pixels.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 * @author Yan Wittmann
 */
public class ImageEditor {
    private BufferedImage image;

    public ImageEditor(File file) {
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        image = newImage;
    }

    public ImageEditor(BufferedImage image) {
        this.image = image;
    }

    public boolean save(File file) {
        try {
            ImageIO.write(image, file.getName().replaceAll(".+\\.([^.]+)", "$1"), file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getColor(int x, int y) {
        return image.getRGB(x, y);
    }

    public int getAlpha(int x, int y) {
        return (image.getRGB(x, y) >> 24) & 0xFF;
    }

    public int getRed(int x, int y) {
        return (image.getRGB(x, y) >> 16) & 0xFF;
    }

    public int getGreen(int x, int y) {
        return (image.getRGB(x, y) >> 8) & 0xFF;
    }

    public int getBlue(int x, int y) {
        return image.getRGB(x, y) & 0xFF;
    }

    public void setColor(int x, int y, int color) {
        image.setRGB(x, y, color);
    }

    public void setAlpha(int x, int y, int alpha) {
        int red = getRed(x, y);
        int green = getGreen(x, y);
        int blue = getBlue(x, y);
        int color = (alpha << 24) | (red << 16) | (green << 8) | blue;
        image.setRGB(x, y, color);
    }

    public void setRed(int x, int y, int red) {
        int alpha = getAlpha(x, y);
        int green = getGreen(x, y);
        int blue = getBlue(x, y);
        int color = (alpha << 24) | (red << 16) | (green << 8) | blue;
        image.setRGB(x, y, color);
    }

    public void setGreen(int x, int y, int green) {
        int alpha = getAlpha(x, y);
        int red = getRed(x, y);
        int blue = getBlue(x, y);
        int color = (alpha << 24) | (red << 16) | (green << 8) | blue;
        image.setRGB(x, y, color);
    }

    public void setBlue(int x, int y, int blue) {
        int alpha = getAlpha(x, y);
        int red = getRed(x, y);
        int green = getGreen(x, y);
        int color = (alpha << 24) | (red << 16) | (green << 8) | blue;
        image.setRGB(x, y, color);
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

    //https://stackoverflow.com/questions/6714045/how-to-resize-jlabel-imageicon; Thanks to trolologuy!
    public void scale(int width, int height) {
        image = toBufferedImage(image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), false);
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
