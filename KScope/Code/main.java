package KScope.Code;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.math3.stat.correlation.KendallsCorrelation;

import com.metsci.glimpse.examples.Example;
import com.metsci.glimpse.examples.line.LinePlotExample;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeySizeException;

/**
 * 
 * @author Larry Kalesinskas
 *
 */
public class main {
	private static int TOTAL_VALS = 3000000;
	public static int nmer = 0;
	public static int kmerMax = 9;
	public static int numShifts = 0;
	public static int numShiftsMinus = 0;
	public static int kmerToDo = 0;

	public static void main(String[] args) throws Exception {

		/** for loop for executing the entire program several times for different kmer sizes  **/
//		for(int kmerSize = 2; kmerSize <= 6; kmerSize ++){
		// kmer size we are using
//		kmerToDo = kmerSize;
		kmerToDo = 3;

		// Files for Axis
		File genome1 = new File("Genomes\\Genome1.fna");
		File genome2 = new File("Genomes\\Genome2.fna");
		File genome3 = new File("Genomes\\GCF_000160075.2_ASM16007v2_genomic.fna");
		File genome4 = new File("Genomes\\GCF_000376245.1_ASM37624v1_genomic.fna");
		File genome5 = new File("Genomes\\GCF_000018105.1_ASM1810v1_genomic.fna");

		// this is the training data for the models
		File geneFile = new File("TrainOut.ffn");

		// Getting sequence
		String sequence1 = inputGenomeSequence(genome1);
		String sequence2 = inputGenomeSequence(genome2);
//		String sequence3 = inputGenomeSequence(genome3);  // z axis
//		String sequence4 = inputGenomeSequence(genome4);  // q axis
//		String sequence5 = inputGenomeSequence(genome5);  // r axis
		// Gets KmerComposition Arrays of each Axis
//		double[] xAxis = processSequencebyKmer(sequence1, kmerToDo);
//		double[] yAxis = processSequencebyKmer(sequence2, kmerToDo);
//		double[] zAxis = processSequencebyKmer(sequence3, kmerToDo);
//		double[] qAxis = processSequencebyKmer(sequence4, kmerToDo);
//		double[] rAxis = processSequencebyKmer(sequence5, kmerToDo);

		// ensure the kmer composition processing worked
//		for (int i = 0; i < xAxis.length; i++) {
//			System.out.print(yAxis[i] + ", ");
//		}
//		System.out.println();
//		for (int i = 0; i < xAxis.length; i++) {
//			System.out.print(xAxis[i] + ", ");
//		}
//		System.out.println();
//		for (int i = 0; i < zAxis.length; i++) {
//			System.out.print(zAxis[i] + ", ");
//		}
//		System.out.println();
//		for (int i = 0; i < qAxis.length; i++) {
//			System.out.print(qAxis[i] + ", ");
//		}
//		System.out.println();
//		for (int i = 0; i < rAxis.length; i++) {
//			System.out.print(rAxis[i] + ", ");
//		}
//		System.out.println();
		
		// Inputs and Stores all genes in storage vector
		System.out.println("Inputting Figgies");
		Vector<Gene> storage = InputAndProcessGenesCategory(geneFile);
		
		System.out.println("Making KD Tree");
		System.out.println("Reading Equations");
		BufferedReader bufferedReader = new BufferedReader(new FileReader("percentage3merPCA.txt"));
		String line = "";
		List<double[]> equationList = new ArrayList<double[]>();
		while ((line = bufferedReader.readLine()) != null) {
			equationList.add(parsePCAText(line));
		}
		bufferedReader.close();
		
		KDTree test = new KDTree(equationList.size());
		HashMap<String, Integer> pegSet = new HashMap<String, Integer>();
		int inSet = 0;
		int outSet = 0;
		int intersectionCount = 0;
		HashMap<String, List<double[]>> clusterMap = new HashMap<String, List<double[]>>();
		HashMap<String, List<String>> intersectionMap = new HashMap<String, List<String>>();
		System.out.println("Correlating");
		
//		BufferedWriter bw = new BufferedWriter(new FileWriter("Intersections.csv"));
//		bw.write("KDT value, Values Intersecting,\n");
		//  reading equations
		
		
		
		
		
		for (int i = 1; i < storage.size(); i++) {

			// storing the kmer composition of the gene
			double[] gene = storage.get(i).kmerVector.clone();
			double sumGene = 0.0;
			for(int i2 = 0; i2 < gene.length; i2++){
				sumGene+=gene[i2];
			}
			for(int i2 = 0; i2 < gene.length; i2++){
				gene[i2] = gene[i2]/sumGene;
			}
//			double[] gene2 = storage.get(i).kmerVector.clone();
//			double[] gene3 = storage.get(i).kmerVector.clone();
//			double[] gene4 = storage.get(i).kmerVector.clone();
//			double[] gene5 = storage.get(i).kmerVector.clone();
//			double[] gene6 = storage.get(i).kmerVector.clone();
//			double[] gene7 = storage.get(i).kmerVector.clone();

			// clone the x and y axis
//			double[] x = xAxis.clone();
//			double[] y = yAxis.clone();
//			double[] z = zAxis.clone();
//			double[] q = qAxis.clone();
//			double[] r = rAxis.clone();
			// adding them to plane by storing them as a number
			// adding them to plane
			
			Double[] coordArr = new Double[equationList.size()];
			for(int v = 0; v < equationList.size(); v ++){
				coordArr[v] = getPCAX(gene, equationList.get(v));
			}
			
//			double[] x = parsePCAText("-0.28kmer5-0.28kmer15-0.279kmer12-0.279kmer2-0.278kmer9-0.278kmer14-0.277kmer3-0.277kmer8-0.237kmer4-0.228kmer1-0.228kmer16-0.224kmer6-0.224kmer11-0.209kmer10-0.205kmer13-0.182kmer7");
//			double[] y = parsePCAText("-0.417kmer7+0.375kmer13-0.369kmer10-0.339kmer11-0.339kmer6+0.336kmer1+0.336kmer16+0.293kmer4+0.037kmer3+0.037kmer8-0.035kmer9-0.035kmer14+0.021kmer5+0.021kmer15-0.012kmer12-0.012kmer2");
//			double[] z = parsePCAText("-0.436kmer7+0.418kmer3+0.418kmer8-0.374kmer13-0.331kmer4-0.27kmer10+0.225kmer9+0.225kmer14-0.108kmer12-0.108kmer2-0.074kmer16-0.074kmer1+0.035kmer5+0.035kmer15+0.019kmer6+0.019kmer11");
//			double[] q	= parsePCAText("-0.388kmer15-0.388kmer5+0.362kmer9+0.362kmer14-0.342kmer2-0.342kmer12+0.336kmer7+0.181kmer4+0.123kmer16+0.123kmer1+0.08 kmer10+0.075kmer8+0.075kmer3+0.064kmer13-0.055kmer6-0.055kmer11");
//			double[] r = parsePCAText("0.44 kmer6+0.44 kmer11-0.331kmer12-0.331kmer2-0.283kmer7-0.281kmer14-0.281kmer9+0.276kmer13+0.129kmer8+0.129kmer3+0.128kmer4+0.124kmer10+0.074kmer1+0.074kmer16-0.041kmer15-0.041kmer5");
//			double[] s = parsePCAText("-0.611kmer10+0.34 kmer11+0.34 kmer6+0.226kmer2+0.226kmer12-0.223kmer5-0.223kmer15-0.202kmer3-0.202kmer8+0.178kmer9+0.178kmer14+0.146kmer13-0.093kmer16-0.093kmer1-0.086kmer7+0.064kmer4");
//			double[] t = parsePCAText("0.42 kmer4-0.406kmer13+0.335kmer5+0.335kmer15-0.309kmer8-0.309kmer3-0.256kmer12-0.256kmer2+0.191kmer9+0.191kmer14-0.142kmer7+0.078kmer11+0.078kmer6-0.07kmer10+0.02 kmer16+0.02 kmer1");
//			double[] x2 = parsePCAText("0.548kmer16+0.548kmer1-0.387kmer4-0.379kmer13-0.138kmer3-0.138kmer8+0.125kmer11+0.125kmer6-0.083kmer14-0.083kmer9-0.082kmer5-0.082kmer15+0.075kmer7+0.046kmer2+0.046kmer12-0.032kmer10");
//			
			
//			Double a = getPCAX(gene, x);
//			Double b = getPCAY(gene, y);
//			Double c = getPCAX(gene, z);
//			Double d = getPCAX(gene, q);
//			Double e = getPCAX(gene, r);
//			Double f = getPCAX(gene, s);
//			Double g = getPCAX(gene, t);
//			Double h = getPCAX(gene, x2);
//			double a = getR(gene, x);
//			double b = getR(gene2, y);
//			double c = getR(gene3, z);
//			double d = getR(gene4, q);
//			double e = getR(gene5, r);
//			storage.get(i).x = a;
//			storage.get(i).y = b;
//			storage.get(i).z = c;
//			storage.get(i).q = d;
//			storage.get(i).r = e;
//			storage.get(i).s = f;
//			storage.get(i).t = g;
//			storage.get(i).x2 = h;
			
			// turn the x and y from above into an ordered pair
			Double[] coord1 = coordArr;
			double[] coord = new double[coord1.length];
			for(int c = 0; c < coord1.length; c ++){
				coord[c] = coord1[c];
			}
			
//			double[] coord = { a, b, c, d, e,f,g,h };
			
			if(!pegSet.containsKey(storage.get(i).Cog)){
				pegSet.put(storage.get(i).Cog, 1);
			}
			else{
				int whatever = pegSet.get(storage.get(i).Cog);
				pegSet.put(storage.get(i).Cog, whatever + 1);
			}

			// if the coordinate isn't in the KDTree, insert into tree
			if (test.search(coord) == null) {
				test.insert(coord, storage.get(i).Cog);

				// if a point has a similar cog value then they are part of a
				// cluster
				String clusterkey = storage.get(i).Cog;
//				String clusterkey = storage.get(i).Cog;
				if (clusterMap.containsKey(clusterkey)) {
//					clusterMap.get(clusterkey).add(new double[] { a, b, c, d, e,f,g,h });
					clusterMap.get(clusterkey).add(coord);
				}
				// if no cog value in the map
				else {
					clusterMap.put(clusterkey, new ArrayList<double[]>());
					clusterMap.get(clusterkey).add(coord);
				}
				
				intersectionMap.put(storage.get(i).Cog, new ArrayList<String>());
			}

			/*
			 * DONE: go through and take the cog value and use it as a key for
			 * an hmap. check to see if the cog is in an hmap. store any
			 * coordinates in a list as the values being returned by the
			 * hmap.get(key)
			 */

			// if it is in the tree, then count the number of intersections
			else if (test.search(coord) != null) {
				// System.out.println(test.search(coord) + ", " +
				// storage.get(i).Cog);
				intersectionMap.get(test.search(coord)).add(storage.get(i).Cog);
				intersectionCount++;
			}
		}
		
//		for(String key : intersectionMap.keySet()){
//			bw.write(key + "," );
//			List<String> intersectValList = intersectionMap.get(key);
//			for(String val : intersectValList){
//				if(intersectValList.size() > 0 || !intersectValList.isEmpty())
//					bw.write(val+"   ");
//			}
//			bw.write(",\n");
//		}
//		bw.close();
//		KScopeGraph graph = new KScopeGraph(TOTAL_VALS, storage);
//		KScopeGraph graph = new KScopeGraph(1000, storage);
//		Example.showWithSwing( graph );

		System.out.println("KDTree size " + test.size());
		System.out.println("intersection count " + intersectionCount);
/**4  tightness of cluster start **/
//		HashMap<Integer, Double[]> centers = calculateClusterCenters(clusterMap);
//		HashMap<Integer, Double> averages = calculateAverageDistanceFromCenters(clusterMap, centers);
//		//HashMap<Integer, Double[]> medians = calculateMedianDistanceFromCenters(clusterMap, centers);
//
//		writeToCSV(clusterMap, centers, averages);
/**4  tightness of cluster end  **/
		
/**5       Finding distance between centers   **/		
//		PrintWriter centerDistanceWriter = new PrintWriter(new File("Distance_Centers.csv"));
//		HashMap<String, Double> centerDistanceMap = new HashMap<String, Double>();
//		Set<Integer> centerKeySet = centers.keySet();
//		List<Integer> centerKeyList = new ArrayList<Integer>(centerKeySet);
//		Object distanceMatrix[][] = new Object[centerKeyList.size()][centerKeyList.size()];
//		System.out.println("size of rows and columns " + centers.keySet().size());
//		for(int i = 0; i < centers.keySet().size(); i ++){
//			for(int j = 0; j < centers.keySet().size(); j ++){
//				if(j ==0 && i == 0){
//					centerDistanceWriter.write(',');
//					continue;   //  leave [0,0] empty
//				}
//				else if(j > 0 && i == 0){
//					
//					distanceMatrix[0][j] = centerKeyList.get(j);
//					distanceMatrix[j][0] = centerKeyList.get(j);
//					centerDistanceWriter.write(Integer.toString(centerKeyList.get(j)));
//					centerDistanceWriter.write(',');
//				}
//				else if(j > 0 && i > 0){
//					Double[] coord1 = centers.get(centerKeyList.get(i));
//					Double[] coord2 = centers.get(centerKeyList.get(j));
//					Double x1 = coord1[0];
//					Double y1 = coord1[1];
////					Double z1 = coord1[2];
////					Double q1 = coord1[3];
////					Double r1 = coord1[4];
//					Double x2 = coord2[0];
//					Double y2 = coord2[1];
////					Double z2 = coord2[2];
////					Double q2 = coord2[3];
////					Double r2 = coord2[4];
//					Double distance = Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) );
////					System.out.println("here is the distance: " + distance);
//					distanceMatrix[i][j] = distance;
//					distanceMatrix[j][i] = distance;
//					
//					centerDistanceWriter.write(Double.toString(distance));
//					centerDistanceWriter.write(',');
//				}
//			}
//			centerDistanceWriter.write('\n');
//			centerDistanceWriter.write(Integer.toString(centerKeyList.get(i)));
//			centerDistanceWriter.write(',');
//		}
//		for(int i = 0; i < centerKeyList.size(); i ++){
//			for(int j = 0; j < centerKeyList.size(); j ++){
//				if(distanceMatrix[i][j] != null)
//					centerDistanceWriter.write(distanceMatrix[i][j].toString());
//				centerDistanceWriter.write(',');
//			}
//			centerDistanceWriter.write('\n');
//		}
//		//for(Integer key : centers.keySet()){
//			//for(Integer key2 : centers.keySet()){
//				//Double[] coord1 = centers.get(key);
//				//Double[] coord2 = centers.get(key2);
//				//Double x1 = coord1[0];
//				//Double y1 = coord1[1];
//				//Double z1 = coord1[2];
//				//Double x2 = coord2[0];
//				//Double y2 = coord2[1];
//				//Double z2 = coord2[2];
//				//Double distance = Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) + (z2-z1)*(z2-z1));
//				//centerDistanceWriter.write(Integer.toString(key) + "<->" + Integer.toString(key2)+"," + Double.toString(distance));
//			//}
//		//}
//		
//		
////		for(String key : centerDistanceMap.keySet()){
////			centerDistanceWriter.write(key);
////			centerDistanceWriter.write(",");
////			centerDistanceWriter.write(Double.toString(centerDistanceMap.get(key)));
////			centerDistanceWriter.write('\n');
////		}
//		centerDistanceWriter.close();
		
/**5     Distance between centers end        **/
		 //KScopeGraph provider = new KScopeGraph( test.size(), storage);
		 //Example.showWithSwing( provider );
		/* DONE TRAINING MODEL */

		// testing the file of the subset of figs
		File testFile = new File("TestOut.ffn");
		Vector<Gene> testSequences = InputAndProcessGenesCategoryTest(testFile);
		System.out.println("We have " + testSequences.size() + " test sequences!");
		int miss = 0;
		int hit = 0;
		int nothingThere = 0;
		int searchPositive = 0;
		int searchNegative = 0;

//		PrintWriter pw = new PrintWriter(new File("Threshold.csv"));
//		PrintWriter pws = new PrintWriter(new File("Threshold_Small.csv"));
//		PrintWriter pwm = new PrintWriter(new File("Threshold_Medium.csv"));
//		PrintWriter sameCogWriterSmall = new PrintWriter(new File("Same_Cogs_Small.csv"));
//		PrintWriter sameCogWriterMed = new PrintWriter(new File("Same_Cogs_Medium.csv"));
//		PrintWriter nearWriter = new PrintWriter(new File("Nearest_Classification.csv"));
//		sameCogWriterSmall.write("peg #, # of same cogs");
//		sameCogWriterSmall.write('\n');
//		sameCogWriterMed.write("peg #, # of same cogs");
//		sameCogWriterMed.write('\n');
//		nearWriter.write("peg #, coords away");
//		nearWriter.write('\n');
		StringBuilder sb = new StringBuilder();
		sb.append("peg #");
		sb.append(',');
		sb.append("Other 0.0 - 0.001");
		sb.append(',');
		sb.append("Other 0.001 - 0.01");
		sb.append(',');
//		sb.append("Other 0.01 - 0.1");
//		sb.append(',');
//		sb.append("# of points with same CSV");
		sb.append('\n');
//		pw.write(sb.toString());
//		pws.write(sb.toString());
//		pwm.write("peg #, Other 0.0 - 0.01,");
//		pwm.write('\n');
		int filenum = 0;


		// takes kmer vector and creates the kmer count
		for (int i = 2; i < testSequences.size(); i++) {
			double[] gene = testSequences.get(i).kmerVector.clone();
			double sumGene = 0.0;
			for(int i2 = 0; i2 < gene.length; i2++){
				sumGene+=gene[i2];
			}
			for(int i2 = 0; i2 < gene.length; i2++){
				gene[i2] = gene[i2]/sumGene;
			}
//			double[] gene2 = testSequences.get(i).kmerVector.clone();
//			double[] gene3 = testSequences.get(i).kmerVector.clone();
//			double[] gene4 = testSequences.get(i).kmerVector.clone();
//			double[] gene5 = testSequences.get(i).kmerVector.clone();
//			double[] x = xAxis.clone();
//			double[] y = yAxis.clone();
//			double[] z = zAxis.clone();
//			double[] q = qAxis.clone();
//			double[] r = rAxis.clone();
			// adding them to plane
			Double[] coordArr = new Double[equationList.size()];
			for(int v = 0; v < equationList.size(); v ++){
				coordArr[v] = getPCAX(gene, equationList.get(v));
			}
			
//			double[] x = parsePCAText("-0.28kmer5-0.28kmer15-0.279kmer12-0.279kmer2-0.278kmer9-0.278kmer14-0.277kmer3-0.277kmer8-0.237kmer4-0.228kmer1-0.228kmer16-0.224kmer6-0.224kmer11-0.209kmer10-0.205kmer13-0.182kmer7");
//			double[] y = parsePCAText("-0.417kmer7+0.375kmer13-0.369kmer10-0.339kmer11-0.339kmer6+0.336kmer1+0.336kmer16+0.293kmer4+0.037kmer3+0.037kmer8-0.035kmer9-0.035kmer14+0.021kmer5+0.021kmer15-0.012kmer12-0.012kmer2");
//			double[] z = parsePCAText("-0.436kmer7+0.418kmer3+0.418kmer8-0.374kmer13-0.331kmer4-0.27kmer10+0.225kmer9+0.225kmer14-0.108kmer12-0.108kmer2-0.074kmer16-0.074kmer1+0.035kmer5+0.035kmer15+0.019kmer6+0.019kmer11");
//			double[] q	= parsePCAText("-0.388kmer15-0.388kmer5+0.362kmer9+0.362kmer14-0.342kmer2-0.342kmer12+0.336kmer7+0.181kmer4+0.123kmer16+0.123kmer1+0.08 kmer10+0.075kmer8+0.075kmer3+0.064kmer13-0.055kmer6-0.055kmer11");
//			double[] r = parsePCAText("0.44 kmer6+0.44 kmer11-0.331kmer12-0.331kmer2-0.283kmer7-0.281kmer14-0.281kmer9+0.276kmer13+0.129kmer8+0.129kmer3+0.128kmer4+0.124kmer10+0.074kmer1+0.074kmer16-0.041kmer15-0.041kmer5");
//			double[] s = parsePCAText("-0.611kmer10+0.34 kmer11+0.34 kmer6+0.226kmer2+0.226kmer12-0.223kmer5-0.223kmer15-0.202kmer3-0.202kmer8+0.178kmer9+0.178kmer14+0.146kmer13-0.093kmer16-0.093kmer1-0.086kmer7+0.064kmer4");
//			double[] t = parsePCAText("0.42 kmer4-0.406kmer13+0.335kmer5+0.335kmer15-0.309kmer8-0.309kmer3-0.256kmer12-0.256kmer2+0.191kmer9+0.191kmer14-0.142kmer7+0.078kmer11+0.078kmer6-0.07kmer10+0.02 kmer16+0.02 kmer1");
//			double[] x2 = parsePCAText("0.548kmer16+0.548kmer1-0.387kmer4-0.379kmer13-0.138kmer3-0.138kmer8+0.125kmer11+0.125kmer6-0.083kmer14-0.083kmer9-0.082kmer5-0.082kmer15+0.075kmer7+0.046kmer2+0.046kmer12-0.032kmer10");
//			
//			
//			Double a = getPCAX(gene, x);
//			Double b = getPCAY(gene, y);
//			Double c = getPCAX(gene, z);
//			Double d = getPCAX(gene, q);
//			Double e = getPCAX(gene, r);
//			Double f = getPCAX(gene, s);
//			Double g = getPCAX(gene, t);
//			Double h = getPCAX(gene, x2);
//			Double a = getR(gene,x);
//			Double b = getR(gene2,y);
//			Double c = getR(gene3, z);
//			Double d = getR(gene4, q);
//			Double e = getR(gene5, r);
			Double[] coord1 = coordArr;
			double[] coord = new double[coord1.length];
			for(int c = 0; c < coord1.length; c ++){
				coord[c] = coord1[c];
			}
			
//			double[] coord = new double[]{
//					a,b,c,d,e,f,g,h
//			};

			if(coordArr[0].isNaN() || coordArr[1].isNaN()) continue;
			// if it is not in the grid, look at the nearest one.
			if (test.search(coord) == null) {
				// if the classification is the same, then we are right
				// otherwise we are wrong
				if (test.nearest(coord).equals(testSequences.get(i).Cog)) {
					searchPositive++;
				} else {
/**1  nearest correct classification  start    **/
//					List<String> nearest = test.nearest(coord, 100);
//					for(int a1 = 0; a1 < nearest.size(); a1 ++){
////						System.out.println("near stats " + nearest.get(q));
//						if(testSequences.get(i).Cog.equals(nearest.get(a1))){
//							
//							nearWriter.write(nearest.get(a1) + " , " + Integer.toString(a1 + 1));
//							nearWriter.write('\n');
//							
//						}
//					}
/**1   nearest correct classification end     **/
/**3		nearest point sequence simularity    **/
					/*  USE NEAR WRITER */
					String nearestpeg = test.nearest(coord).toString();
					String currpeg = testSequences.get(i).Cog;
//					findNearestSequencesQ(currpeg, testFile, filenum);
//					findNearestSequencesS(nearestpeg, geneFile, filenum);
					filenum ++;
/**3		nearest point sequence simularity    **/
					
					if(pegSet.containsKey(testSequences.get(i).Cog)){
//						System.out.println(pegSet.get(testSequences.get(i).Cog));
						inSet ++;
					}
					else outSet ++;
					searchNegative++;
				}
			}
			// if we search and land on top of another coordinate
			else if (test.nearest(coord).toString().equals(testSequences.get(i).Cog)) {
/**2     THRESHOLD p1 START   **/
//				List<String> rangeVals = new ArrayList<String>();
//				List<String> medVals = new ArrayList<String>();
////				List<String> highVals = new ArrayList<String>();
//
//				rangeVals = findThreshold(0.0, 0.001, test, coord);
//				medVals = findThreshold(0.0, 0.01, test, coord);
////				highVals = findThreshold(0.01, 0.1, test, coord);
//				int sameCogSmall = 0;
//				int sameCogMed = 0;
//				pw.write(testSequences.get(i).Cog);
////				pws.write(testSequences.get(i).Cog);
////				pws.write(',');
////				pwm.write(testSequences.get(i).Cog);
////				pwm.write(',');
//				for (String genes : rangeVals) {
//					if (genes.equals(testSequences.get(i).Cog)) {
//						sameCogSmall++;
//					} else{
////						pws.write(genes + ' ');
//						pw.write(genes + ' ');
//					}
//
//					
//				}
////				pws.write(',');
//				pw.write(',');
//				for (String genes : medVals) {
//					if (genes.equals(testSequences.get(i).Cog)) {
//						sameCogMed++;
//					} else{
////						pwm.write(genes + ' ');
//						pw.write(genes + ' ');
//					}
//						
//				}
////				pwm.write(',');
//				pw.write(',');
////				for (String genes : highVals) {
////					if (genes.equals(testSequences.get(i).Cog)) {
////						sameCog++;
////					} else
////						pw.write(genes + ' ');
////				}
////				pw.write(',');
//				
////				pws.write('\n');
////				pwm.write('\n');
//				pw.write('\n');
/**2       THRESHOLD p1 END     **/
//				sameCogWriterSmall.write(testSequences.get(i).Cog);
//				sameCogWriterSmall.write(',');
//				sameCogWriterSmall.write(sameCogSmall);
//				sameCogWriterSmall.write('\n');
//				
//				sameCogWriterMed.write(testSequences.get(i).Cog);
//				sameCogWriterMed.write(',');
//				sameCogWriterMed.write(sameCogMed);
//				sameCogWriterMed.write('\n');

				hit++;
			} else if (test.search(coord).equals(testSequences.get(i).Cog) == false) {
				
//				System.out.println("looking at " + test.search(coord) + " and using " + testSequences.get(i).Cog);
				
				
/***2     THRESHOLD p2 START       **/
				
//				List<String> rangeVals = new ArrayList<String>();
//				List<String> medVals = new ArrayList<String>();
//				List<String> highVals = new ArrayList<String>();
//				rangeVals = findThreshold(0.0, 0.001, test, coord);
//				medVals = findThreshold(0.001, 0.01, test, coord);
////				highVals = findThreshold(0.01, 0.1, test, coord);
//				int sameCogSmall = 0;
//				int sameCogMed = 0;
//				// for(String nearCog : rangeVals){
//				// if(nearCog.equals(testSequences.get(i).Cog)){
//				// sameCog ++;
//				// }
//				// }
////				pws.write(testSequences.get(i).Cog);
////				pws.write(',');
////				pwm.write(testSequences.get(i).Cog);
////				pwm.write(',');
//				pw.write(testSequences.get(i).Cog);
//				pw.write(',');
//				for (String genes : rangeVals) {
//					if (genes.equals(testSequences.get(i).Cog)) {
//						sameCogSmall++;
//					} else{
////						pws.write(genes + ' ');
//						pw.write(genes + ' ');
//					}
//				}
////				pws.write(',');
//				pw.write(',');
//				for (String genes : medVals) {
//					if (genes.equals(testSequences.get(i).Cog)) {
//						sameCogMed++;
//					} else{
////						pwm.write(genes + ' ');
//						pw.write(genes + ' ');
//					}
//				}
////				pwm.write(',');
//				pw.write(',');
////				for (String genes : highVals) {
////					if (genes.equals(testSequences.get(i).Cog)) {
////						sameCog++;
////					} else
////						pw.write(genes + ' ');
////				}
//				pw.write(',');
//				pw.write('\n');
////				pws.write(',');
////				pws.write('\n');
////				pwm.write(',');
////				pwm.write('\n');

/***2     THRESHOLD p2 END       **/
//				sameCogWriterSmall.write(testSequences.get(i).Cog);
//				sameCogWriterSmall.write(',');
//				sameCogWriterSmall.write(sameCogSmall);
//				sameCogWriterSmall.write('\n');
//				
//				sameCogWriterMed.write(testSequences.get(i).Cog);
//				sameCogWriterMed.write(',');
//				sameCogWriterMed.write(sameCogMed);
//				sameCogWriterMed.write('\n');
				
		
				miss++;
			}

			// TODO: use test.nearest(coord) to get the nearest to the current
			// Gene in testSequence.get(i)
			// read the file and scan for the same testSequence.get(i).Cog and
			// test.nearest(coord)
			// record those sequences and pegs in separate files
			

		}
		// pw.write(sb.toString());
//		pw.close();
		//pws.close();
		//pwm.close();
//		sameCogWriterSmall.close();
//		sameCogWriterMed.close();
//		nearWriter.close();
//		System.out.println("KMER=" + kmerSize + " KDT=2");
		System.out.println("Hits: " + hit);
		System.out.println("Misclassified" + miss);
		System.out.println("Nothing there" + nothingThere);
		System.out.println("Search Positive: " + searchPositive);
		System.out.println("Search Negative: " + searchNegative);
		System.out.println("in missed set: " + inSet);
		System.out.println("not in missed set: " + outSet);
//		}
		// for(String key : lowHitThresholdMap.keySet()){
		// PrintWriter pw = new PrintWriter(new File("low hit threshold.csv"));
		// StringBuilder sb = new StringBuilder();
		// sb.append("peg #, # of points with same CSV, Other pegs found,\n");
		// sb.append(key);
		// }

		// System.exit(0);
		//
		//
		//
		// DiscoPlane plane = new DiscoPlane(10000);
		//
		// //long startTime = System.currentTimeMillis();
		// //grab vectors (gene=gene2) from gene
		// //grab x and y
		// //correlate them and add them to plane
		// System.out.println();
		// PrintWriter out = new PrintWriter(new File("output.txt"));
		// for (int i = 1; i<storage.size(); i++) {
		// double[] gene = storage.get(i).kmerVector.clone();
		// double[] gene2 = storage.get(i).kmerVector.clone();
		// double[] x = xAxis.clone();
		// double[] y = yAxis.clone();
		// //adding them to plane
		// BigDecimal b = new BigDecimal(getR(gene, x));
		// BigDecimal c = new BigDecimal(getR(gene2, y));
		// out.println(storage.get(i).Cog + "," + b.toPlainString() +
		// c.toPlainString());
		// //plane.placeSequence(new Point(b,c), storage.get(i).Cog);
		// }
		// out.close();
		// long estimatedTime = System.currentTimeMillis() - startTime;
		// System.out.println(estimatedTime);
		// plane.checkLegos();

		/*
		 * for (int i = 0; i < xAxis.length; i++) { System.out.print(yAxis[i] +
		 * ", "); } System.out.println();
		 * 
		 * for (int i = 0; i < xAxis.length; i++) { System.out.print(xAxis[i] +
		 * ", "); }
		 */
		/*
		 * for (int i = 0; i<storage.size(); i++) {
		 * System.out.println(storage.get(i).Cog); for (int j = 0;
		 * j<storage.get(i).kmerVector.length; j++) {
		 * System.out.print(storage.get(i).kmerVector[j] + ", "); } }
		 */

		/*
		 * for (int q = 2; q<7; q++) { int kmerToDo = q; Vector<double[]>
		 * kmerComps = new Vector<double[]>(); Vector<String> filePaths = new
		 * Vector<String>(); Scanner x = new Scanner(new
		 * File("BacPathsGenus.txt")); while (x.hasNextLine()) {
		 * filePaths.add(x.nextLine().substring(1).trim()); }
		 * //filePaths.add("xGenome.txt"); //filePaths.add("yGenome.txt"); for
		 * (int i = 0; i<filePaths.size(); i++) { File genome1 = new
		 * File(filePaths.get(i)); String sequence1 = inputSequence(genome1);
		 * kmerComps.add(processSequencebyKmer(sequence1, kmerToDo));
		 * System.out.println(i); } PrintStream outputer = new PrintStream(new
		 * File("GenomeCheck" + kmerToDo + ".csv")); for (int i = 0;
		 * i<kmerComps.size(); i++) { for (int j = 0; j<kmerComps.size(); j++) {
		 * double[] a = kmerComps.get(i).clone(); double[] b =
		 * kmerComps.get(j).clone(); outputer.println(filePaths.get(i) + "," +
		 * filePaths.get(j) + "," + getR(a, b)); } } outputer.close(); }
		 */
		/*
		 * //inputs 2 FASTA files for axis File genome1 = new
		 * File("Genome1.fna"); File genome2 = new File("Genome2.fna"); //grabs
		 * sequences from File - with replacements made
		 * System.out.println("Start Input"); String sequence1 =
		 * inputSequence(genome1); System.out.println(sequence1.length());
		 * String sequence2 = inputSequence(genome2);
		 * System.out.println(sequence2.length());
		 * 
		 * //gets kmerComps of sequence,kmerSize for (int i = 2; i<8; i++) {
		 * System.out.println(getR(processSequencebyKmer(sequence1, i),
		 * processSequencebyKmer(sequence2, i))); }
		 */
	}

