package yanwittmann.ai.provider;

import java.util.ArrayList;

public abstract class DataProvider<Type> {

    protected int currentDataIndex = 0;
    protected int inputSize = 0;
    protected int expectedOutputSize = 0;

    private final ArrayList<double[]> inputs = new ArrayList<>();
    private final ArrayList<double[]> outputs = new ArrayList<>();

    public void prepareData(int inputSize, int expectedOutputSize) {
        this.inputSize = inputSize;
        this.expectedOutputSize = expectedOutputSize;
        if (currentExists()) {
            reset();
            do {
                inputs.add(generateInput());
                outputs.add(generateOutput());
            } while (next());
        }
        reset();
    }

    public boolean next() {
        currentDataIndex++;
        return currentExists();
    }

    public void reset() {
        currentDataIndex = 0;
    }

    public int index() {
        return currentDataIndex;
    }

    public double[] input() {
        return currentExists() ? inputs.get(currentDataIndex) : null;
    }

    public double[] output() {
        return currentExists() ? outputs.get(currentDataIndex) : null;
    }

    /**
     * Override this method.<br>
     *
     * @return The current data input for a given size.
     */
    protected abstract double[] generateInput();

    /**
     * Override this method.<br>
     *
     * @return The current data input for a given size and input.
     */
    public abstract double[] generateInput(Type input);

    /**
     * Override this method.<br>
     *
     * @return The current data expected output for a given size.
     */
    protected abstract double[] generateOutput();

    /**
     * Override this method.<br>
     * Checks whether the current data index exists in the dataset.
     */
    public abstract boolean currentExists();

    /**
     * Override this method.<br>
     *
     * @return The dataset size.
     */
    public abstract int size();
}
