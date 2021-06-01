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
        return switch (character) {
            case 'a' -> 0.03;
            case 'b' -> 0.06;
            case 'c' -> 0.09;
            case 'd' -> 0.12;
            case 'e' -> 0.15;
            case 'f' -> 0.18;
            case 'g' -> 0.21;
            case 'h' -> 0.24;
            case 'i' -> 0.27;
            case 'j' -> 0.3;
            case 'k' -> 0.33;
            case 'l' -> 0.36;
            case 'm' -> 0.39;
            case 'n' -> 0.42;
            case 'o' -> 0.45;
            case 'p' -> 0.48;
            case 'q' -> 0.51;
            case 'r' -> 0.54;
            case 's' -> 0.57;
            case 't' -> 0.6;
            case 'u' -> 0.63;
            case 'v' -> 0.66;
            case 'w' -> 0.69;
            case 'x' -> 0.72;
            case 'y' -> 0.75;
            case 'z' -> 0.78;
            case ' ' -> 0.81;
            default -> 1;
        };
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
