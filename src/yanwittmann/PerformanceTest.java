package yanwittmann;

/**
 * Use this class to measure the total and average time that a certain operation needs for executing a certain amount of times.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 */
public abstract class PerformanceTest {
    private final int repeatCount;
    private long result = -1;

    public PerformanceTest(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    /**
     * Repeats the performanceTest task the given amount of times
     * @return Returns the milliseconds it took to run the test
     */
    public PerformanceTest start() {
        long before = System.currentTimeMillis();
        for (int i = 0; i < repeatCount; i++)
            perform();
        result = System.currentTimeMillis() - before;
        return this;
    }

    public long getResult() {
        return result;
    }

    public double getAverageResult() {
        return Double.parseDouble(result + "") / repeatCount;
    }

    public abstract void perform();
}
