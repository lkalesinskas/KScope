import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Scanner;
import java.util.Vector;

public class main {
	public static int nmer = 0;
	public static int kmerMax = 9;
	public static int numShifts = 0;
	public static int numShiftsMinus = 0;
	public static int kmerToDo = 0;
	
	public static void main(String[] args) throws IOException {
		kmerToDo = 3;
		
		// Files for Axis
		File genome1 = new File("Genome1.fna");
		File genome2 = new File("Genome2.fna");
		File geneFile = new File("trainingData.txt");
		// Getting sequence
		String sequence1 = inputGenomeSequence(genome1);
		String sequence2 = inputGenomeSequence(genome2);
		// Gets KmerComposition Arrays of each Axis
		double[] xAxis = processSequencebyKmer(sequence1, kmerToDo);
		double[] yAxis = processSequencebyKmer(sequence2, kmerToDo);

		for (int i = 0; i < xAxis.length; i++) {
			System.out.print(yAxis[i] + ", ");
		}
		System.out.println();
		for (int i = 0; i < xAxis.length; i++) {
			System.out.print(xAxis[i] + ", ");
		}
		
		//Inputs and Stores all genes in storage vector
		Vector<Gene> storage = InputAndProcessGenes(geneFile);
		
		DiscoPlane plane = new DiscoPlane(10000);
		
		long startTime = System.currentTimeMillis();
		//grab vectors (gene=gene2) from gene
		//grab x and y
		//correlate them and add them to plane
		System.out.println();
		for (int i = 1; i<storage.size(); i++) {
			double[] gene = storage.get(i).kmerVector.clone();
			double[] gene2 = storage.get(i).kmerVector.clone();
			double[] x = xAxis.clone();
			double[] y = yAxis.clone();
			//adding them to plane
			BigDecimal b = new BigDecimal(getR(gene, x));
			BigDecimal c = new BigDecimal(getR(gene2, y));
			plane.placeSequence(new Point(b,c), storage.get(i).Cog);
		}
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println(estimatedTime);
		plane.checkLegos();
		
		/*for (int i = 0; i < xAxis.length; i++) {
			System.out.print(yAxis[i] + ", ");
		}
		System.out.println();

		for (int i = 0; i < xAxis.length; i++) {
			System.out.print(xAxis[i] + ", ");
		}*/
		/*for (int i = 0; i<storage.size(); i++) {
			System.out.println(storage.get(i).Cog);
			for (int j = 0; j<storage.get(i).kmerVector.length; j++) {
				System.out.print(storage.get(i).kmerVector[j] + ", ");
			}
		}*/
		System.exit(0);

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
				//TODO: Filter out unneeded data in ID
				
				storage.add(new Gene(id, processSequencebyKmer(sequence, kmerToDo)));
				count++;
				if (count>100000) {
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
				stringbuilder.append("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
			}
		}
		sequence = stringbuilder.toString();
		bufferedReader.close();
		return sequence;
	}

	public static double[] processSequencebyKmer(String sequence, int mermer) throws FileNotFoundException {
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
