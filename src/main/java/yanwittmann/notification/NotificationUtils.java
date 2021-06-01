package yanwittmann.notification;

import huxtable.blur.GaussianFilter;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

/**
 * Utils that are being used by package notification classes.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 *
 * @author Yan Wittmann
 */
class NotificationUtils {

    private static Robot robot;
    public final static Rectangle SCREEN_RECTANGLE = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

    private static Robot getRobot() {
        if (robot == null)
            try {
                robot = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
                robot = null;
            }
        return robot;
    }

    public static BufferedImage getScreenshotImage() {
        return getRobot().createScreenCapture(SCREEN_RECTANGLE);
    }

    public final static GaussianFilter GAUSSIAN_FILTER = new GaussianFilter(4);
    public final static GaussianFilter GAUSSIAN_FILTER_SMALL = new GaussianFilter(3);

    public static BufferedImage blurImage(BufferedImage image) {
        return GAUSSIAN_FILTER.filter(image, image);
    }

    public static BufferedImage blurImageSmall(BufferedImage image) {
        return GAUSSIAN_FILTER_SMALL.filter(image, image);
    }

    public static BufferedImage cropImage(BufferedImage src, Rectangle rect) {
        return src.getSubimage((int) rect.getX(), (int) rect.getY(), rect.width, rect.height);
    }

    public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();

        // This is what we want, but it only does hard-clipping, i.e. aliasing
        // g2.setClip(new RoundRectangle2D ...)

        // so instead fake soft-clipping by first drawing the desired clip shape
        // in fully opaque white with antialiasing enabled...
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));

        // ... then compositing the image on top,
        // using the white shape from above as alpha source
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);

        g2.dispose();

        return output;
    }

    public static Color averageColor(BufferedImage bi, Rectangle rectangle) {
        int x1 = (int) (rectangle.getX() + rectangle.getWidth());
        int y1 = (int) (rectangle.getY() + rectangle.getHeight());
        long sumr = 0, sumg = 0, sumb = 0;
        for (int x = (int) rectangle.getX(); x < x1; x++) {
            for (int y = (int) rectangle.getY(); y < y1; y++) {
                Color pixel = new Color(bi.getRGB(x, y));
                sumr += pixel.getRed();
                sumg += pixel.getGreen();
                sumb += pixel.getBlue();
            }
        }
        int num = (int) (rectangle.getWidth() * rectangle.getHeight());
        return new Color((int) (sumr / toF(num)), (int) (sumg / toF(num)), (int) (sumb / toF(num)));
    }

    public static Color averageColor(BufferedImage bi) {
        int x1 = bi.getWidth();
        int y1 = bi.getHeight();
        long sumr = 0, sumg = 0, sumb = 0;
        for (int x = 0; x < x1; x++) {
            for (int y = 0; y < y1; y++) {
                Color pixel = new Color(bi.getRGB(x, y));
                sumr += pixel.getRed();
                sumg += pixel.getGreen();
                sumb += pixel.getBlue();
            }
        }
        int num = bi.getWidth() * bi.getHeight();
        return new Color((int) (sumr / toF(num)), (int) (sumg / toF(num)), (int) (sumb / toF(num)));
    }

    public static BufferedImage modifyBrightness(BufferedImage image, float amount) {
        RescaleOp op = new RescaleOp(amount, 0, null);
        return op.filter(image, null);
    }

    public static float toF(int i) {
        return Float.parseFloat("" + i);
    }

}
