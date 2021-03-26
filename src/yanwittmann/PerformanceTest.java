package yanwittmann;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Use this class to measure the total and average time that a certain operation needs for executing a certain amount of times.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 */
public abstract class PerformanceTest {

    private final static long DEFAULT_VALUE = -1;

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

    /**
     * Override this method to implement your own test.
     */
    public abstract void perform();

    public PerformanceTest removeFirst() {
        if (results.size() > 0)
            results.remove(0);
        return this;
    }

    public PerformanceTest removeLast() {
        if (results.size() > 0)
            results.remove(results.size() - 1);
        return this;
    }

    public long getTotalResult() {
        if (results.size() == 0) return DEFAULT_VALUE;
        return results.stream().mapToLong(result -> result).sum();
    }

    public long getAverageResult() {
        if (results.size() == 0) return DEFAULT_VALUE;
        return results.stream().mapToLong(result -> result).sum() / repeatCount;
    }

    public long getMaxResult() {
        if (results.size() == 0) return DEFAULT_VALUE;
        return results.stream().mapToLong(result -> result).max().getAsLong();
    }

    public long getMinResult() {
        if (results.size() == 0) return DEFAULT_VALUE;
        return results.stream().mapToLong(result -> result).min().getAsLong();
    }

    public void print() {
        if (results.size() == 0)
            System.out.println("Make sure to run the PerformanceTest before getting the results!");
        else System.out.println(toString());
    }

    @Override
    public String toString() {
        if (results.size() > 0) {
            ArrayList<ArrayList<String>> rows = new ArrayList<>();
            rows.add(new ArrayList<>(Arrays.asList("all", "" + Arrays.toString(results.toArray(new Long[0])))));
            rows.add(new ArrayList<>(Arrays.asList("total", "" + getTotalResult())));
            rows.add(new ArrayList<>(Arrays.asList("max", "" + getMaxResult())));
            rows.add(new ArrayList<>(Arrays.asList("min", "" + getMinResult())));
            rows.add(new ArrayList<>(Arrays.asList("average", "" + getAverageResult())));
            return "PerformanceTest results in ms:\n" + GeneralUtils.formatAsTable(rows);
        } else return "Make sure to run the PerformanceTest before getting the results!";
    }
}
