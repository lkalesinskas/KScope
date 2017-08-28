//package KScope;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class FastaToFeatureFile {
//	
//public static void fastaToFeature(String optionValue, String equationFile, int kmerToDo) throws IOException {
//	
//	System.out.println("converting to feature file");
//		
//		BufferedReader bufferedReader = new BufferedReader(new FileReader(equationFile));
//		String line = "";
//		
//		//  reading and parsing PCA equations
//		List<double[]> equationList = new ArrayList<double[]>();
//		while ((line = bufferedReader.readLine()) != null) {
//			equationList.add(KDTOnlyMain.parsePCAText(line));
//		}
//		bufferedReader.close();
//			
//			
//		BufferedReader br = new BufferedReader(new FileReader(optionValue));
//		BufferedWriter bw = new BufferedWriter(new FileWriter(optionValue+".feature"));
//		String id = "";
//		String sequence = "";
//		boolean first = true;
//		while((line = br.readLine()) != null){
//			if(line.contains(">")){
//				if(first){
//					id = line;
//					first = false;
//					continue;
//				}
//				else{
//					
//					if(id.contains("hypothetical") || id.contains("Hypothetical") || id.contains("USS-DB") || sequence.length() < 100){
//						sequence = "";
//						id=line;
//						continue;
//					}
//					// fix sequence to have replaced length and unwanted parts removed
//					sequence = KDTOnlyMain.replaceNucs(sequence);
//					sequence = sequence.substring(60, sequence.length() - 2);
//					double[] gene = KDTOnlyMain.processSequencebyKmer(sequence, kmerToDo);
//					double sumGene = 0.0;
//					for(int i2 = 0; i2 < gene.length; i2++){
//						sumGene+=gene[i2];
//					}
//					for(int i2 = 0; i2 < gene.length; i2++){
//						gene[i2] = gene[i2]/sumGene;
//					}
//					
//					Double[] coordArr = new Double[equationList.size()];
//					//  get the coordinates for each point
//					for(int v = 0; v < equationList.size(); v ++){
//						coordArr[v] = KDTOnlyMain.getPCAX(gene, equationList.get(v));
//						bw.write(Double.toString(coordArr[v]));
//						if(v+1 != equationList.size()){
//							bw.write(",");
//						}
//					}
//					bw.write("~~"+id.replaceAll(",", "")+"\n");
//					
//					
//					sequence = "";
//					id = line;
//					
//				}
//			}
//			else{
//				sequence += line;
//			}
//		}
//		br.close();
//		bw.close();
//		System.out.println("done converting");
//	}
//
//}


package KScope;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.wlu.cs.levy.CG.KDTree;

public class FastaToFeatureFile{
	

	public static void fastaToFeature(String optionValue, String equationFile, int kmerToDo) throws Exception{
		String line = "";
		String id = "";
		String sequence = "";
		boolean first = true;
		int count = 0;
		BufferedReader bufferedReader = new BufferedReader(new FileReader(equationFile));
		
		//  reading and parsing PCA equations
		List<double[]> equationList = new ArrayList<double[]>();
		while ((line = bufferedReader.readLine()) != null) {
			equationList.add(KDTOnlyMain.parsePCAText(line));
		}
		bufferedReader.close();
		HashMap<double[],HashMap<String, Integer>> sameMap = new HashMap<double[], HashMap<String, Integer>>();
		BufferedReader br = new BufferedReader(new FileReader(optionValue));
		BufferedWriter trainWriter = new BufferedWriter(new FileWriter(optionValue+".feature"));
//		BufferedWriter trainWriter = new BufferedWriter(new FileWriter("TrainOut93merecoli.fasta"));
//		BufferedWriter testWriter = new BufferedWriter(new FileWriter("TestOut93merecoli.fasta"));
		System.out.println("converting to feature file");
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
						trainWriter.write(coord1[c].toString());
						if(c + 1 != coord.length){
							trainWriter.write(",");
						}
					}
					
					trainWriter.write("~~"+id.replaceAll(",", "") + "\n");
//					sequenceMap.put(coord, origSequence);
//					for(int v = 0; v < equationList.size(); v ++){
//						coordArr[v] = KDTOnlyMain.getPCAX(gene, equationList.get(v));
//					}
					/**   if a search at the coord yields nothing  **/
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
		trainWriter.close();

/**   TREE/STORAGE VECTOR INSERTIONS FINISHED   **/
			System.out.println("finished initial tree inserts");
			System.gc();
			System.out.println("beginning secondary tree inserts");
//			BufferedWriter intersectWriter = new BufferedWriter(new FileWriter("test intersects.csv"));
			
			//  go through sameMap and make the most popular part of the tree.  eliminate less popular
//			intersectWriter.close();
	}

}

