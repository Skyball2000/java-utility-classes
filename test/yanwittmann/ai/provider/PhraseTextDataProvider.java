package yanwittmann.ai.provider;

import java.util.List;

public class PhraseTextDataProvider extends DataProvider<String> {

    private List<String> lines;

    public void setData(List<String> lines) {
        this.lines = lines;
        for (int i = 0; i < this.lines.size(); i++)
            this.lines.set(i, this.lines.get(i).toLowerCase());
    }

    @Override
    public double[] generateInput() {
        double[] generatedData = new double[inputSize];
        char[] letters = lines.get(0).substring(index(), index() + inputSize - 1).toCharArray();
        for (int i = 0; i < letters.length && i < generatedData.length; i++)
            generatedData[i] = prepareValue(letters[i]);
        return generatedData;
    }

    @Override
    public double[] generateInput(String input) {
        double[] generatedData = new double[inputSize];
        char[] letters = input.toCharArray();
        for (int i = 0; i < letters.length && i < generatedData.length; i++)
            generatedData[i] = prepareValue(letters[i]);
        return generatedData;
    }

    @Override
    public double[] generateOutput() {
        double[] generatedData = new double[expectedOutputSize];
        generatedData[0] = prepareValue(lines.get(0).charAt(index() + inputSize));
        return generatedData;
    }

    private double prepareValue(char character) {
        switch (character) {
            case 'a':
                return 0.03;
            case 'b':
                return 0.06;
            case 'c':
                return 0.09;
            case 'd':
                return 0.12;
            case 'e':
                return 0.15;
            case 'f':
                return 0.18;
            case 'g':
                return 0.21;
            case 'h':
                return 0.24;
            case 'i':
                return 0.27;
            case 'j':
                return 0.3;
            case 'k':
                return 0.33;
            case 'l':
                return 0.36;
            case 'm':
                return 0.39;
            case 'n':
                return 0.42;
            case 'o':
                return 0.45;
            case 'p':
                return 0.48;
            case 'q':
                return 0.51;
            case 'r':
                return 0.54;
            case 's':
                return 0.57;
            case 't':
                return 0.6;
            case 'u':
                return 0.63;
            case 'v':
                return 0.66;
            case 'w':
                return 0.69;
            case 'x':
                return 0.72;
            case 'y':
                return 0.75;
            case 'z':
                return 0.78;
            case ' ':
                return 0.81;
            default:
                return 1;
        }
    }

    public static char prepareValue(double value) {
        double[] charactersMapped = new double[]{0.03, 0.06, 0.09, 0.12, 0.15, 0.18, 0.21, 0.24, 0.27, 0.3, 0.33, 0.36, 0.39, 0.42, 0.45, 0.48, 0.51, 0.54, 0.57, 0.6, 0.63, 0.66, 0.69, 0.72, 0.75, 0.78, 0.81};
        char[] characters = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', ' '};

        for (int i = 0; i < charactersMapped.length; i++) {
            if (value < charactersMapped[i] + 0.15 && value > charactersMapped[i] - 0.15) return characters[i];
        }
        return 'N';
    }

    @Override
    public boolean currentExists() {
        try {
            lines.get(0).charAt(index() + inputSize);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int size() {
        return lines == null ? 0 : lines.get(0).length() - inputSize;
    }
}
