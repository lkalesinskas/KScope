package KScope.Code;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;

import edu.wlu.cs.levy.CG.KDTree;

public class TrainFasta {
	
	private int intersectionCount;

	public int getIntersectionCount(){
		return this.intersectionCount;
	}

	public void train(KDTree test, BufferedReader br, List<double[]> equationList, int kmerToDo, boolean fastatofeature, String TrainFile) throws Exception{
		String line = "";
		String id = "";
		String sequence = "";
		boolean first = true;
		int count = 0;
		intersectionCount = 0;
		HashMap<double[],HashMap<String, Integer>> sameMap = new HashMap<double[], HashMap<String, Integer>>();
		BufferedWriter bw = new BufferedWriter(new FileWriter(TrainFile+".feature"));
		if(fastatofeature) System.out.println("converting to feature file");
//		BufferedWriter trainWriter = new BufferedWriter(new FileWriter("TrainOut93merecoli.fasta"));
//		BufferedWriter testWriter = new BufferedWriter(new FileWriter("TestOut93merecoli.fasta"));
		
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
					String origSequence = sequence;
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
						if(fastatofeature){
							bw.write(coord1[c].toString());
							if(c + 1 != coord1.length){
								bw.write(",");
							}
						}
					}
					if(fastatofeature){
						bw.write("~~"+id+"\n");
					}
					
//					sequenceMap.put(coord, origSequence);
//					for(int v = 0; v < equationList.size(); v ++){
//						coordArr[v] = KDTOnlyMain.getPCAX(gene, equationList.get(v));
//					}
					/**   if a search at the coord yields nothing  **/
					if(test.search(coord) == null){
						test.insert(coord, id);
//						trainWriter.write(id+"\n");
//						String[] splitSeq = origSequence.split("(?<=\\G.{70})");
//						for(String str : splitSeq){
//							trainWriter.write(str+"\n");
//						}
					}
					//  if an intersection then insert into sameMap for later possible reinsertion
					else if(test.search(coord) != null){
						intersectionCount ++;
						
//						testWriter.write(id+"\n");
//						String[] splitSeq = origSequence.split("(?<=\\G.{70})");
//						for(String str : splitSeq){
//							testWriter.write(str+"\n");
//						}
						
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
		
//		testWriter.close();
//		trainWriter.close();
		bw.close();
		if(fastatofeature) System.out.println("done converting");

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