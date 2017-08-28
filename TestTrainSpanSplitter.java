package KScope;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;

public class TestTrainSpanSplitter {
	
	static int kmerToDo = 2;
	
	
	public static void main(String[] args) throws IOException, KeySizeException, KeyDuplicateException{
		BufferedReader setReader = new BufferedReader(new FileReader("testSet.txt"));
		BufferedWriter trainSpanWriter = new BufferedWriter(new FileWriter("TrainOut8Span2merPCA.fasta"));
		BufferedWriter testSpanWriter = new BufferedWriter(new FileWriter("TestOut8Span2merPCA.fasta"));
		
		
		BufferedReader bufferedReader = new BufferedReader(new FileReader("kmer2PCA"));
		String line = "";
		int count = 0;
		
		int intersectionCount = 0;
		
		//  reading and parsing PCA equations
		List<double[]> equationList = new ArrayList<double[]>();
		while ((line = bufferedReader.readLine()) != null) {
			equationList.add(KDTOnlyMain.parsePCAText(line));
		}
			bufferedReader.close();
			
			KDTree test = new KDTree(equationList.size());
			
		String id = "";
		String sequence = "";
		System.out.println("beginning inserts into tree");
		while((line = setReader.readLine()) != null){
			id = line;
			sequence = setReader.readLine();
			if(id.contains("hypothetical") || id.contains("Hypothetical") || id.contains("USS-DB") || sequence.length() < 100){
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
				
				//  write to train file
				trainSpanWriter.write(id+"\n");
				String[] splitSeq = sequence.split("(?<=\\G.{70})");
				for(String str : splitSeq){
					trainSpanWriter.write(str+"\n");
				}
				
			}
			else if(test.search(coord) != null){
				intersectionCount ++;
				
				//  write to test file
				testSpanWriter.write(id+"\n");
				String[] splitSeq = sequence.split("(?<=\\G.{70})");
				for(String str : splitSeq){
					testSpanWriter.write(str+"\n");
				}
			}
			
		}
		setReader.close();
		testSpanWriter.close();
		trainSpanWriter.close();
		System.out.println("tree size " + test.size());
		System.out.println("intersections " + intersectionCount);
		System.out.println("done inserting into tree");
	}

}
