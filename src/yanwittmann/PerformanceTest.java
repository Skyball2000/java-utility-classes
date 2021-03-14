package yanwittmann;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Use this class to measure the total and average time that a certain operation needs for executing a certain amount of times.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 */
public abstract class PerformanceTest {
    private final int repeatCount;
    private ArrayList<Long> results = null;

    public PerformanceTest(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    /**
     * Repeats the performanceTest task the given amount of times
     *
     * @return Returns the milliseconds it took to run the test
     */
    public PerformanceTest start() {
        results = new ArrayList<>();
        for (int i = 0; i < repeatCount; i++) {
            long before = System.currentTimeMillis();
            perform();
            results.add(System.currentTimeMillis() - before);
        }
        return this;
    }

    public long getResult() {
        return results.stream().mapToLong(result -> result).sum();
    }

    public long getAverageResult() {
        return results.stream().mapToLong(result -> result).sum() / repeatCount;
    }

    public long getMaxResult() {
        if (results.size() > 0)
            return results.stream().mapToLong(result -> result).max().getAsLong();
        return 0;
    }

    public long getMinResult() {
        if (results.size() > 0)
            return results.stream().mapToLong(result -> result).min().getAsLong();
        return 0;
    }

    public void print() {
        System.out.println(toString());
    }

    @Override
    public String toString() {
        ArrayList<ArrayList<String>> rows = new ArrayList<>();
        rows.add(new ArrayList<>(Arrays.asList("all", "" + Arrays.toString(results.toArray(new Long[0])))));
        rows.add(new ArrayList<>(Arrays.asList("total", "" + getResult())));
        rows.add(new ArrayList<>(Arrays.asList("max", "" + getMaxResult())));
        rows.add(new ArrayList<>(Arrays.asList("min", "" + getMinResult())));
        rows.add(new ArrayList<>(Arrays.asList("average", "" + getAverageResult())));
        return "PerformanceTest results in ms:\n" + GeneralUtils.formatAsTable(rows);
    }

    public abstract void perform();
}
