package prfm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A RandomForest is simply a collection of DecisionTrees.
 * These trees are grown in a certain random way, and they vote on decisions.
 */
@SuppressWarnings("serial")
public class RandomForest<D> implements java.io.Serializable {

    /**
     * Grows a random forest from a list of samples.
     *
     * @param attrs     The attributes and their possible values.
     * @param samples   The sample data to train on.
     * @param size      The number of trees in the forest.
     * @param n         The number of sample records to choose with replacement
     *                  for each tree.
     * @param m         The best attribute at each node in a tree will be chosen
     *                  from m attributes selected at random without replacement.
     * @return          A new RandomForest.
     */
    public static <D> RandomForest<D> growRandomForest(
            Map<String,List<String>> attrs,
            List<Sample<D>> samples,
            int size,
            int n,
            int m) throws Exception
    {
        List<DecisionTree<D>> trees = new ArrayList<DecisionTree<D>>(size);
        for (int i = 0; i < size; i++) {
            List<Sample<D>> sampleChoices = ListUtils.choices(samples, n);
            trees.add(DecisionTree.growDecisionTree(attrs, sampleChoices, m));
        }
        return new RandomForest<D>(trees);
    }

    /** The trees in this forest. */
    public List<DecisionTree<D>> trees;

    /**
     * @param trees     The trees in this forest.
     */
    protected RandomForest(List<DecisionTree<D>> trees) {
        this.trees = trees;
    }

    /**
     * Gets the mode decision of the trees in this forest on the sample.
     *
     * @param sample    The sample whose attributes will be used to get
     *                  a decision from the forest.
     * @return          The decision of this forest on the sample.
     */
    public D decide(Sample<D> sample) {
        Counter<D> decisions = new Counter<D>();
        for (DecisionTree<D> tree : trees) {
            D temp = tree.decide(sample.choices);
            if (temp != null) {
                decisions.add(temp);
            }
        }
        return decisions.mode();
    }

    public D eval_decide(Sample<D> sample) {
        Counter<D> decisions = new Counter<D>();
        for (DecisionTree<D> tree : trees) {
            D temp = tree.decide(sample.choices);
            if (temp != null) {
                decisions.add(temp);
            }
        }
        System.out.println("--------------------------------------");
        return decisions.eval_mode();
    }

    /**
     * Run a list of samples against this forest.
     *
     * @param samples   The test samples to evaluate the forest with.
     * @return          The number of correct decisions by this forest.
     */
    public int test(List<Sample<D>> samples) throws Exception {
        int correct = 0;
        D predict = null;
        for (Sample<D> sample : samples) {
            predict = eval_decide(sample);
            System.out.println("predict:" + predict);
            if (sample.decision.equals(predict)) {
                correct++;
            }
        }
        return correct;
    }

    /**
     * Parse input data, and time the construction of a random forest.
     */
    public static void main(String[] args) throws Exception {
        String trainDataFile = "./data/mushrooms.data";
        String testDataFile = "./data/mushrooms_test.data";
        // Parse arguments.
        int size = 100;  // The number of trees in the forest

        // Read samples and attrs from the train file.
        Map<String,List<String>> trainAttrs = new HashMap<String,List<String>>();
        List<Sample<String>> trainData = RandomForestInput.readData(trainDataFile, trainAttrs);
        trainData = ListUtils.shuffle(trainData);

        // Read samples and attrs from the test file.
        Map<String,List<String>> testAttrs = new HashMap<String,List<String>>();
        List<Sample<String>> testData = RandomForestInput.readData(testDataFile, testAttrs);
        // testData = ListUtils.shuffle(testData);

        long t1 = System.currentTimeMillis();
        // Grow the forest.
        int n = trainData.size();      // The number of sample records to choose with replacement for each tree
        int m = trainAttrs.size();     // he best attribute at each node in a tree will be chosen from m attributes selected at random without replacement
        RandomForest<String> forest = RandomForest.<String>growRandomForest(trainAttrs, trainData, size, n, m);
        long t2 = System.currentTimeMillis();
        // Test the forest.
        int correct = forest.test(testData);
        long t3 = System.currentTimeMillis();

        // Print results.
        System.out.println(trainData.size() + " samples used to train the forest.");
        System.out.println(testData.size() + " samples used to test the forest.");
        double percent = 100.0 * correct / testData.size();
        System.out.printf("%.2f%% (%d/%d) tests passed.\n",
                percent, correct, testData.size());
        System.out.println("Forest construction time: " + (t2 - t1) + " ms");
        System.out.println("Forest testing time: " + (t3 - t2) + " ms");

    }

}