	public static double[] parsePCAText(String text){
		
		String[] arr = text.split("[kmer]");
		double[] returnarr = new double[arr.length];
		for(int i = 0; i < arr.length; i += 4){
			if(i==0){
				String zeroSpot = arr[0].replaceAll(" ", "");
				String first = "";
				if(zeroSpot.contains("-")){
					first = "-"+zeroSpot.split("-")[1];
				}
				else{
					first = zeroSpot.substring(zeroSpot.length()-5, zeroSpot.length());
				}
				returnarr[Integer.valueOf(arr[i+4].split("[+-]")[0]) - 1] = Double.valueOf(first);
				continue;
			}
			else if(arr[i].contains("-")){
				String[] arr2 = arr[i].split("[-]");
//					System.out.println(Integer.valueOf(arr[i+4].split("[+-]")[0]));
					int posval = Integer.valueOf(arr[i+4].split("[+-]")[0]);
					returnarr[posval - 1] = Double.valueOf(arr2[1]) * -1.0;
				
			}
			
			else if(arr[i].contains("+")){
				String[] arr2 = arr[i].split("[+]");
				
					returnarr[Integer.valueOf(arr[i+4].split("[+-]")[0]) - 1] = Double.valueOf(arr2[1]);
			}
			else if(!arr[i].contains("+-") && i == 0){
				returnarr[Integer.valueOf(arr[i+4].split("[+-]")[0]) - 1] = Double.valueOf(arr[i]);
			}
		}
		
		return returnarr;
	}

