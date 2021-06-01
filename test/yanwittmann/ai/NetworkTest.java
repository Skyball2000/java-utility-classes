package yanwittmann.ai;

import org.junit.jupiter.api.Test;
import yanwittmann.ai.provider.DataProvider;
import yanwittmann.ai.provider.LineTextDataProvider;
import yanwittmann.ai.provider.PhraseTextDataProvider;
import yanwittmann.file.File;
import yanwittmann.types.LineBuilder;
import yanwittmann.log.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;

public class NetworkTest {

    private static int size = 100, length = 4;
    private final PrintStream stdout = System.out;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    @Test
    public void visTest(File networkFile, File visFile) throws IOException {
        Network network = new Network(networkFile);

        //LineTextDataProvider dataProvider = new LineTextDataProvider();
        PhraseTextDataProvider dataProvider = new PhraseTextDataProvider();
        network.setDataProvider(dataProvider);
        network.prepare();
        ask(network, "-.--.");
        //ask(network, "abc");

        visFile.write(network.generateVis());
    }

    @Test
    public void loadNetworkTest(File file) throws IOException {
        Network network = new Network(file);

        LineTextDataProvider dataProvider = new LineTextDataProvider();
        network.setDataProvider(dataProvider);
        network.prepare();

        askQuestionsTest(network);
    }

    public static void main(String[] args) throws IOException {
        new NetworkTest().networkTrainTest();
    }

    @Test
    public void networkTrainTest() throws IOException {
        createDataset();

        Network network = new Network();

        network.setInputLayer(new Layer(length));
        network.setOutputLayer(new Layer(length));

        network.addHiddenLayer(new Layer(5));
        network.addHiddenLayer(new Layer(5));

        LineTextDataProvider dataProvider = new LineTextDataProvider();
        dataProvider.setData(new File("res/training/symbols_" + size + "_" + length + ".txt").readToArrayList());
        network.setDataProvider(dataProvider);

        network.setPermuteAmountLayers(3);
        network.setPermuteAmountNodes(1);

        network.prepare();

        network.train(100000);

        network.printTrainingResults();
        askQuestionsTest(network);

        //network.save(new File("res/networks/test 4.ntw"));
    }

    @Test
    public void loadNetworkSentenceTest(File file) throws IOException {
        Network network = new Network(file);

        PhraseTextDataProvider dataProvider = new PhraseTextDataProvider();
        network.setDataProvider(dataProvider);
        network.prepare();

        String word = "abc";
        StringBuilder stringBuilder = new StringBuilder(word);
        for (int i = 0; i < 15; i++) {
            char newText = PhraseTextDataProvider.prepareValue(ask(network, word)[0]);
            stringBuilder.append(newText);
            word = word.substring(1, 3) + newText;
        }
        Log.info(stringBuilder);
    }

    @Test
    public void networkSentenceTrainTest() throws IOException {
        Network network = new Network();

        network.setInputLayer(new Layer(3));
        network.setOutputLayer(new Layer(1));

        network.addHiddenLayer(new Layer(26));
        network.addHiddenLayer(new Layer(26));
        network.addHiddenLayer(new Layer(10));
        network.addHiddenLayer(new Layer(5));

        PhraseTextDataProvider dataProvider = new PhraseTextDataProvider();
        dataProvider.setData(new File("res/training/loremlorem.txt").readToArrayList());
        network.setDataProvider(dataProvider);

        network.setPermuteAmountLayers(1);
        network.setPermuteAmountNodes(3);

        network.prepare();

        network.train(200000);

        network.printTrainingResults();

        network.save(new File("res/networks/test 4.ntw"));
    }

    private static void askQuestionsTest(Network network) {
        switch (length) {
            case 3:
                ask(network, "-..");
                ask(network, "--.");
                ask(network, ".--");
                break;
            case 4:
                ask(network, "-.-.");
                ask(network, "-.--");
                ask(network, ".---");
                ask(network, ".-..");
                break;
            case 5:
                ask(network, "-.-.-");
                ask(network, "-.---");
                ask(network, ".----");
                ask(network, ".-..-");
                break;
        }
    }

    private static double[] ask(Network network, String input) {
        DataProvider dataProvider = network.getDataProvider();
        double[] output = network.ask(dataProvider.generateInput(input));

        //Log.info("asking [{}] and got {}, char: {}", input, Arrays.toString(output), PhraseTextDataProvider.prepareValue(output[0])));
        Log.info("asking [{}] and got {}, sorted: {}", input, Arrays.toString(output), Arrays.toString(indexise(output)));
        return output;
    }

    private static double[] indexise(double[] arr) {
        double[] sorted = arr.clone();
        Arrays.sort(sorted);
        sorted = reverse(sorted);

        double[] ret = new double[arr.length];
        Arrays.fill(ret, -1);
        int current = 1;
        boolean found;
        for (double v : sorted) {
            found = false;
            for (int i = 0; i < arr.length; i++) {
                if (v == arr[i] && ret[i] == -1) {
                    ret[i] = current;
                    found = true;
                }
            }
            if (found) current++;
        }
        return ret;
    }

    private static double[] reverse(double[] array) {
        double[] reversed = new double[array.length];

        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[(array.length - 1) - i];
        }

        return reversed;
    }

    private final static Random random = new Random();

    private static void createDataset() throws IOException {
        LineBuilder lines = new LineBuilder();
        for (int i = 0; i < size; i++) lines.append(generateDatasetLine(length));
        new File("res/training/symbols_" + size + "_" + length + ".txt").write(lines.toString());
    }

    private static String generateDatasetLine(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) builder.append(random.nextBoolean() ? "." : "-");
        return builder.toString();
    }
}
