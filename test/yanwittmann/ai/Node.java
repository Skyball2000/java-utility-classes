package yanwittmann.ai;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * A node inside of an AI Network Layer.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 * @author Yan Wittmann
 */
public class Node {

    private int layerNumber = 0;
    private int nodeNumber = 0;
    private String coordinates;
    private double threshold = Network.random.nextDouble();
    private final HashMap<Node, Double> weights = new HashMap<>();
    private double value = 0.0;

    public Node() {
    }

    public Node(Layer previousLayer, String input) {
        String[] parameters = input.split(" ");
        coordinates = parameters[0];
        threshold = Double.parseDouble(parameters[1]);
        String[] weightsStrings = parameters[2].split(";");
        for (String weightsString : weightsStrings) {
            if (weightsString.length() == 0) continue;
            String[] coordValues = weightsString.split(",");
            Node node = previousLayer.getNode(coordValues[0]);
            weights.put(node, Double.parseDouble(coordValues[1]));
        }
    }

    public void perform(Layer previousLayer) {
        if (previousLayer.size() != weights.size())
            for (Node node : previousLayer.getNodes())
                if (!weights.containsKey(node))
                    weights.put(node, Network.random.nextDouble());
        double[] inputs = new double[previousLayer.size()];
        for (int i = 0; i < previousLayer.getNodes().size(); i++) {
            Node node = previousLayer.getNodes().get(i);
            inputs[i] = node.getValue() * weights.get(node);
        }
        value = sum(inputs);
    }

    private double avg(double[] arr) {
        return Arrays.stream(arr).average().getAsDouble();
    }

    private double sum(double[] arr) {
        return Arrays.stream(arr).sum();
    }

    private double max(double[] arr) {
        return Arrays.stream(arr).max().getAsDouble();
    }

    public double getValue() {
        if (value > threshold || layerNumber == 1000 || layerNumber == -1)
            return value;
        return 0.0;
    }

    public void setValue(double value) {
        this.value = value;
    }

    private double lastPermuteThreshold = 0;
    private final HashMap<Node, Double> lastPermutedNodeWeights = new HashMap<>();

    public void permute(int amount) {
        lastPermuteThreshold = threshold;
        threshold = permuteThreshold(threshold);

        if (weights.size() == 0) return;
        lastPermutedNodeWeights.clear();
        Node[] nodes = weights.keySet().toArray(new Node[0]);
        for (int i = 0; i < amount; i++) {
            Node selectedNode = nodes[Network.random.nextInt(nodes.length)];
            lastPermutedNodeWeights.put(selectedNode, weights.get(selectedNode));
        }
        for (Node node : lastPermutedNodeWeights.keySet()) {
            weights.put(node, permuteWeight(weights.get(node)));
        }
    }

    public void undoPermute() {
        threshold = lastPermuteThreshold;
        for (Node node : lastPermutedNodeWeights.keySet())
            weights.put(node, lastPermutedNodeWeights.get(node));
    }

    private final static double PERMUTE_FACTOR = 1.2;
    private final static double MAX_THRESHOLD = 0.9;
    private final static int PERMUTE_METHOD = 0;

    private double permuteThreshold(double value) {
        switch (PERMUTE_METHOD) {
            case 0:
                if (Network.random.nextBoolean() && threshold != MAX_THRESHOLD)
                    return Math.min(0.9, Math.max(0, value + (Network.random.nextDouble() / PERMUTE_FACTOR)));
                else return Math.min(0.9, Math.max(0, value - (Network.random.nextDouble() / PERMUTE_FACTOR)));
            case 1:
                return Network.random.nextDouble();
            case 2:
                if (Network.random.nextBoolean() && threshold != MAX_THRESHOLD)
                    return Math.max(0.9, value + (Network.random.nextDouble() / PERMUTE_FACTOR));
                else return Math.max(0.9, value - (Network.random.nextDouble() / PERMUTE_FACTOR));
            default:
                return 0.0;
        }
    }

    private double permuteWeight(double value) {
        switch (PERMUTE_METHOD) {
            case 0:
                if (Network.random.nextBoolean())
                    return Math.min(1, Math.max(0, value + (Network.random.nextDouble() / PERMUTE_FACTOR)));
                else return Math.min(1, Math.max(0, value - (Network.random.nextDouble() / PERMUTE_FACTOR)));
            case 1:
                return Network.random.nextDouble();
            case 2:
                if (Network.random.nextBoolean())
                    return Math.max(0, value + (Network.random.nextDouble() / PERMUTE_FACTOR));
                else return Math.max(0, value - (Network.random.nextDouble() / PERMUTE_FACTOR));
            default:
                return 0.0;
        }
    }

    public void setLayerNumber(int layerNumber) {
        this.layerNumber = layerNumber;
    }

    public void setNodeNumber(int nodeNumber) {
        this.nodeNumber = nodeNumber;
    }

    public String getCoordinates() {
        if (coordinates == null) coordinates = layerNumber + ":" + nodeNumber;
        return coordinates;
    }

    public double getWeight(Node node) {
        return weights.get(node);
    }

    @Override
    public String toString() {
        String weights = this.weights.entrySet().stream().map(nodeWeight -> nodeWeight.getKey().getCoordinates() + "," + nodeWeight.getValue() + ";").collect(Collectors.joining());
        return getCoordinates() + " " + threshold + " " + weights;
    }
}