	private static double getPCAY(double[] kmer, double[] pcaArr) {
		//  0.414kmer13-0.39kmer7+0.379kmer1+0.379kmer16-0.333kmer10+0.309kmer4-0.294kmer11-0.294kmer6-0.054kmer9-0.054kmer14+0.039kmer8+0.039kmer3-0.023kmer12-0.023kmer2+0.005kmer5+0.005kmer15
		double retval = 0.0;
		
		for(int i = 0; i < kmer.length; i ++){
			retval += pcaArr[i] * kmer[i];
		}
		
		return retval;
//		return   0.414 * kmer[12] - 0.39 * kmer[6] + 0.379 * kmer[0] + 0.379 * kmer[15] - 0.333 * kmer[9] + 0.309 * kmer[3]
//				- 0.294 * kmer[10] - 0.294 * kmer[5] - 0.054 * kmer[8] - 0.054 * kmer[13] + 0.039 * kmer[7] + 0.039 * kmer[2]
//						- 0.023 * kmer[11] - 0.023 * kmer[1] + 0.005 * kmer[4] + 0.005 * kmer[14];
	}

	private static double getPCAX(double[] kmer, double[] pcaArr) {
		// -0.278kmer15-0.278kmer5-0.278kmer3-0.278kmer8-0.278kmer12-0.278kmer2-0.276kmer9-0.276kmer14-0.238kmer6-0.238kmer11-0.235kmer4-0.224kmer10-0.211kmer1-0.211kmer16-0.201kmer7-0.191kmer13
		
		double retval = 0.0;
		
		for(int i = 0; i < kmer.length; i ++){
			retval += pcaArr[i] * kmer[i];
		}
		
		return retval;
		
//		return -0.278 * kmer[14] - 0.278* kmer[4] - 0.278*kmer[2] - 0.278 * kmer[7] - 0.278 * kmer[11] - 0.278 * kmer[1] - 0.276 * kmer[8] 
//				- 0.276 * kmer[13] - 0.238 * kmer[5] - 0.238 * kmer[10] - 0.235 * kmer[3] - 0.224* kmer[9] - 0.211 * kmer[0] 
//						- 0.211 * kmer[15] - 0.201 * kmer[6] - 0.191 * kmer[12];
	}

