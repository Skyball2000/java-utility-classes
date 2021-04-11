package yanwittmann.notification;

import yanwittmann.utils.Sleep;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Extend this class to create a custom notification bar.<br>
 * An example using this is the yanwittmann.notification.BlurNotification.<br>
 * Override methods <i>setSize</i>, <i>createNotification</i>, <i>clicked</i>, <i>into</i> and <i>out</i> to customize your own bar.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 * @author Yan Wittmann
 */
public abstract class AbstractNotification extends JFrame {

    private final static int DEFAULT_SHOW_DURATION = 2700;
    private final static int DISTANCE_BETWEEN_NOTIFICATIONS = 15;
    private final static ArrayList<ArrayList<AbstractNotification>> notificationBars = new ArrayList<>();

    private final String[] extraData;

    public AbstractNotification(String text) {
        this.extraData = null;
        lifecycle(text, DEFAULT_SHOW_DURATION);
    }

    public AbstractNotification(String text, String[] extraData) {
        this.extraData = extraData;
        lifecycle(text, DEFAULT_SHOW_DURATION);
    }

    public AbstractNotification(String text, String[] extraData, int displayDuration) {
        this.extraData = extraData;
        lifecycle(text, displayDuration);
    }

    public AbstractNotification(String text, int displayDuration) {
        this.extraData = null;
        lifecycle(text, displayDuration);
    }

    private void lifecycle(String text, int displayDuration) {
        create(text);
        new Thread(() -> {
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    clicked(text, extraData);
                    out();
                }
            });
            into();
            Sleep.milliseconds(displayDuration);
            out();
            removeFromList();
        }).start();
    }

    private void create(String text) {
        this.setTitle("");
        this.setAlwaysOnTop(true);
        this.setUndecorated(true);

        setSize(text);
        autoSetLocation();
        createNotification(text);
        createNotification();

        this.setType(Type.UTILITY);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.pack();
        this.setVisible(false);
    }

    private final static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());

    /**
     * Override this method to set the size of the frame to fit your text and data.
     */
    public void setSize(String text) {
        setSize(200, 50);
    }

    /**
     * Automatically sets the location of the notification based on the already existing ones.
     */
    private void autoSetLocation() {
        int taskBarSizeBottom = scnMax.bottom;
        int taskBarSizeRight = scnMax.right;

        int height = getHeight();

        for (int j = 0; j < notificationBars.size(); j++) {
            ArrayList<AbstractNotification> bar = notificationBars.get(j);
            for (int i = screenSize.height - taskBarSizeBottom - getHeight(); i >= 0; i--) {
                if (i - DISTANCE_BETWEEN_NOTIFICATIONS < 0) break;
                if (checkIfSpaceIsFree(i, height, bar)) {
                    bar.add(this);
                    setLocation(screenSize.width - taskBarSizeRight - getWidth() - DISTANCE_BETWEEN_NOTIFICATIONS - getDistanceUntilBar(j), i - DISTANCE_BETWEEN_NOTIFICATIONS);
                    return;
                }
            }
        }

        notificationBars.add(new ArrayList<>(Collections.singletonList(this)));
        setLocation(screenSize.width - taskBarSizeRight - getWidth() - DISTANCE_BETWEEN_NOTIFICATIONS - getDistanceUntilBar(notificationBars.size() - 1), screenSize.height - taskBarSizeBottom - getHeight() - DISTANCE_BETWEEN_NOTIFICATIONS);
    }

    private int getDistanceUntilBar(int index) {
        int distance = 0;
        for (int i = 0; i < notificationBars.size() && i < index; i++) {
            int maxWidth = 0;
            for (AbstractNotification notification : notificationBars.get(i)) {
                maxWidth = Math.max(notification.getWidth(), maxWidth);
            }
            distance += maxWidth + DISTANCE_BETWEEN_NOTIFICATIONS;
        }
        return distance;
    }

    private boolean checkIfSpaceIsFree(int y, int height, ArrayList<AbstractNotification> notifications) {
        for (AbstractNotification notification : notifications) {
            if (notification.equals(this)) continue;
            if (notification.getY() <= y + height && notification.getY() + notification.getHeight() >= y)
                return false;
        }
        return true;
    }

    private void removeFromList() {
        for (int i = 0; i < notificationBars.size(); i++)
            if (notificationBars.get(i).contains(this)) {
                notificationBars.get(i).remove(this);
                if (notificationBars.get(i).size() == 0) notificationBars.remove(i);
                return;
            }
    }

    public String[] getExtraData() {
        return extraData;
    }

    /**
     * Called when notification is clicked.
     */
    public abstract void clicked(String text, String[] extraData);

    /**
     * Code that shows the notification.
     */
    public void into() {
        int currentOpacity = 0;
        setOpacity(0f);
        this.setVisible(true);
        for (; currentOpacity <= 100; currentOpacity += 2) {
            try {
                Thread.sleep(2);
            } catch (Exception ignored) {
            }
            setOpacity(currentOpacity * 0.01f);
        }
    }

    /**
     * Code that hides the notification.
     */
    public void out() {
        int currentOpacity = 100, startX = getX(), stepsDone = 0;
        double xPerStep = 30d / 50d;
        for (; currentOpacity > 0; currentOpacity -= 2) {
            Sleep.milliseconds(5);
            stepsDone++;
            setLocation((int) (startX + (xPerStep * stepsDone)), getY());
            setOpacity(currentOpacity * 0.01f);
        }
        dispose();
    }

    /**
     * Notification bar creation code.
     */
    void createNotification(String text) {
    }

    /**
     * Notification bar creation code.
     */
    void createNotification() {
    }
}
