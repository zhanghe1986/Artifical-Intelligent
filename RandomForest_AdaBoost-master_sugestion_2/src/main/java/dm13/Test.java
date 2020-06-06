/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dm13;

import auxiliary.DataSet;
import auxiliary.ClassifierController;

/**
 *
 * @author daq
 */
public class Test {

    public static void main(String[] args) {
        // for RandomForest
        System.out.println("for RandomForest");

        String trainSetFile = "./data/train1.data";
        String testSetFile = "./data/test1.data";
        DataSet trainSet = new DataSet(trainSetFile);
        DataSet testSet = new DataSet(testSetFile);

        ClassifierController eva = new ClassifierController(trainSet, "RandomForest");
        eva.dataHandler();
        eva.train();
        eva.val(testSet);

        ClassifierController eva2 = new ClassifierController(trainSet, "AdaBoost");
        eva2.dataHandler();
        eva2.train();
        eva2.val(testSet);
    }
}