	private static void findNearestSequencesS(String currpeg, File f, int filenum) throws IOException {
		String sequence = "";
		String id = "";
		BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
		String line = "";
		while ((line = bufferedReader.readLine()) != null) {
			id = line;
			if (id.equals(currpeg)) {
				sequence = bufferedReader.readLine();
				sequence = replaceNucs(sequence);
				break;
			}

			// System.out.println(sequence);
			// if (count>TOTAL_VALS) {
			id = "";
			sequence = "";
		}
		bufferedReader.close();

		PrintWriter pw = new PrintWriter(new File("Nearest\\match" + filenum + "s.fasta"));
		pw.write(id);
		pw.write('\n');
		pw.write(sequence);
		pw.close();
	}

	private static void findNearestSequencesQ(String currpeg, File f, int filenum) throws IOException {
		// TODO Auto-generated method stub
		String sequence = "";
		String id = "";
		BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
		String line = "";
		while ((line = bufferedReader.readLine()) != null) {
			id = line;
			if (id.equals(currpeg)) {
				sequence = bufferedReader.readLine();
				sequence = replaceNucs(sequence);
				break;
			}

			id = "";
			sequence = "";
		}
		bufferedReader.close();

		PrintWriter pw = new PrintWriter(new File("Nearest\\match" + filenum + "q.fasta"));
		pw.write(id + "\n" + sequence);
		pw.close();

	}

