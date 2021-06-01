package yanwittmann.ai;

import yanwittmann.log.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A Layer inside of a AI Network.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 * @author Yan Wittmann
 */
public class Layer {

    private Layer previousLayer;
    private int layerNumber = 0;
    private final ArrayList<Node> nodes = new ArrayList<>();

    public Layer(int size) {
        Log.debug("Creating layer with size [" + size + "]");
        for (int i = 0; i < size; i++) {
            Node node = new Node();
            node.setNodeNumber(nodes.size());
            nodes.add(node);
        }
    }

    public Layer(Layer previousLayer, String input) {
        Log.debug("Creating layer from: " + input);
        this.previousLayer = previousLayer;
        for (String s : input.split("#")) if (s.length() > 0) nodes.add(new Node(previousLayer, s));
        for (Node node : nodes) node.getCoordinates();
    }

    public Layer() {
    }

    public void addNode(Node node) {
        node.setLayerNumber(layerNumber);
        node.setNodeNumber(nodes.size());
        nodes.add(node);
    }

    public void setPreviousLayer(Layer previousLayer) {
        this.previousLayer = previousLayer;
    }

    public Layer getPreviousLayer() {
        return previousLayer;
    }

    public void perform() {
        nodes.forEach(node -> node.perform(previousLayer));
    }

    public double[] getOutput() {
        double[] output = new double[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            output[i] = nodes.get(i).getValue();
        }
        return output;
    }

    public void setValues(double[] values) {
        for (int i = 0; i < nodes.size() && i < values.length; i++) {
            nodes.get(i).setValue(values[i]);
        }
    }

    public int size() {
        return nodes.size();
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    private final Set<Node> permutedNodes = new HashSet<>();

    public void permute(int amount) {
        permutedNodes.clear();
        for (int i = 0; i < amount; i++)
            permutedNodes.add(nodes.get(Network.random.nextInt(nodes.size())));
        permutedNodes.forEach(permutedLayer -> permutedLayer.permute(amount));
    }

    public void undoPermute() {
        permutedNodes.forEach(Node::undoPermute);
    }

    public void setLayerNumber(int layerNumber) {
        this.layerNumber = layerNumber;
        nodes.forEach(node -> node.setLayerNumber(layerNumber));
    }

    public Node getNode(String coordinates) {
        return nodes.stream().filter(node -> node.getCoordinates().equals(coordinates)).findFirst().orElse(null);
    }

    public String export() {
        return nodes.stream().map(node -> node.toString() + "#").collect(Collectors.joining());
    }
}
