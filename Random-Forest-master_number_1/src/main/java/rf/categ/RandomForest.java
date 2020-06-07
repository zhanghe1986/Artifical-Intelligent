package rf.categ;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class RandomForest {
	
	/** the number of threads to use when generating the forest */
	private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
	//private static final int NUM_THREADS = 1;
	/** the number of attributes in the data - set this before beginning the forest creation */
	public static int M;
	/** Of the M total attributes, the random forest computation requires a subset of them
	 * to be used and picked via random selection. "Ms" is the number of attributes in this
	 * subset. The formula used to generate Ms was recommended on Breiman's website.
	 */
	public static int Ms;//recommended by Breiman: =(int)Math.round(Math.log(M)/Math.log(2)+1);
	/** the collection of the forest's decision trees */
	private ArrayList<DecisionTree> decisionTrees;
	/** the number of trees in this random tree */
	private int numTrees;
	/** the thread pool that controls the generation of the decision trees */
	private ExecutorService treePool;
	/** the original training data matrix that will be used to generate the random forest classifier */
	private ArrayList<ArrayList<String>> trainData;
	/** the data on which produced random forest will be tested*/
	private ArrayList<ArrayList<String>> testData;
	/** This holds all of the predictions of trees in a Forest */
	private ArrayList<ArrayList<String>> prediction;
	
	/**
	 * Initializes a Breiman random forest creation
	 * @param numTrees			the number of trees in the forest
	 */
	public RandomForest(int numTrees, int M, int Ms, ArrayList<ArrayList<String>> trainData, ArrayList<ArrayList<String>> testData) {
		this.numTrees = numTrees;
		this.trainData = trainData;
		this.testData = testData;
		this.M = M;
		this.Ms = Ms;
        decisionTrees = new ArrayList<DecisionTree>(numTrees);
        prediction = new ArrayList<ArrayList<String>>();
	}

	/**
	 * Begins the random forest creation
	 */
	public void train() {
	    long begin = System.currentTimeMillis();
        System.out.println("start to train...");
		System.out.println("Number of threads started : " + NUM_THREADS);
		treePool = Executors.newFixedThreadPool(NUM_THREADS);
		for (int t = 0; t < numTrees; t++){
			treePool.execute(new CreateDecisionTree(trainData,this,t+1));
		}
		treePool.shutdown();
		try {	         
			treePool.awaitTermination(Long.MAX_VALUE,TimeUnit.SECONDS); //effectively infinity
	    } catch (InterruptedException ignored){
	    	System.out.println("interrupted exception in Random Forests");
	    }

        long end = System.currentTimeMillis();
        long time = end - begin;
        System.out.println("train time is :" + time);

	    testForest(decisionTrees, trainData, testData);
        end = System.currentTimeMillis();
        time = end - begin;
        System.out.println("total time is :" + time);
		System.out.print("end to train...");
	}

	/**
	 * Predicting unlabeled data
	 * @param testdata
	 */
	public void eval(ArrayList<ArrayList<String>> testdata) {
		ArrayList<String> TestResult = new ArrayList<String>();
		System.out.println("Predicting Labels now");
		for(ArrayList<String> data : testdata){
			ArrayList<String> Predict = new ArrayList<String>();
			for(DecisionTree decisionTree : decisionTrees){
				Predict.add(decisionTree.evaluate(data));
			}
			TestResult.add(ModeofList(Predict));
		}
		System.out.println(TestResult);
	}

	/**
	 * Testing the forest using the test-data
	 * 
	 */
	private void testForest(ArrayList<DecisionTree> trees, ArrayList<ArrayList<String>> train, ArrayList<ArrayList<String>> test){
		int correctness=0;ArrayList<String> ActualValues = new ArrayList<String>();
		
		for(ArrayList<String> s : test){
			ActualValues.add(s.get(s.size()-1));
		}
		System.out.println("Testing forest now ");
		
		for(DecisionTree DTC : trees){
            ArrayList<String> calculatedLabel = DTC.calculateLabel(test);
			if(!CollectionUtils.isEmpty(calculatedLabel))
				prediction.add(calculatedLabel);
		}
		for(int i = 0;i<test.size();i++){
			ArrayList<String> Val = new ArrayList<String>();
			for(int j=0;j<trees.size();j++){
				Val.add(prediction.get(j).get(i));
			}
			String pred = ModeofList(Val);
			if(pred.equalsIgnoreCase(ActualValues.get(i))){
				correctness = correctness +1;
			}
		}
		System.out.println("The Result of Predictions :-");
		System.out.println("Total Cases : "+test.size());
		System.out.println("Total Correct Predicitions  : "+correctness);
		System.out.println("Forest Accuracy :"+(correctness*100/test.size())+"%");				
	}
	/**
	 * To find the final prediction of the trees
	 * 
	 * @param predictions
	 * @return the mode of the list
	 */
	public String ModeofList(ArrayList<String> predictions) {
		// TODO Auto-generated method stub
		String MaxValue = null; int MaxCount = 0;
		for(int i=0;i<predictions.size();i++){
			int count=0;
			for(int j=0;j<predictions.size();j++){
				if(predictions.get(j).trim().equalsIgnoreCase(predictions.get(i).trim()))
					count++;
				if(count>MaxCount){
					MaxValue=predictions.get(i);
					MaxCount=count;
				}
			}
		}return MaxValue;
	}
	/**
	 * This class houses the machinery to generate one decision tree in a thread pool environment.
	 *
	 */
	private class CreateDecisionTree implements Runnable{
		/** the training data to generate the decision tree (same for all trees) */
		private ArrayList<ArrayList<String>> data;
		/** the Tree number */
		private int treenum;
		/**
		 * A default constructor
		 */
		public CreateDecisionTree(ArrayList<ArrayList<String>> data, RandomForest forest, int num){
			this.data = data;
			this.treenum = num;
		}
		/**
		 * Creates the decision tree
		 */
		public void run() {
            decisionTrees.add(new DecisionTree(data, treenum));
		}
	}
}
