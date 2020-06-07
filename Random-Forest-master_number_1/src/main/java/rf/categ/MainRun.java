package rf.categ;

import java.util.ArrayList;
import java.util.HashMap;

public class MainRun {

	public static void main(String[] args){
		String trainDataFile = "./data/train1.data";
		String testDataFile = "./data/test1.data";
		String evalDataFile = "./data/test1_1.data";
		
		DataSet dataSet = new DataSet();
		ArrayList<ArrayList<String>> trainData = dataSet.loadData(trainDataFile);
		ArrayList<ArrayList<String>> testData = dataSet.loadData(testDataFile);
		//ArrayList<ArrayList<String>> evalData = dataSet.loadData(evalDataFile);
		
		int numTrees=100;
		int M = trainData.get(0).size()-1;
		int Ms = (int)Math.round(Math.log(M)/Math.log(2)+1);

        System.out.println("creating " + numTrees + " trees in a random Forest. . . ");
        System.out.println("total train data size is " + trainData.size());
        System.out.println("number of attributes " + M);
        System.out.println("number of selected attributes " + Ms);

		RandomForest rf = new RandomForest(numTrees, M, Ms, trainData, testData);
		rf.train();
		//rf.eval(evalData);
	}
}
