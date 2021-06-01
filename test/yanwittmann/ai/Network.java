package yanwittmann.ai;

import yanwittmann.ai.provider.DataProvider;
import yanwittmann.visjs.VisJS;
import yanwittmann.file.File;
import yanwittmann.types.LineBuilder;
import yanwittmann.utils.GeneralUtils;
import yanwittmann.log.Log;
import yanwittmann.log.Progress;

import java.io.IOException;
import java.util.*;

/**
 * The main AI class. Create an object from this class and add layers and a data provider to get
 * started with training the model.<br>
 * WARNING: The created network will not be very elaborate. Use with caution.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 * @author Yan Wittmann
 */
public class Network {

    private Layer inputLayer, outputLayer;
    private final ArrayList<Layer> hiddenLayers = new ArrayList<>();
    private DataProvider dataProvider;

    public Network() {
    }

    public Network(File file) throws IOException {
        Log.debug("Creating network from [{}]", file.getName());

        ArrayList<String> loadedNetwork = file.readToArrayList();

        inputLayer = new Layer(Integer.parseInt(loadedNetwork.get(0)));
        inputLayer.setLayerNumber(-1);
        loadedNetwork.remove(0);

        for (String hiddenLayer : loadedNetwork) {
            if (hiddenLayer.length() == 0) break;
            if (hiddenLayers.size() > 0)
                hiddenLayers.add(new Layer(hiddenLayers.get(hiddenLayers.size() - 1), hiddenLayer));
            else hiddenLayers.add(new Layer(inputLayer, hiddenLayer));
        }

        outputLayer = new Layer(hiddenLayers.get(hiddenLayers.size() - 1), loadedNetwork.get(loadedNetwork.size() - 1));
        outputLayer.setLayerNumber(1000);

        Log.debug("Reconstructed network from [{}]", file.getName());
    }

    public void setInputLayer(Layer inputLayer) {
        inputLayer.setLayerNumber(-1);
        this.inputLayer = inputLayer;
    }

    public void setOutputLayer(Layer outputLayer) {
        outputLayer.setLayerNumber(1000);
        this.outputLayer = outputLayer;
        if (hiddenLayers.size() > 0) this.outputLayer.setPreviousLayer(hiddenLayers.get(hiddenLayers.size() - 1));
    }

    public void addHiddenLayer(Layer hiddenLayer) {
        hiddenLayer.setLayerNumber(hiddenLayers.size());
        if (hiddenLayers.size() == 0) hiddenLayer.setPreviousLayer(inputLayer);
        else hiddenLayer.setPreviousLayer(hiddenLayers.get(hiddenLayers.size() - 1));
        hiddenLayers.add(hiddenLayer);
        if (outputLayer != null) this.outputLayer.setPreviousLayer(hiddenLayers.get(hiddenLayers.size() - 1));
    }

    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public void prepare() {
        dataProvider.prepareData(inputLayer.size(), outputLayer.size());
    }

    public void train(int amount) {
        Progress bar = new Progress(amount, 50);
        bar.updateAndPrint(0);
        String currentScoreBarText = "";
        for (int i = 0; i < amount; i++) {
            double beforeScore = train();
            permute();
            double afterScore = train();
            if (i % 100 == 0 && i != 0) {
                bar.updateAndPrint(i);
                System.out.print(currentScoreBarText);
            }
            if (beforeScore < afterScore) {
                undoPermute();
            } else {
                bar.updateAndPrint(i);
                currentScoreBarText = "  score: [" + afterScore + "]                                ";
                System.out.print(currentScoreBarText);
            }
        }
        bar.updateAndPrint(amount);
        System.out.print("                                         ");
    }

    private double train() {
        double[] scores = new double[dataProvider.size()];
        do {
            setPerformInputData(dataProvider.input());
            scores[dataProvider.index()] = scoreResult(perform(), dataProvider.output());
        } while (dataProvider.next());
        dataProvider.reset();
        return Arrays.stream(scores).sum();
        //return Arrays.stream(scores).average().getAsDouble();
        //return Arrays.stream(scores).max().getAsDouble();
    }

    private double scoreResult(double[] output, double[] expectedOutput) {
        double value = 0;
        for (int i = 0; i < output.length && i < expectedOutput.length; i++) {
            value += Math.pow(expectedOutput[i] - output[i], 2);
        }
        return value;
    }