	private static List findThreshold(double d, double e, KDTree test, double[] coord) throws KeySizeException {
		// TODO Auto-generated method stub

		//  return test.range(new double[]	{ x - d, y - e, ...}, new double[]	{x + d, y + e, ...} ) 
		return test.range(new double[] { coord[0] - d, coord[1] - e }, 
				new double[] { coord[0] + d, coord[1] + e });

	}

	private static void writeToThresholdCSV(Gene gene, int sameCog, List<String> otherGenesFound, String title)
			throws FileNotFoundException {
		// TODO write out CSV file formatted peg #, same found, others in area
		PrintWriter pw = new PrintWriter(new File(title + ".csv"));
		StringBuilder sb = new StringBuilder();
		sb.append(gene.Cog);
		sb.append(',');
		sb.append(sameCog);
		sb.append(',');
		for (String genes : otherGenesFound) {

			sb.append(genes);

		}
		sb.append('\n');
		pw.append(sb.toString());
		pw.close();

	}

	/**
	 * Will take the cluster map and write to a CSV file. Singularly purposed.
	 * Do not touch
	 * 
	 * @param clusterMap
	 * @param centers
	 * @param averages
	 * @param medians
	 * @throws FileNotFoundException
	 */
	private static void writeToCSV(HashMap<Integer, List<Double[]>> clusterMap, HashMap<Integer, Double[]> centers,
			HashMap<Integer, Double> averages) throws FileNotFoundException {
		// TODO use a print writer to write all the averages to a CSV file in
		// the following format
		// peg # (aka the Cog or key val for the hmaps), # of points in cluster,
		// mean distance, median distance

		PrintWriter pw = new PrintWriter(new File("cluster distance.csv"));
		StringBuilder sb = new StringBuilder();
		sb.append("peg #");
		sb.append(',');
		sb.append("# of points in cluster");
		sb.append(',');
		sb.append("mean distance");
		sb.append(',');
		//sb.append("median distance");
		sb.append('\n');
		for (Integer key : clusterMap.keySet()) {
			sb.append(key.toString());
			sb.append(',');
			sb.append(Integer.toString(clusterMap.get(key).size()));
			sb.append(',');
			sb.append(averages.get(key).toString());
			sb.append(',');

			// sb.append(Collections.sort((List<T>)
			// Arrays.asList(averages.values().toArray())));
			sb.append('\n');
		}
		pw.write(sb.toString());
		pw.close();
		System.out.println("CSV distance file written");

	}

