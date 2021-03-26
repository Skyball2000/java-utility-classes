package yanwittmann.notification;

import yanwittmann.GeneralUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * A notification bar that appears in the bottom right corner that has a blurred background.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 */
public class BlurNotification extends AbstractNotification {

    public BlurNotification(String text) {
        super(text, null);
    }

    public BlurNotification(String text, String[] extraData) {
        super(text, extraData);
    }

    public BlurNotification(String text, String[] extraData, int displayDuration) {
        super(text, extraData, displayDuration);
    }

    public BlurNotification(String text, int displayDuration) {
        super(text, displayDuration);
    }

    @Override
    public void clicked(String text, String[] extraData) {
        System.out.println("BlurNotification clicked '" + text + (getExtraData() == null ? "'" : "' " + Arrays.toString(extraData)));
    }

    private Rectangle barRectangle;
    private JLabel notificationText, backgroundImageLabel, frameBorderLabel;

    @Override
    public void setSize(String text) {
        int xSize = 40 + text.length() * 14, ySize = 50;
        this.setSize(xSize, ySize);
    }

    @Override
    void createNotification(String text) {
        barRectangle = new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        JPanel contentPane = new JPanel(null);
        contentPane.setPreferredSize(new Dimension(getWidth(), getHeight()));
        contentPane.setBackground(new Color(0, 0, 0, 0));
        setBackground(new Color(0, 0, 0, 0));

        notificationText = new JLabel(text);
        notificationText.setBounds(20, 0, (int) barRectangle.getWidth(), (int) barRectangle.getHeight());
        notificationText.setBackground(new Color(0, 0, 0, 0));
        notificationText.setBorder(null);
        notificationText.setFont(new Font("Monospaced", Font.BOLD, 24));
        notificationText.setVisible(true);
        contentPane.add(notificationText);

        frameBorderLabel = new JLabel();
        frameBorderLabel.setBounds(0, 0, (int) barRectangle.getWidth(), (int) barRectangle.getHeight());
        frameBorderLabel.setBackground(new Color(0, 0, 0, 0));
        frameBorderLabel.setBorder(roundedLineBorderWhite);
        contentPane.add(frameBorderLabel);

        backgroundImageLabel = new JLabel();
        backgroundImageLabel.setBounds(0, 0, (int) barRectangle.getWidth(), (int) barRectangle.getHeight());
        backgroundImageLabel.setBackground(new Color(0, 0, 0, 0));
        backgroundImageLabel.setVisible(true);
        contentPane.add(backgroundImageLabel);

        this.setContentPane(contentPane);

        setBarBackground();
    }

    private final static TextBubbleBorder roundedLineBorderWhite = new TextBubbleBorder(Color.WHITE, 4, 18, 0);
    private final static TextBubbleBorder roundedLineBorderBlack = new TextBubbleBorder(Color.BLACK, 4, 18, 0);

    private void setBarBackground() {
        BufferedImage background = NotificationUtils.getScreenshotImage();
        background = NotificationUtils.cropImage(background, barRectangle);
        Color average = NotificationUtils.averageColor(background);
        if (average.getRed() + average.getGreen() + average.getBlue() > 300)
            average = Color.BLACK;
        else average = Color.WHITE;
        if (average.getBlue() == 255)
            background = NotificationUtils.modifyBrightness(background, .9f);
        else background = NotificationUtils.modifyBrightness(background, 1.3f);
        background = NotificationUtils.blurImage(background);
        background = NotificationUtils.makeRoundedCorner(background, 20);
        backgroundImageLabel.setIcon(new ImageIcon(background));

        notificationText.setForeground(average);
        if (average.getBlue() == 255) frameBorderLabel.setBorder(roundedLineBorderWhite);
        else frameBorderLabel.setBorder(roundedLineBorderBlack);
    }
}
