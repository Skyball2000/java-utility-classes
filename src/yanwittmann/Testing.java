package yanwittmann;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

public class Testing {

    public static void main(String[] args) {
        CountApi counter = new CountApi("yan", "test");
        System.out.println(counter.create(false));
        System.out.println(counter.get());
        System.out.println(counter.hit());
        System.out.println(counter.get());
        System.out.println(counter.set(4));
        System.out.println(counter.get());

    }

    public static void translateTest() {
        GoogleTranslate translate = new GoogleTranslate();
        translate.setLanguages(GoogleTranslate.German, GoogleTranslate.French);

        System.out.println(new PerformanceTest(3) {
            @Override
            public void perform() {
                translate.addRequest("Eine frage", "Hallo wie geht es dir?");
                translate.addRequest(1, "Dies ist ein zweiter Text");
                translate.addRequest("Was esse ich gerne?", "Ich esse gerne Eis am Strand.");
                for (Map.Entry<Object, String> entry : translate.performRequests().entrySet())
                    System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }.start().toString());
    }

    private static void visTest() {
        VisJS vis = new VisJS("myNetwork", 1200, 800);

        vis.addNodeType(0, new VisJS.NodeTypeBuilder().setShape(VisJS.NodeTypeBuilder.SHAPE_ELLIPSE).setShadow("#848c99", 10));

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

        vis.setOption(VisJS.EDGE_ARROW_DIRECTION, VisJS.EDGE_ARROW_TO);
        vis.setOption(VisJS.EDGE_DASHES, true);
        vis.setOption(VisJS.EDGE_SHADOW, true);
        vis.setOption(VisJS.EDGE_SMOOTH, true);
        vis.setOption(VisJS.EDGE_LENGTH, 200);
        vis.setOption(VisJS.PHYSICS_SOLVER, VisJS.PHYSICS_SOLVER_FORCE_ATLAS_2_BASED);

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