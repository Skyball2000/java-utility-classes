package yanwittmann;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        
    }

    private static void visTest() {
        Vis vis = new Vis("myNetwork", 1200, 800);

        vis.addNodeType(0, new Vis.NodeTypeBuilder().setShape(Vis.NodeTypeBuilder.SHAPE_ELLIPSE).setShadow("#848c99", 10));

        int amountNodes = 10;
        for (int i = 1; i <= amountNodes; i++) {
            vis.addNode("node " + i, 0);
        }
        for (int i = 0; i < amountNodes; i++) {
            vis.addEdge("node " + GeneralUtils.randomNumber(1, amountNodes), "node " + i, "title " + i, true);
            vis.addEdge("node " + GeneralUtils.randomNumber(1, amountNodes), "node " + i, "title " + i, true);
            vis.addEdge("node " + GeneralUtils.randomNumber(1, amountNodes), "node " + i, "title " + i, true);
            vis.addEdge("node " + GeneralUtils.randomNumber(1, amountNodes), "node " + i, "title " + i, true);
            vis.addEdge("node " + GeneralUtils.randomNumber(1, amountNodes), "node " + i, "title " + i, true);
        }

        vis.setOption(Vis.EDGE_ARROW_DIRECTION, Vis.EDGE_ARROW_TO);
        vis.setOption(Vis.EDGE_DASHES, true);
        vis.setOption(Vis.EDGE_SHADOW, true);
        vis.setOption(Vis.EDGE_SMOOTH, true);
        vis.setOption(Vis.EDGE_LENGTH, 200);
        vis.setOption(Vis.PHYSICS_SOLVER, Vis.PHYSICS_SOLVER_FORCE_ATLAS_2_BASED);

        FileUtils.writeFile(new File("out.html"), vis.generate().toArray(new String[0]));
    }

    private static void mathEvalTest() {
        try {
            String expression = "-3 * (-45 + - 15 % 10) -3 pow 4 + (12/3) sqrt 2";
            System.out.println(MathEval.prepareExpression(expression));
            System.out.println(MathEval.evaluate(expression));
        } catch (MathEval.MathEvalException e) {
            e.printStackTrace();
        }
    }
}