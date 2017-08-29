package KScope.Code;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.wlu.cs.levy.CG.KDTree;

public class TrainFeature {

	private int intersectionCount;

	public void train(KDTree test, BufferedReader br, List<double[]> equationList, int kmerToDo)
			throws Exception {
		String id = "";
		String line = "";
		HashMap<double[], HashMap<String, Integer>> sameMap = new HashMap<double[], HashMap<String, Integer>>();
		String[] arr = new String[0];
		String[] coordArr = new String[0];
		intersectionCount = 0;

		while ((line = br.readLine()) != null) {
			/** INSERTING INTO TREE OR STORAGE VECTOR **/
			// read all the coords from the feature file
			arr = line.split("~~");
			coordArr = arr[0].split(",");
			
			
			//  turn the string coords into doubles
			double[] coord = new double[coordArr.length];
			for(int i = 0; i < coordArr.length; i ++){
				coord[i] = Double.valueOf(coordArr[i]);
			}
			//  get the ID from the feature file
			id = arr[1];
			
			/** if a search at the coord yields nothing **/
			if (test.search(coord) == null) {
				test.insert(coord, id);
				/** put into training data **/
			} else if (test.search(coord) != null) {
				intersectionCount++;
				/**
				 * if intersection then write to the test file that will be
				 * broken up into smaller 100k files later
				 **/
				if (sameMap.containsKey(coord)) {
					HashMap<String, Integer> IDMap = sameMap.get(coord);
					String targetID = test.search(coord).toString();
					if (IDMap.containsKey(targetID)) {
						IDMap.put(targetID, IDMap.get(targetID) + 1);
					} else {
						IDMap.put(targetID, 1);
					}
				} else {
					sameMap.put(coord, new HashMap<String, Integer>());
					sameMap.get(coord).put(test.search(coord).toString(), 1);
				}

			}
		}
		System.out.println("finished initial tree inserts");
		System.gc();
		System.out.println("beginning secondary tree inserts");
		
		// go through sameMap and make the most popular part of the tree.
		// eliminate less popular
		for (double[] coords : sameMap.keySet()) {
			HashMap<String, Integer> IDMap = sameMap.get(coords);
			int max = 1;
			String maxString = "";
			for (String key : IDMap.keySet()) {
				if (IDMap.get(key) > max) {
					max = IDMap.get(key);
					maxString = key;
				}
			}
			if (max > 1 && !maxString.equals("")) {
				test.insert(coords, maxString);
			}
		}
	}
	
	public int getIntersectionCount(){
		return this.intersectionCount;
	}

}
