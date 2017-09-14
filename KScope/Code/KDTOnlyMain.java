package KScope.Code;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.math3.stat.correlation.KendallsCorrelation;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeySizeException;

/**
 * 
 * @author Larry Kalesinskas
 *
 */
public class KDTOnlyMain {
	private static int TOTAL_VALS = 3000000;
	public static int nmer = 0;
	public static int kmerMax = 9;
	public static int numShifts = 0;
	public static int numShiftsMinus = 0;
	protected static int miss = 0;
	protected static int hit = 0;
	protected static int nothingThere = 0;
	protected static int searchPositive = 0;
	protected static int searchNegative = 0;
	static int inSet = 0;
	static int outSet = 0;
	double[] gene;

	public static void execute(String PCAFile, String TestFile, String TrainFile, String OutFile, int numthread,
			int kmer, boolean fastatofeature) {

		// this is the training data for the models
		File geneFile = new File(TrainFile);

		// Getting sequence
		System.out.println("Making KD Tree");
		// reading equations
		System.out.println("Reading Equations");
		BufferedReader bufferedReader;
		try {
			bufferedReader = new BufferedReader(new FileReader(PCAFile));

			String line = "";

			// reading and parsing PCA equations
			List<double[]> equationList = new ArrayList<double[]>();
			while ((line = bufferedReader.readLine()) != null) {
				equationList.add(parsePCAText(line));
			}
			bufferedReader.close();

			// the kdt will have dimensions equal to the size of the equation
			// list
			KDTree test = new KDTree(equationList.size());
			HashMap<String, Integer> pegSet = new HashMap<String, Integer>();

			System.out.println("Correlating");

			/** TRAINING DATA START **/
			BufferedReader br = new BufferedReader(new FileReader(geneFile));
			String extension = "";
			long start = System.nanoTime();
			int i = TrainFile.lastIndexOf('.');
			int p = Math.max(TrainFile.lastIndexOf('/'), TrainFile.lastIndexOf('\\'));

			if (i > p) {
				extension = TrainFile.substring(i + 1);
			}
			BufferedReader trainReader = new BufferedReader(new FileReader(TrainFile));
			String trainLine = trainReader.readLine();
			int intersectionCount = 0;
			try {
				
				if (Double.valueOf(trainLine.split(",")[0]) != null && trainLine.contains("~~")) {
					TrainFeature trainer = new TrainFeature();
					trainer.train(test, br, equationList, kmer);
					intersectionCount = trainer.getIntersectionCount();
				} 
				else if(trainLine.contains(">") && trainLine.charAt(0) == '>'){
					TrainFasta trainer = new TrainFasta();
					trainer.train(test, br, equationList, kmer, fastatofeature, TrainFile);
					intersectionCount = trainer.getIntersectionCount();
				}else {
					throw new IOException();
				}
			} catch (NumberFormatException y) {
				System.out.println("Not a supported File type");
				System.exit(14);
			} catch (NullPointerException e2){
				System.out.println("Please make sure there is data in the file and they are formatted correctly");
				System.exit(15);
			}
			trainReader.close();

			long end = System.nanoTime();
			System.out.println("total train time: " + (end - start));
			/** TRAINING DATA END **/
			System.out.println("training data finished");
			System.out.println("KDTree size " + test.size());
			System.out.println("actual intersection count " + intersectionCount);

			BufferedReader testReader = new BufferedReader(new FileReader(TestFile));
			String testLine = testReader.readLine();
			try{
				if(testLine.charAt(0)!='>'){
					throw new IOException();
				}
			}catch(NullPointerException e1){
					System.out.println("Please make sure there are contents in the file and that the file exists");
					System.exit(15);
				}

			testReader.close();
			File testFile = new File(TestFile);

			BufferedWriter outfasta = new BufferedWriter(new FileWriter(OutFile));
			Vector<Gene> testSequences = InputAndProcessGenesCategoryTest(testFile, kmer);
			System.out.println("We have " + testSequences.size() + " test sequences!");
			miss = 0;
			hit = 0;
			nothingThere = 0;
			searchPositive = 0;
			searchNegative = 0;
			inSet = 0;
			outSet = 0;
			HashMap<String, Double> correctIDHit = new HashMap<String, Double>();
			HashMap<String, String> outFastaSequenceMap = new HashMap<String, String>();

			long testStart = System.nanoTime();
			int q = 100;
			// thread pool.
			ExecutorService executor = Executors.newFixedThreadPool(numthread);
			for (int runs = 0; runs < testSequences.size() / q; runs++) {
				final int runs3 = runs;
				Runnable r = new Runnable() {
					public void run() {
						// total from test file is runs * sequences
						for (int sequences = runs3 * q; sequences < runs3 * q + q; sequences++) {
							if (sequences == 0)
								sequences = 2; // error happens if sequences = 0
												// or 1
							if (sequences % 10000 == 0)
								System.out.println("Currently testing sequence " + sequences);
							// trainhit.add(sequences,
							// testSequences.get(sequences).Cog);
							try {

								double[] gene = testSequences.get(sequences).kmerVector.clone();
								double sumGene = 0.0;
								for (int i2 = 0; i2 < gene.length; i2++) {
									sumGene += gene[i2];
								}
								for (int i2 = 0; i2 < gene.length; i2++) {
									gene[i2] = gene[i2] / sumGene;
								}

								// begin calculating the coordinates for the
								// test sequences
								Double[] coordArr = new Double[equationList.size()];
								for (int v = 0; v < equationList.size(); v++) {
									coordArr[v] = getPCAX(gene, equationList.get(v));
								}

								Double[] coord1 = coordArr;
								double[] coord = new double[coord1.length];
								for (int c = 0; c < coord1.length; c++) {
									coord[c] = coord1[c];
								}

								// random weird error happens without this
								if (coordArr[0].isNaN() || coordArr[1].isNaN())
									continue;

								// if it is not in the grid, look at the nearest
								// one.
								if (test.search(coord) == null) {
									// if the classification is the same, then
									// we are right
									// otherwise we are wrong
									if (test.nearest(coord).toString().equals(testSequences.get(sequences).Cog)) {
										incrementSearchPositive();
										// for ecoli classification
										String targetSequence = testSequences.get(sequences).sequence;
										// input the sequence for the out file
										// and summary file
										if (!outFastaSequenceMap.containsKey(targetSequence))
											outFastaSequenceMap.put(targetSequence, test.nearest(coord).toString() + " | " + testSequences.get(sequences).Cog);
										if (correctIDHit.containsKey(test.nearest(coord).toString())) {
											correctIDHit.put(test.nearest(coord).toString(),
													correctIDHit.get(test.nearest(coord).toString()) + 1);
										} else {
											correctIDHit.put(test.nearest(coord).toString(), 1.0);
										}
									} else {
										/**
										 * skipped a lot of commented code.
										 * please refer to below for missing
										 * commented out code
										 **/

										if (pegSet.containsKey(testSequences.get(sequences).Cog)) {
											inSet++;
										} else {
											outSet++;
										}
										incrementSearchNegative();
										// for ecoli classification
									}
								}
								// if we search and land on top of another
								// coordinate
								else if (test.nearest(coord).toString().equals(testSequences.get(sequences).Cog)) {
									/**
									 * skipped a lot of commented code. please
									 * refer to below for missing commented out
									 * code
									 **/
									incrementHit();
									// for ecoli classification
									String targetSequence = testSequences.get(sequences).sequence;
									// input the sequence for the out file and
									// summary file
									if (!outFastaSequenceMap.containsKey(targetSequence))
										outFastaSequenceMap.put(targetSequence, test.nearest(coord).toString() +" | " + testSequences.get(sequences).Cog);
									if (correctIDHit.containsKey(test.nearest(coord).toString())) {
										String target = test.nearest(coord).toString();
										correctIDHit.put(target, correctIDHit.get(target) + 1);
									} else {
										correctIDHit.put(test.nearest(coord).toString(), 1.0);
									}
								}
								// hits something, but not the correct values
								else if (test.search(coord).toString()
										.equals(testSequences.get(sequences).Cog) == false) {
									/**
									 * skipped a lot of commented code. please
									 * refer to below for missing commented out
									 * code
									 **/
									incrementMiss();
									// for ecoli classification
								}

							} catch (KeySizeException | ArrayIndexOutOfBoundsException e) {
								// Array Index Out Of Bounds is being caused by
								// all the test files being less than 100k
								// sequences in size

								if (e instanceof ArrayIndexOutOfBoundsException) {
									break;
								} else if (e instanceof NullPointerException) {
									continue;
								}else if (e instanceof KeySizeException){
									System.out.println("Please make sure the dimensions are the same");
									System.exit(16);
								}else{
									e.printStackTrace();
								}
							}

						}
					}
				};
				executor.execute(r);
			}

			// shutdown each thread as it finishes
			executor.shutdown();

			// wait for the threads to finish
			while (!executor.isTerminated()) {

			}

			double sum = 0;
			for (double value : correctIDHit.values()) {
				sum += value;
			}
			// writing summary file
			BufferedWriter summaryWriter = new BufferedWriter(new FileWriter("Summary.csv"));
			summaryWriter.write("Function ID, Percentage Used (in decimal), Number of hits,\n");
			for (String key : correctIDHit.keySet()) {
				summaryWriter.write(key.replaceAll(",", "") + "," + (correctIDHit.get(key) / sum) + ","
						+ correctIDHit.get(key) + "," + "\n");
			}
			summaryWriter.close();

			// writing output file
			for (String key : outFastaSequenceMap.keySet()) {
				outfasta.write(outFastaSequenceMap.get(key) + "\n");
				String[] splitSeq = key.split("(?<=\\G.{70})");
				for (String subseq : splitSeq) {
					outfasta.write(subseq + "\n");
				}
			}
			outfasta.close();
			long testEnd = System.nanoTime();
			System.out.println("Total time for testing: " + (testEnd - testStart));

			/** END OF TESTING **/
			// print statistics
			BufferedWriter logWriter = new BufferedWriter(new FileWriter("log.txt"));
			System.out.println("Hits: " + getHits());
			logWriter.write("Hits: " + getHits());
			System.out.println("Misclassified " + getMisses());
			logWriter.write("Misclassified " + getMisses());
			System.out.println("Search Positive: " + getSearchPositive());
			logWriter.write("Search Positive: " + getSearchPositive());
			System.out.println("Search Negative: " + getSearchNegative());
			logWriter.write("Search Negative: " + getSearchNegative());

		} catch (FileNotFoundException e1) {
			System.out.println("Please make sure the file exists");
			System.exit(10);

		} catch (IOException e1) {
			System.out.println(
					"Please make sure you are inputting either a FASTA or FEATURE file as training and a FASTA file as testing");
			System.exit(16);
		} 
	}