	/**
	 * Will calculate the median distance for the points
	 * 
	 * @param clusterMap
	 *            the map of all the clusters. The key is the Cog (or peg) value
	 *            and the values are lists of coordinates associated with the
	 *            Cog
	 * @param centers
	 *            the map of centers. The key is the cog and the values are the
	 *            center of the clusters associated with the cog
	 * @return
	 */
	private static HashMap<Integer, Double[]> calculateMedianDistanceFromCenters(
			HashMap<Integer, List<Double[]>> clusterMap, HashMap<Integer, Double[]> centers) {
		List<List<Double[]>> clusters = new ArrayList<List<Double[]>>(clusterMap.values());
		List<Double> xvals = new ArrayList<Double>();
		List<Double> yvals = new ArrayList<Double>();
		HashMap<Integer, Double[]> medians = new HashMap<Integer, Double[]>();
		for (Integer key : clusterMap.keySet()) {
			List<Double[]> points = clusterMap.get(key);
			for (Double[] coords : points) {
				xvals.add(coords[0]);
				yvals.add(coords[1]);
			}
			Collections.sort(xvals);
			Collections.sort(yvals);
			if (xvals.size() % 2 == 0) {
				medians.put(key, new Double[] { xvals.get(xvals.size() / 2 - 1), yvals.get(yvals.size() / 2 - 1) });
			} else {
				medians.put(key, new Double[] { xvals.get(xvals.size() / 2), yvals.get(yvals.size() / 2) });
			}

		}
		return medians;
	}

