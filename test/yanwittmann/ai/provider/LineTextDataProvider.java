package yanwittmann.ai.provider;

import yanwittmann.utils.GeneralUtils;

import java.util.Arrays;
import java.util.List;

public class LineTextDataProvider extends DataProvider<String> {

    private List<String> lines;

    public void setData(List<String> lines) {
        this.lines = lines;
    }

    @Override
    public double[] generateInput() {
        double[] generatedData = new double[inputSize];
        String data = lines.get(index());
        for (int i = 0; i < data.length() && i < inputSize; i++)
            generatedData[i] = 1.0 * (data.charAt(i) == '.' ? 1 : 0);
        return generatedData;
    }

    @Override
    public double[] generateInput(String input) {
        double[] generatedData = new double[inputSize];
        for (int i = 0; i < input.length() && i < inputSize; i++)
            generatedData[i] = 1.0 * (input.charAt(i) == '.' ? 1 : 0);
        return generatedData;
    }

    @Override
    public double[] generateOutput() {
        double[] generatedData = new double[expectedOutputSize];
        String data = lines.get(index());
        Arrays.fill(generatedData, 0);
        int count = GeneralUtils.countOccurrences(data, ".");
        if (count > 0)
            generatedData[count - 1] = 1;
        return generatedData;
    }

    @Override
    public boolean currentExists() {
        return lines != null && this.lines.size() > currentDataIndex;
    }

    @Override
    public int size() {
        return lines == null ? 0 : lines.size();
    }
}