	private static int getSearchNegative() {
		return searchNegative;
	}

	private static int getSearchPositive() {
		return searchPositive;
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

	protected synchronized static void incrementSearchNegative() {
		searchNegative++;
	}

	protected synchronized static void incrementHit() {
		hit++;
	}

	protected synchronized static void incrementMiss() {
		miss++;
	}

	public static double[] parsePCAText(String text) {

		String[] arr = text.split("[kmer]");
		double[] returnarr = new double[arr.length];
		for (int i = 0; i < arr.length; i += 4) {
			if (i == 0) {
				String zeroSpot = arr[0].replaceAll(" ", "");
				String first = "";
				if (zeroSpot.contains("-")) {
					first = "-" + zeroSpot.split("-")[1];
				} else {
					first = zeroSpot.substring(zeroSpot.length() - 5, zeroSpot.length());
				}
				returnarr[Integer.valueOf(arr[i + 4].split("[+-]")[0]) - 1] = Double.valueOf(first);
				continue;
			} else if (arr[i].contains("-")) {
				String[] arr2 = arr[i].split("[-]");
				int posval = Integer.valueOf(arr[i + 4].split("[+-]")[0]);
				returnarr[posval - 1] = Double.valueOf(arr2[1]) * -1.0;

			}

			else if (arr[i].contains("+")) {
				String[] arr2 = arr[i].split("[+]");

				returnarr[Integer.valueOf(arr[i + 4].split("[+-]")[0]) - 1] = Double.valueOf(arr2[1]);
			} else if (!arr[i].contains("+-") && i == 0) {
				returnarr[Integer.valueOf(arr[i + 4].split("[+-]")[0]) - 1] = Double.valueOf(arr[i]);
			}
		}

		return returnarr;
	}

	private static double getPCAY(double[] kmer, double[] pcaArr) {
		// 0.414kmer13-0.39kmer7+0.379kmer1+0.379kmer16-0.333kmer10+0.309kmer4-0.294kmer11-0.294kmer6-0.054kmer9-0.054kmer14+0.039kmer8+0.039kmer3-0.023kmer12-0.023kmer2+0.005kmer5+0.005kmer15
		double retval = 0.0;

		for (int i = 0; i < kmer.length; i++) {
			retval += pcaArr[i] * kmer[i];
		}

		return retval;
		// return 0.414 * kmer[12] - 0.39 * kmer[6] + 0.379 * kmer[0] + 0.379 *
		// kmer[15] - 0.333 * kmer[9] + 0.309 * kmer[3]
		// - 0.294 * kmer[10] - 0.294 * kmer[5] - 0.054 * kmer[8] - 0.054 *
		// kmer[13] + 0.039 * kmer[7] + 0.039 * kmer[2]
		// - 0.023 * kmer[11] - 0.023 * kmer[1] + 0.005 * kmer[4] + 0.005 *
		// kmer[14];
	}

	protected static double getPCAX(double[] kmer, double[] pcaArr) {
		// -0.278kmer15-0.278kmer5-0.278kmer3-0.278kmer8-0.278kmer12-0.278kmer2-0.276kmer9-0.276kmer14-0.238kmer6-0.238kmer11-0.235kmer4-0.224kmer10-0.211kmer1-0.211kmer16-0.201kmer7-0.191kmer13

		double retval = 0.0;

		for (int i = 0; i < kmer.length; i++) {
			retval += pcaArr[i] * kmer[i];
		}

		return retval;

		// return -0.278 * kmer[14] - 0.278* kmer[4] - 0.278*kmer[2] - 0.278 *
		// kmer[7] - 0.278 * kmer[11] - 0.278 * kmer[1] - 0.276 * kmer[8]
		// - 0.276 * kmer[13] - 0.238 * kmer[5] - 0.238 * kmer[10] - 0.235 *
		// kmer[3] - 0.224* kmer[9] - 0.211 * kmer[0]
		// - 0.211 * kmer[15] - 0.201 * kmer[6] - 0.191 * kmer[12];
	}

	/**
	 * The current way to take in and process genes from a file
	 * 
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static Vector<Gene> InputAndProcessGenesCategoryTest(File f, int kmerToDo) throws IOException {
		boolean first = true;
		String sequence = "";
		Vector<Gene> storage = new Vector<Gene>();
		String id = "";
		BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
		String line = "";
		while ((line = bufferedReader.readLine()) != null) {
			if (line.contains(">")) {
				if (first)
					first = false;
				else {
					if (id.contains("USS-DB") || id.contains("hypothetical") || id.contains("Hypothetical")) {
						id = line;
						sequence = "";
						continue;
					}
					if (sequence.length() < 100) {
						sequence = "";
						id = line;
						continue;
					}
					sequence = replaceNucs(sequence);
					String origSequence = sequence;
					sequence = sequence.substring(60, sequence.length() - 2);

					storage.add(new Gene(id, processSequencebyKmer(sequence, kmerToDo), origSequence));

					id = line;
					sequence = "";
				}

			} else {
				sequence += line;
			}

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
	public static Vector<Gene> InputAndProcessGenesLine(File f, double[] xEQN, double[] yEQN, int kmerToDo)
			throws IOException {
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
			// storage.add(new Gene(id, processSequencebyKmer(sequence,
			// kmerToDo)));
			storage.add(new Gene(id, getPCAX(processSequencebyKmer(sequence, kmerToDo), xEQN),
					getPCAY(processSequencebyKmer(sequence, kmerToDo), yEQN)));

			count++;
			if (count > TOTAL_VALS) {
				// if (count > 1000) {
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

	public static Vector<Gene> InputAndProcessGenes(File f, int kmerToDo) throws IOException {
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
		// THIS NORMALIZES!!
		double xCount = 0;
		double yCount = 0;
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
		if(!sequence.matches("(?i)^[a-z]*$")){
			try {
				throw new Exception();
			} catch (Exception e) {
				System.out.println(sequence);
				System.out.println("Please make sure that your sequences are formatted properly.  The sequences should only support letters.  No numbers, spaces or other symbols allowed");
				System.exit(12);
			}
		}
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
				System.out.println("replaced   i=" + i + "      " + replaceNucs(sequence));
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