	/**
	 * Calculates the average, or mean, distance from the centers for each of
	 * the cluster's values
	 * 
	 * @param clusterMap
	 *            - the map containing the list of points associated with a Cog
	 *            as the key
	 * @param centers
	 *            - the map with coordinates for the center of each cluster with
	 *            a cog for the cluster center as the key
	 * @return a hashmap with a Cog as the key and the average distance of all
	 *         points from the center
	 */
	private static HashMap<Integer, Double> calculateAverageDistanceFromCenters(
			HashMap<Integer, List<Double[]>> clusterMap, HashMap<Integer, Double[]> centers) {
		// DONE: find distance between center and all points. then add them
		// together and divide by length of distance array
		HashMap<Integer, List<Double>> distanceMap = new HashMap<Integer, List<Double>>();

		for (Integer key : clusterMap.keySet()) {

			distanceMap.put(key, new ArrayList<Double>());

			// finding the distance between a pair of coordinates and the center
			for (Double[] coords : clusterMap.get(key)) {
				Double x1 = coords[0];
				Double y1 = coords[1];
//				Double z1 = coords[2];
//				Double q1 = coords[3];
//				Double r1 = coords[4];
				Double x2 = centers.get(key)[0];
				Double y2 = centers.get(key)[1];
//				Double z2 = centers.get(key)[2];
//				Double q2 = centers.get(key)[3];
//				Double r2 = centers.get(key)[4];
				distanceMap.get(key).add(Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1) 
						));
			}

		}

		// add up the distances and divide by the size of the list
		Double distances = 0.0;
		HashMap<Integer, Double> averages = new HashMap<Integer, Double>();
		for (Integer key : distanceMap.keySet()) {
			distances = 0.0;

			for (Double distance : distanceMap.get(key)) {
				distances += distance;
			}
			// map the key to the respective average
			averages.put(key, distances / distanceMap.get(key).size());
		}
		return averages;
	}

	/**
	 * will calculate the centers of the clusters in the KDTree
	 * 
	 * @param clusterMap
	 *            - a map with the list of coordinates associated with a cluster
	 *            and a cog as a key for the points
	 * @return will return a hashmap with the coordinates of the center as the
	 *         value and the cluster's cog as the key
	 */
	private static HashMap<Integer, Double[]> calculateClusterCenters(HashMap<Integer, List<Double[]>> clusterMap) {
		// DONE: go through and calculate the x and y coordinates for each of
		// the clusters
		HashMap<Integer, Double[]> centers = new HashMap<Integer, Double[]>();
		Double totalX = 0.0;
		Double totalY = 0.0;
//		Double totalZ = 0.0;
//		Double totalQ = 0.0;
//		Double totalR = 0.0;
		for (Integer key : clusterMap.keySet()) {
			totalX = 0.0;
			totalY = 0.0;
//			totalZ = 0.0;
//			totalQ = 0.0;
//			totalR = 0.0;
			// find the center of the cluster
			for (Double[] coords : clusterMap.get(key)) {
				totalX += coords[0];
				totalY += coords[1];
//				totalZ += coords[2];
//				totalQ += coords[3];
//				totalR += coords[4];
			}
			centers.put(key, new Double[] { totalX / clusterMap.get(key).size(), totalY / clusterMap.get(key).size(), 
					 });
		}

		return centers;
	}
	
	
	public static Vector<Gene> InputAndProcessGenesCategoryTest(File f) throws IOException{
		boolean first = true;
		String sequence = "";
		Vector<Gene> storage = new Vector<Gene>();
		String id = "";
		BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
		String line = "";
		int count = 0;
		while ((line = bufferedReader.readLine()) != null) {
			if(line.contains("USS-DB")){
				sequence = bufferedReader.readLine();
				id = "";
				sequence = "";
				continue;
			}
			id = line;
			sequence = bufferedReader.readLine();
			sequence = replaceNucs(sequence);
			sequence = sequence.substring(60, sequence.length() - 2);
			if(!id.contains("hypothetical") || !id.contains("Hypothetical")){
				storage.add(new Gene(id, processSequencebyKmer(sequence, kmerToDo)));
				count++;
			}
			
			
			 if (count>100000) {
//			if (count > 1000) {
				break;
			}
			id = "";
			sequence = "";
		}
		bufferedReader.close();
		return storage;
	}

	//
	public static Vector<Gene> InputAndProcessGenesCategory(File f) throws IOException{
		boolean first = true;
		String sequence = "";
		Vector<Gene> storage = new Vector<Gene>();
		String id = "";
		BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
		String line = "";
		int count = 0;
		while ((line = bufferedReader.readLine()) != null) {

			if(line.contains("USS-DB")){
				sequence = bufferedReader.readLine();
				id = "";
				sequence = "";
				continue;
			}
			id = line;
			
			sequence = bufferedReader.readLine();
			sequence = replaceNucs(sequence);
			sequence = sequence.substring(60, sequence.length() - 2);
			if (!id.contains("hypothetical") || !id.contains("Hypothetical")) {
				storage.add(new Gene(id, processSequencebyKmer(sequence, kmerToDo)));
				count++;
			}
			 if (count>TOTAL_VALS) {
//			if (count > 1000) {
				break;
			}
			id = "";
			sequence = "";
		}
		bufferedReader.close();
		return storage;
	}
	
	
	/**
	 * looks through the file and takes in the gene sequences and
	 * classifications
	 * 
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static Vector<Gene> InputAndProcessGenesLine(File f ) throws IOException {
		boolean first = true;
		String sequence = "";
		Vector<Gene> storage = new Vector<Gene>();
		String id = "";
		BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
		String line = "";
		int count = 0;
		while ((line = bufferedReader.readLine()) != null) {
			id = line;
			sequence = bufferedReader.readLine();
			sequence = replaceNucs(sequence);
			// System.out.println(sequence);
			storage.add(new Gene(id, processSequencebyKmer(sequence, kmerToDo)));
			count++;
			 if (count>TOTAL_VALS) {
//			if (count > 1000) {
				break;
			}
			id = "";
			sequence = "";
		}
		bufferedReader.close();
		return storage;
	}

	public static String getPeg(String id) {
		return id.substring(id.indexOf("peg.") + 4);
	}

	public static Vector<Gene> InputAndProcessGenes(File f) throws IOException {
		boolean first = true;
		String sequence = "";
		Vector<Gene> storage = new Vector<Gene>();
		String id = "";
		BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
		String line = "";
		int count = 0;
		while ((line = bufferedReader.readLine()) != null) {
			if (line.contains(">")) {
				if (first)
					first = false;
				else
					sequence = replaceNucs(sequence);
				// TODO: Filter out unneeded data in ID

				storage.add(new Gene(id, processSequencebyKmer(sequence, kmerToDo)));
				count++;
				if (count > 100000) {
					break;
				}
				id = "";
				sequence = "";
				id = line.replace("'", "").replaceAll(",", "");
			} else {
				sequence += line;
			}
		}
		bufferedReader.close();
		return storage;
	}

	public static Double getMutualInformation(double[] comps, double[] y) {
		// MutualInformation mi = new MutualInformation();
		return MutualInformation.calculateMutualInformation(comps, y);
	}

	public static Double getR(double[] comps, double[] y) {
		double xAverage = 0;
		double yAverage = 0;
		double zAverage = 0;
		// THIS NORMALIZES!!
		double xCount = 0;
		double yCount = 0;
		double zCount = 0;
		for (int i = 0; i < comps.length; i++) {
			xCount += comps[i];
			yCount += y[i];
			
		}
		for (int i = 0; i < comps.length; i++) {
			comps[i] = comps[i] / xCount;
			y[i] = y[i] / yCount;
			
		}
		for (int i = 0; i < comps.length; i++) {
			xAverage += comps[i];
			yAverage += y[i];
		}
		xAverage = xAverage / comps.length;
		yAverage = yAverage / y.length;
		double xy = 0;
		double xSq = 0;
		double ySq = 0;
		for (int i = 0; i < comps.length; i++) {
			comps[i] = comps[i] - xAverage;
			y[i] = y[i] - yAverage;
			xy += (comps[i] * y[i]);
			xSq += comps[i] * comps[i];
			ySq += y[i] * y[i];
		}
		double r = xy / Math.sqrt(xSq * ySq);
		return r;
	}

	public static Double getRTau(double[] comps, double[] y) {

		KendallsCorrelation kcor = new KendallsCorrelation();
		return kcor.correlation(comps, y);
	}

	public static String replaceNucs(String sequence) {
		sequence = sequence.replaceAll("B", "N");
		sequence = sequence.replaceAll("D", "N");
		sequence = sequence.replaceAll("E", "N");
		sequence = sequence.replaceAll("F", "N");
		sequence = sequence.replaceAll("H", "N");
		sequence = sequence.replaceAll("I", "N");
		sequence = sequence.replaceAll("J", "N");
		sequence = sequence.replaceAll("K", "N");
		sequence = sequence.replaceAll("L", "N");
		sequence = sequence.replaceAll("M", "N");
		sequence = sequence.replaceAll("O", "N");
		sequence = sequence.replaceAll("P", "N");
		sequence = sequence.replaceAll("Q", "N");
		sequence = sequence.replaceAll("R", "N");
		sequence = sequence.replaceAll("S", "N");
		sequence = sequence.replaceAll("U", "N");
		sequence = sequence.replaceAll("V", "N");
		sequence = sequence.replaceAll("W", "N");
		sequence = sequence.replaceAll("X", "N");
		sequence = sequence.replaceAll("Y", "N");
		sequence = sequence.replaceAll("Z", "N");
		sequence = sequence.toUpperCase();
		return sequence;
	}

	/**
	 * returns the sequence of the genomes, which is then turned into the axis
	 * on the grid
	 * 
	 * @param f
	 *            the file of genomes
	 * @return a string sequence representing the genomes
	 * @throws IOException
	 */
	public static String inputGenomeSequence(File f) throws IOException {
		String sequence = "";
		String id = "";
		BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
		bufferedReader.readLine();
		StringBuilder stringbuilder = new StringBuilder();
		String line = "";
		while ((line = bufferedReader.readLine()) != null) {
			if (line.contains(">") == false) {
				stringbuilder.append(line);
			} else {
				// just so we have all included kmers within the nnn
				stringbuilder.append("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
			}
		}
		sequence = stringbuilder.toString();
		bufferedReader.close();
		return sequence;
	}

	/**
	 * Method to get kmer composition
	 * 
	 * @param sequence
	 *            the sequence found from the input genome sequence method
	 * @param mermer
	 *            the size of the kmer
	 * @return a double representing the number of matched kmers found in the
	 *         sequence
	 * @throws FileNotFoundException
	 */
	public static double[] processSequencebyKmer(String sequence, int mermer) {
		nmer = mermer;
		numShifts = 64 - (2 * (nmer - 1));
		numShiftsMinus = numShifts - 2;
		double[] comps = runGetKmers(sequence);
		return comps;
	}

	public static double[] runGetKmers(String sequence) {
		double[] kmerComp = new double[(int) Math.pow(4, nmer)];
		String[] toRun = sequence.split("N");
		for (int i = 0; i < toRun.length; i++) {
			if (toRun[i].length() >= nmer) {
				kmerComp = getKmers(toRun[i], kmerComp);
			}
		}
		double sum = 0;
		for (int i = 0; i < kmerComp.length; i++) {
			sum += kmerComp[i];
		}
		for (int i = 0; i < kmerComp.length; i++) {
			kmerComp[i] = kmerComp[i];
		}
		return kmerComp;
	}

	public static double[] getKmers(String sequence, double[] kmerComp) {
		// initialize first set of kmers
		Long temp = null;
		Long full = Long.parseUnsignedLong("0");
		int i = 0;
		for (i = 0; i < nmer; i++) {
			temp = nucToNum(sequence.charAt(i));
			try {
				full = full + temp;
			} catch (NullPointerException e) {
				System.out.println(sequence.substring(i));
			}
			// full = full + temp;
			if (i < nmer - 1) {
				full = full << 2;
			}
		}
		// add it and its reverse kmer to count array
		kmerComp[full.intValue()] += 1;
		kmerComp[reverser(full)] += 1;

		// delete first nucleotide and add to the end of it
		// add it and its reverse complement to count array
		while (i < sequence.length()) {
			temp = nucToNum(sequence.charAt(i));
			full = fancyShift(full);
			try {
				full = full + temp;
				kmerComp[full.intValue()] += 1;
				kmerComp[reverser(full)] += 1;
				i++;
			}

			// TODO:What the hell is going on here?
			catch (NullPointerException e) {
				break;
			}
		}
		// return kmerComposition array
		return kmerComp;
	}

	public static Long fancyShift(Long a) {
		a = a << numShifts;
		a = a >>> numShiftsMinus;
		return a;
	}

	public static int reverser(Long xx) {
		xx = ~xx;
		xx = Long.reverse(xx);
		xx = xx >>> numShiftsMinus;
		String rc = Long.toBinaryString(xx);
		int length = rc.length();
		if (length % 2 == 1) {
			rc = '0' + rc;
		}
		char[] twosies = rc.toCharArray();
		String newString = "";
		for (int i = 0; i < twosies.length; i = i + 2) {
			if (twosies[i] == '0' && twosies[i + 1] == '1') {
				twosies[i] = '1';
				twosies[i + 1] = '0';
			} else if (twosies[i] == '1' && twosies[i + 1] == '0') {
				twosies[i] = '0';
				twosies[i + 1] = '1';
			}
			newString += twosies[i];
			newString += twosies[i + 1];
		}
		Long l = parseLong(newString, 2);
		return l.intValue();
	}

	private static long parseLong(String s, int base) {
		return new BigInteger(s, base).longValue();
	}

	// Silly initializations for optimization
	public static Long aLong = Long.parseUnsignedLong("0");
	public static Long cLong = Long.parseUnsignedLong("1");
	public static Long gLong = Long.parseUnsignedLong("2");
	public static Long tLong = Long.parseUnsignedLong("3");

	// turns a nucleotide character to a binary representation
	public static Long nucToNum(char a) {
		switch (a) {
		case 'A':
			return aLong;
		case 'C':
			return cLong;
		case 'G':
			return gLong;
		case 'T':
			return tLong;
		case 'U':
			return tLong;
		default:
			return null;
		}
	}

}