    private void setPerformInputData(double[] input) {
        inputLayer.setValues(input);
    }

    public double[] perform() {
        hiddenLayers.forEach(Layer::perform);
        outputLayer.perform();
        return outputLayer.getOutput();
    }

    public double[] ask(double[] input) {
        setPerformInputData(input);
        return perform();
    }

    private final Set<Layer> permutedLayers = new HashSet<>();
    private int permuteAmountLayers = 4;
    private int permuteAmountNodes = 2;

    public void permute() {
        permutedLayers.clear();
        for (int i = 0; i < permuteAmountLayers; i++)
            permutedLayers.add(hiddenLayers.get(GeneralUtils.randomNumber(0, hiddenLayers.size() - 1)));
        permutedLayers.add(outputLayer);
        permutedLayers.forEach(permutedLayer -> permutedLayer.permute(permuteAmountNodes));
    }

    public void undoPermute() {
        permutedLayers.forEach(Layer::undoPermute);
    }

    public void setPermuteAmountLayers(int permuteAmountLayers) {
        this.permuteAmountLayers = permuteAmountLayers;
    }

    public void setPermuteAmountNodes(int permuteAmountNodes) {
        this.permuteAmountNodes = permuteAmountNodes;
    }

    public void save(File file) throws IOException {
        LineBuilder output = new LineBuilder();
        output.append(inputLayer.size());
        hiddenLayers.stream().map(Layer::export).forEach(output::append);
        output.append("");
        output.append(outputLayer.export());
        file.write(output.toString());
    }

    public void printTrainingResults() {
        double worseScore = 0;
        double bestScore = 99999;
        do {
            double[] input = dataProvider.input();
            double[] expected = dataProvider.output();
            setPerformInputData(input);
            double[] output = perform();
            double score = scoreResult(output, expected);
            if (score > worseScore) worseScore = score;
            if (score < bestScore) bestScore = score;

            Log.info("");
            Log.info(" results for : " + Arrays.toString(input) + " " + Arrays.toString(expected));
            Log.info("         are : " + Arrays.toString(output));
            Log.info("       score : " + score);
        } while (dataProvider.next());
        dataProvider.reset();
        Log.info("");
        Log.info("  best score : " + bestScore);
        Log.info(" worse score : " + worseScore);
        Log.info("");
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }

    public ArrayList<String> generateVis() {
        VisJS vis = new VisJS("network", 1600, 900);
        vis.addNodeType(1, new VisJS.NodeTypeBuilder());
        vis.addNodeType(2, new VisJS.NodeTypeBuilder().setColor("#3ddb3d"));
        vis.addNodeType(3, new VisJS.NodeTypeBuilder().setColor("#fcd31c"));

        for (Node node : inputLayer.getNodes()) {
            vis.addNode(node.getCoordinates() + "\\n" + node.getValue(), 3);
        }

        for (Layer hiddenLayer : hiddenLayers) {
            for (Node node : hiddenLayer.getNodes()) {
                vis.addNode(node.getCoordinates() + "\\n" + node.getValue(), 1);
                for (Node prevNode : hiddenLayer.getPreviousLayer().getNodes()) {
                    vis.addEdge(prevNode.getCoordinates() + "\\n" + prevNode.getValue(), node.getCoordinates() + "\\n" + node.getValue(), "" + node.getWeight(prevNode));
                }
            }
        }

        for (Node node : outputLayer.getNodes()) {
            vis.addNode(node.getCoordinates() + "\\n" + node.getValue(), 2);
            for (Node prevNode : outputLayer.getPreviousLayer().getNodes()) {
                vis.addEdge(prevNode.getCoordinates() + "\\n" + prevNode.getValue(), node.getCoordinates() + "\\n" + node.getValue(), "" + node.getWeight(prevNode));
            }
        }

        vis.setOption(VisJS.PHYSICS_SOLVER, VisJS.PHYSICS_SOLVER_FORCE_ATLAS_2_BASED);
        vis.setOption(VisJS.EDGE_ARROW_DIRECTION, VisJS.EDGE_ARROW_TO);
        vis.setOption(VisJS.EDGE_LENGTH, 200);

        return vis.generate();
    }

    public final static Random random = new Random();
}
