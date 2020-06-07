package rf.categ;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class DataSet {

	public DataSet(){
	}

	public ArrayList<ArrayList<String>> loadData(String path){
		BufferedReader bufferedReader = null;
		ArrayList<ArrayList<String>> dataInput = new ArrayList<ArrayList<String>>();

		try {
			String currentLine;
			bufferedReader = new BufferedReader(new FileReader(path));
			while ((currentLine = bufferedReader.readLine()) != null) {
				ArrayList<Integer> Sp=new ArrayList<Integer>();int i;
				ArrayList<String> dataPointsArray = new ArrayList<>();
				String[] dataPoints = currentLine.split(",");
				Collections.addAll(dataPointsArray, dataPoints);
				dataInput.add(dataPointsArray);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return dataInput;
	}
}
