package auxiliary;

import java.util.Random;

/**
 *
 * @author daq
 */
public class ClassifierController {
    private Classifier classifier;
    private String clsName;
    private DataSet trainSet;
    private double[][] shuffleTrainFeatures;
    private double[] shuffleTrainLabels;

    public ClassifierController() {
    }

    public ClassifierController(DataSet trainSet, String clsName) {
        this.trainSet = trainSet;
        this.clsName = clsName;
        this.shuffleTrainFeatures = new double[trainSet.getNumInstnaces()][trainSet.getNumAttributes()];
        this.shuffleTrainLabels = new double[trainSet.getNumInstnaces()];
    }

    public void dataHandler() {
        int[] trainDataIndex = new int[trainSet.getNumInstnaces()];
        for (int i = 0; i < trainDataIndex.length; i++) {
            trainDataIndex[i] = i;
        }
        shuffle(trainDataIndex);
        double[][] trainFeatures = trainSet.getFeatures();
        double[] trainLabels = trainSet.getLabels();

        for (int i = 0; i < trainDataIndex.length; i++) {
            shuffleTrainFeatures[i] = trainFeatures[trainDataIndex[i]];
            shuffleTrainLabels[i] = trainLabels[trainDataIndex[i]];
        }

    }

    public void train() {
        boolean[] isCategory = trainSet.getIsCategory();

        try {
            classifier = (Classifier) Class.forName("auxiliary." + clsName).newInstance();
            classifier.train(isCategory, shuffleTrainFeatures, shuffleTrainLabels);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            System.out.println("");
        }
    }

    public void val(DataSet testSet) {
        boolean[] isCategory = trainSet.getIsCategory();
        boolean isClassification = isCategory[isCategory.length - 1];

        double[][] testFeatures = testSet.getFeatures();
        double[] testLabels = testSet.getLabels();

        double error = 0;
        for (int j = 0; j < testFeatures.length; j++) {
            double prediction = classifier.predict(testFeatures[j]);
            if (isClassification) {
                if (prediction != testLabels[j]) {
                    error = error + 1;
                }
            } else {
                error = error + (prediction - testLabels[j]) * (prediction - testLabels[j]);
            }
        }

        System.out.println((testFeatures.length - error) / testFeatures.length);
    }

    private void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    private void shuffle(int[] arr) {
        Random rand = new Random();
        int length = arr.length;
        for (int i = length; i > 0; i--) {
            int randInd = rand.nextInt(i);
            swap(arr, randInd, i - 1);
        }
    }
}
