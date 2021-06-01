package yanwittmann.log;

/**
 * Generates a progress bar that can be printed. Can also estimate the remaining time.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 *
 * @author Yan Wittmann
 */
public class Progress {

    private double max;
    private double current = 0;
    private int length;
    private long startTime = -1;

    public Progress(double max) {
        this.max = max;
        this.length = 33;
    }

    public Progress(double max, int length) {
        this.max = max;
        this.length = length;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public void setLength(int length) {
        this.length = length;
    }

    /**
     * Sets the value of the progress bar.
     */
    public void update(double value) {
        if (startTime == -1)
            startTime = System.currentTimeMillis();
        current = value;
    }

    /**
     * Generates a printable progress bar. Use \r to update the already printed version.
     */
    public String getBar() {
        int currentProgressBarIndex = (int) Math.ceil(((double) length / max) * current);
        String formattedPercent = String.format(" %5.1f %% ", (100 * currentProgressBarIndex) / (double) length);
        int percentStartIndex = ((length - formattedPercent.length()) / 2);

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int progressBarIndex = 0; progressBarIndex < length; progressBarIndex++) {
            if (progressBarIndex <= percentStartIndex - 1
                || progressBarIndex >= percentStartIndex + formattedPercent.length()) {
                sb.append(currentProgressBarIndex <= progressBarIndex ? " " : "=");
            } else if (progressBarIndex == percentStartIndex) {
                sb.append(formattedPercent);
            }
        }
        sb.append("]");

        return sb.toString();
    }

    /**
     * @return The estimated time in milliseconds until the progress bar reaches 100%.
     */
    public long getRemainingTimeMillis() {
        return (long) ((max - current) * ((System.currentTimeMillis() - startTime) / current));
    }

    /**
     * @return The estimated time in seconds until the progress bar reaches 100%.
     */
    public int getRemainingTimeSeconds() {
        return (int) Math.round(0.4 + (getRemainingTimeMillis() / 1000.0));
    }

    /**
     * Prints the current version of the progress bar with the remaining time in seconds.
     */
    public void print() {
        System.out.print("\r" + getBar() + " ~" + getRemainingTimeSeconds() + "s  ");
    }

    /**
     * Prints the current version of the progress bar with the remaining time in seconds.
     */
    public void updateAndPrint(double value) {
        update(value);
        print();
    }
}
