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
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.math3.stat.correlation.KendallsCorrelation;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;

/**
 * 
 * @author Larry Kalesinskas
 *
 */
public class KDTOnly {
	private static int TOTAL_VALS = 3000000;
	public static int nmer = 0;
	public static int kmerMax = 9;
	public static int numShifts = 0;
	public static int numShiftsMinus = 0;
	public static int kmerToDo = 0;
	protected static int miss = 0;
	protected static int hit = 0;
	protected static int nothingThere = 0;
	protected static int searchPositive = 0;
	protected static int searchNegative = 0;
	static int inSet = 0;
	static int outSet = 0;
	double[] gene;

	public static void execute(String PCAFile, String TestFile, String TrainFile, String OutFile, int numthread) throws Exception {

		// kmer size we are using
		kmerToDo = 3;

		// Files for Axis
		File genome1 = new File("Genomes\\Genome1.fna");
		File genome2 = new File("Genomes\\Genome2.fna");
		File genome3 = new File("Genomes\\GCF_000160075.2_ASM16007v2_genomic.fna");
		File genome4 = new File("Genomes\\GCF_000376245.1_ASM37624v1_genomic.fna");
		File genome5 = new File("Genomes\\GCF_000018105.1_ASM1810v1_genomic.fna");

		// this is the training data for the models
		File geneFile = new File(TrainFile);

		// Getting sequence		
		System.out.println("Making KD Tree");
		//  reading equations
		System.out.println("Reading Equations");
		BufferedReader bufferedReader = new BufferedReader(new FileReader(PCAFile));
		String line = "";
		int count = 0;
		
		//  reading and parsing PCA equations
		List<double[]> equationList = new ArrayList<double[]>();
		while ((line = bufferedReader.readLine()) != null) {
			equationList.add(parsePCAText(line));
		}
			bufferedReader.close();
			
			//  the kdt will have dimensions equal to the size of the equation list
		KDTree test = new KDTree(equationList.size());
		HashMap<String, Integer> pegSet = new HashMap<String, Integer>();
		
		int intersectionCount = 0;
		HashMap<String, List<double[]>> clusterMap = new HashMap<String, List<double[]>>();
		System.out.println("Correlating");
		boolean first = true;
		
		//  reading from the train file
		BufferedReader br = new BufferedReader(new FileReader(geneFile));
		String id = "";
		String sequence = "";
		HashMap<double[],HashMap<String, Integer>> sameMap = new HashMap<double[], HashMap<String, Integer>>();
		/**  the writers for train and test out that will be used for the spanning set   **/
//		BufferedWriter trainWriter = new BufferedWriter(new FileWriter("trainOut5.ffn"));
//		BufferedWriter testWriter = new BufferedWriter(new FileWriter("testOut5.ffn"));
		
		while( (line = br.readLine()) != null){

			/**   INSERTING INTO TREE OR STORAGE VECTOR    **/
			//  if it is the first line
						if(line.contains(">")){
							if(first){
								id = line;
								first = false;
								continue;
							}
							else{
								//  filter out unwanted sequences
								if(id.contains("hypothetical") || id.contains("Hypothetical") || id.contains("USS-DB") || sequence.length() < 100){
									sequence = "";
									id=line;
									continue;
								}
								
								// 
								sequence = replaceNucs(sequence);
								sequence = sequence.substring(60, sequence.length() - 2);
								double[] gene = processSequencebyKmer(sequence, kmerToDo);
								double sumGene = 0.0;
								for(int i2 = 0; i2 < gene.length; i2++){
									sumGene+=gene[i2];
								}
								for(int i2 = 0; i2 < gene.length; i2++){
									gene[i2] = gene[i2]/sumGene;
								}
								
								Double[] coordArr = new Double[equationList.size()];
								
								for(int v = 0; v < equationList.size(); v ++){
									coordArr[v] = getPCAX(gene, equationList.get(v));
								}
								
								
								
								Double[] coord1 = coordArr;
								double[] coord = new double[coord1.length];
								for(int c = 0; c < coord1.length; c ++){
									coord[c] = coord1[c];
								}
								
								
								for(int v = 0; v < equationList.size(); v ++){
									coordArr[v] = getPCAX(gene, equationList.get(v));
								}
								/**   if a search at the coord yields nothing  **/
								if(test.search(coord) == null){
									test.insert(coord, id);
								}
								else if(test.search(coord) != null){
									intersectionCount ++;
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
			BufferedWriter intersectWriter = new BufferedWriter(new FileWriter("test intersects.csv"));
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
			intersectWriter.close();
			System.out.println("beginning training data");
/**   TRAINING DATA START    **/
			
/**   TRAINING DATA END    **/
		System.out.println("training data finished");

		System.out.println("KDTree size " + test.size());
		System.out.println("actual intersection count " + intersectionCount);
		
		// testing the file of the subset of figs
		File testFile = new File(TestFile);
		BufferedWriter testWriter = new BufferedWriter(new FileWriter("100ktestOut.csv"));
		testWriter.write("Hits,Misclassified,Nothing there,Search Positive, Search Negative, in missed set, not in missed set,");
		testWriter.write("\n");
		
		/** begin loop for 100k test files   **/
		//  begin loop for running through 100 test files of 100k
		for(int i = 0; i < 100; i ++){
			testFile = new File("D:\\Larry Projects\\KSCOPE\\FinishingKScopeOff\\test"+i+".ffn");
		Vector<Gene> testSequences = InputAndProcessGenesCategoryTest(testFile);
		System.out.println("We have " + testSequences.size() + " test sequences!");
		//  reset the values for each test file run through
		miss = 0;
		hit = 0;
		nothingThere = 0;
		searchPositive = 0;
		searchNegative = 0;
		inSet = 0;
		outSet = 0;
		HashMap<String, Double> correctIDHit = new HashMap<String, Double>();
		HashMap<String, Double> allIDHit = new HashMap<String, Double>();

		int filenum = 0;

		
		//  thread pool.  10 threads seems to cut runtime down to half an hour
		ExecutorService executor = Executors.newFixedThreadPool(10);
		for(int runs = 0; runs < 1000; runs ++){
			final int runs3 = runs;
			Runnable r = new Runnable(){
				public void run(){
					//  total from test file is runs * sequences
					for(int sequences = runs3*100; sequences < runs3*100 + 100; sequences ++){
						if(sequences == 0) sequences = 2;  //  error happens if sequences = 0 or 1
						//  get the kmer vector
						try{
							double[] gene = testSequences.get(sequences).kmerVector.clone();
							double sumGene = 0.0;
							for(int i2 = 0; i2 < gene.length; i2++){
								sumGene+=gene[i2];
							}
							for(int i2 = 0; i2 < gene.length; i2++){
								gene[i2] = gene[i2]/sumGene;
							}
							
							//  begin calculating the coordinates for the test sequences
							Double[] coordArr = new Double[equationList.size()];
							for(int v = 0; v < equationList.size(); v ++){
								coordArr[v] = getPCAX(gene, equationList.get(v));
							}
							
							
							Double[] coord1 = coordArr;
							double[] coord = new double[coord1.length];
							for(int c = 0; c < coord1.length; c ++){
								coord[c] = coord1[c];
							}
							
							
							//  random weird error happens without this
							if(coordArr[0].isNaN() || coordArr[1].isNaN()) continue;
						
							// if it is not in the grid, look at the nearest one.
							if (test.search(coord) == null) {
								// if the classification is the same, then we are right
								// otherwise we are wrong
								if (test.nearest(coord).toString().equals(testSequences.get(sequences).Cog)) {
									incrementSearchPositive();
									/**  for testing for "conserved" points  **/
//									if(correctIDHit.containsKey(test.search(coord).toString()) ){
//										correctIDHit.put(test.search(coord).toString(), correctIDHit.get(test.search(coord).toString()) +1);
//									}
//									else{
//										correctIDHit.put(test.search(coord).toString(), 1.0);
//									}
//									if(allIDHit.containsKey(test.search(coord).toString())){
//										allIDHit.put(test.search(coord).toString(), allIDHit.get(test.search(coord).toString()) +1);
//									}
//									else{
//										allIDHit.put(test.search(coord).toString(), 1.0);
//									}
								}else{
									/**   skipped a lot of commented code.  please refer to below for missing commented out code   **/
									
									if(pegSet.containsKey(testSequences.get(sequences).Cog)){
										inSet ++;
									}
									else{
										outSet ++;
									}
									incrementSearchNegative();
									/**  for testing for "conserved" points  **/
//									if(allIDHit.containsKey(test.search(coord).toString())){
//										allIDHit.put(test.search(coord).toString(), allIDHit.get(test.search(coord).toString()) +1);
//									}
//									else{
//										allIDHit.put(test.search(coord).toString(), 1.0);
//									}
									
								}
							}
							// if we search and land on top of another coordinate
							else if (test.nearest(coord).toString().equals(testSequences.get(sequences).Cog)) {
								/**   skipped a lot of commented code.  please refer to below for missing commented out code   **/
								incrementHit();
								/**  for testing for "conserved" points  **/
//								if(correctIDHit.containsKey(test.search(coord).toString())){
//									correctIDHit.put(test.search(coord).toString(), correctIDHit.get(test.search(coord).toString()) +1);
//								}
//								else{
//									correctIDHit.put(test.search(coord).toString(), 1.0);
//								}
							}
							//  hits something, but not the correct values
							else if (test.search(coord).equals(testSequences.get(sequences).Cog) == false) {
								/**   skipped a lot of commented code.  please refer to below for missing commented out code   **/
								incrementMiss();
								/**  for testing for "conserved" points  **/
//								if(allIDHit.containsKey(test.search(coord).toString())){
//									allIDHit.put(test.search(coord).toString(), allIDHit.get(test.search(coord).toString()) +1);
//								}
//								else{
//									allIDHit.put(test.search(coord).toString(), 1.0);
//								}
							}
						} catch (KeySizeException | ArrayIndexOutOfBoundsException e) {
							// Array Index Out Of Bounds  is being caused by all the test files being less than 100k sequences in size
							
							if(e instanceof ArrayIndexOutOfBoundsException){
								break;
							}
							else{
								e.printStackTrace();
							}
						}
						
					}
				}
			};
			executor.execute(r);
		}
		
		//  shutdown each thread as it finishes
		executor.shutdown();
		
		//  wait for the threads to finish
		while(!executor.isTerminated()){
			
		}
	
		//  display the results
		System.out.println("for test " + i);
		System.out.println("Hits: " + getHits());
		testWriter.write(getHits()+",");
		System.out.println("Misclassified " + getMisses());
		testWriter.write(getMisses()+",");
		System.out.println("Nothing there" + getNothingThere());
		testWriter.write(getNothingThere()+",");
		System.out.println("Search Positive: " + getSearchPositive());
		testWriter.write(getSearchPositive()+",");
		System.out.println("Search Negative: " + getSearchNegative());
		testWriter.write(getSearchNegative()+",");
		System.out.println("in missed set: " + inSet);
		testWriter.write(inSet+",");
		System.out.println("not in missed set: " + outSet);
		testWriter.write(outSet+",\n");
		}
		testWriter.close();
	}

	private static int getSearchNegative() {
		return searchNegative;
	}

	private static int getSearchPositive() {
		return searchPositive;
	}

	private static int getNothingThere() {
		return nothingThere;
	}

	private static int getMisses() {
		return miss;
	}

	private static int getHits() {
		return hit;
	}

	protected synchronized static void incrementSearchPositive() {
		searchPositive++;
	}
	protected synchronized static void incrementSearchNegative(){
		searchNegative++;
	}
	protected synchronized static void incrementHit(){
		hit++;
	}
	protected synchronized static void incrementMiss(){
		miss++;
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
		sb.append(getPeg(gene.Cog));
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
	
	
	/**
	 * 	The current way to take in and process genes from a file
	 * @param f
	 * @return
	 * @throws IOException
	 */
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
			if(sequence.length() < 100){
				sequence = "";
				id = "";
				continue;
			}
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
	

	
	/**
	 * looks through the file and takes in the gene sequences and
	 * classifications
	 * 
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static Vector<Gene> InputAndProcessGenesLine(File f, double[] xEQN, double[] yEQN ) throws IOException {
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
			sequence = sequence.substring(60, sequence.length() - 2);
//			storage.add(new Gene(id, processSequencebyKmer(sequence, kmerToDo)));
			storage.add(new Gene(id, getPCAX(processSequencebyKmer(sequence, kmerToDo), xEQN), getPCAY(processSequencebyKmer(sequence, kmerToDo), yEQN)));
			
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
		sequence = replaceNucs(sequence);
		
		double[] comps = runGetKmers(sequence);
		return comps;
	}

	public static double[] runGetKmers(String sequence) {
		sequence = replaceNucs(sequence);
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
				System.out.println("replaced   i="+i+"      " + replaceNucs(sequence));
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
