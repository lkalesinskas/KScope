package KScope;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.List;

import edu.wlu.cs.levy.CG.KDTree;

public class TrainFasta implements TrainFile {

	public void train(KDTree test, int intersectionCount, BufferedReader br, List<double[]> equationList, int kmerToDo) throws Exception{
		String line = "";
		String id = "";
		String sequence = "";
		boolean first = true;
		int count = 0;
		HashMap<double[],HashMap<String, Integer>> sameMap = new HashMap<double[], HashMap<String, Integer>>();
		/**  the writers for train and test out that will be used for the spanning set   **/
		while( (line = br.readLine()) != null){

/**   INSERTING INTO TREE OR STORAGE VECTOR    **/
			//  dealing with the first line
			if(line.contains(">")){
				if(first){
					id = line;
					first = false;
					continue;
				}
				else{
					//  filtering unwanted sequences
					if(id.contains("hypothetical") || id.contains("Hypothetical") || id.contains("USS-DB") || sequence.length() < 100){
						sequence = "";
						id=line;
						continue;
					}
					// fix sequence to have replaced length and unwanted parts removed
					sequence = KDTOnlyMain.replaceNucs(sequence);
					sequence = sequence.substring(60, sequence.length() - 2);
					double[] gene = KDTOnlyMain.processSequencebyKmer(sequence, kmerToDo);
					double sumGene = 0.0;
					for(int i2 = 0; i2 < gene.length; i2++){
						sumGene+=gene[i2];
					}
					for(int i2 = 0; i2 < gene.length; i2++){
						gene[i2] = gene[i2]/sumGene;
					}
					
					Double[] coordArr = new Double[equationList.size()];
					//  get the coordinates for each point
					for(int v = 0; v < equationList.size(); v ++){
						coordArr[v] = KDTOnlyMain.getPCAX(gene, equationList.get(v));
					}
					
					
					//  convert the coords calculated into legacy code
					Double[] coord1 = coordArr;
					double[] coord = new double[coord1.length];
					for(int c = 0; c < coord1.length; c ++){
						coord[c] = coord1[c];
					}
					
					
					for(int v = 0; v < equationList.size(); v ++){
						coordArr[v] = KDTOnlyMain.getPCAX(gene, equationList.get(v));
					}
					/**   if a search at the coord yields nothing  **/
					if(test.search(coord) == null){
						test.insert(coord, id);
					}
					//  if an intersection then insert into sameMap for later possible reinsertion
					else if(test.search(coord) != null){
						intersectionCount ++;
						//  sameMap will be for when there might be points that are at the same point and might have multiple of the same thing at the same point
						//  will later make what is most popular at the point what is in the tree
						if(sameMap.containsKey(coord)){
							HashMap<String, Integer> IDMap = sameMap.get(coord);
							String targetID = test.search(coord).toString();
							if(IDMap.containsKey(targetID)){
								IDMap.put(targetID, IDMap.get(targetID) + 1);
							}
							else{
								IDMap.put(targetID, 1);
							}
						}
						else{
							sameMap.put(coord, new HashMap<String, Integer>());
							sameMap.get(coord).put(test.search(coord).toString(), 1);
						}
					}
					count++;
					sequence = "";
					id = line;
				}

			}
			else{
				sequence += line;
			}
		}

/**   TREE/STORAGE VECTOR INSERTIONS FINISHED   **/
			System.out.println("finished initial tree inserts");
			System.gc();
			System.out.println("beginning secondary tree inserts");
//			BufferedWriter intersectWriter = new BufferedWriter(new FileWriter("test intersects.csv"));
			
			//  go through sameMap and make the most popular part of the tree.  eliminate less popular
			for(double[] coords : sameMap.keySet()){
				HashMap<String, Integer> IDMap = sameMap.get(coords);
				int max = 1;
				String maxString = "";
				for(String key : IDMap.keySet()){
					if(IDMap.get(key) > max){
						max = IDMap.get(key);
						maxString = key;
					}
				}
				if(max > 1 && !maxString.equals("")){
					test.insert(coords, maxString);
				}
			}
//			intersectWriter.close();
	}

}
