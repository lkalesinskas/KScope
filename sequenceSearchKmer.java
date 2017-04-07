import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JOptionPane;

public class sequenceSearchKmer {

	public static PrintStream out;
	public static int nmer = 0;
	public static int kmerMax = 9;
	public static int numShifts = 0;
	public static int numShiftsMinus = 0;
	public static int uniqueID = 1000;
	public static String replaceNucs(String sequence) {
        sequence = sequence.replaceAll("M", "N");
        sequence = sequence.replaceAll("K", "N");
        sequence = sequence.replaceAll("R", "N");
        sequence = sequence.replaceAll("S", "N");
        sequence = sequence.replaceAll("W", "N");
        sequence = sequence.replaceAll("Y", "N");
        sequence = sequence.replaceAll("B", "N");
        sequence = sequence.replaceAll("D", "N");
        sequence = sequence.replaceAll("H", "N");
        sequence = sequence.replaceAll("V", "N");
        sequence = sequence.toUpperCase();
        return sequence;
    }
	
	public static BigDecimal[] processSequence(String identifier, String sequence, int number) {
		if (identifier.isEmpty()==false) {
			//PrintStream out = new PrintStream (new File ("trainingData.discoplanes"));
			//out.println(identifier);
			//out.println(sequence);
			int start = 0;
			int end = 0;
			int hinge = 0;
			List<Double> storage1;
			List<Double> storage2;
			List<Double> storage3;
			BigDecimal x = null;
			BigDecimal y = null;
			BigDecimal z = null;
			double distance = 0;
				nmer = number;
				hinge = number - 2;
				numShifts = 64 - (2*(nmer-1));
		        numShiftsMinus = numShifts - 2;
		        double[] comps = runGetKmers(sequence);
		        //System.out.println(breakValues);
		        start = breakValues.get(hinge);
		        end = breakValues.get(hinge+1);
		        //System.out.println(start+1);
		       // System.out.println(end);
		       // System.out.println("Fakes" + fakes1.size());
		        storage1 = fakes1.subList(start+1, end);
		        //System.out.println(storage1.size());
		        storage2 = fakes2.subList(start+1, end);
		        //storage3 = fakes3.subList(start+1, end);
		        //System.out.println(storage1.size());
		        //System.out.println(comps.length);
		        //System.out.println("________________________");
		        x = new BigDecimal(getR(comps, storage1));
		        y = new BigDecimal(getR(comps, storage2));
		        //z = new BigDecimal(getR(comps, storage3));
		        //storage1.clear();
		        //storage2.clear();
		        BigDecimal[] abde = {x,y, z};
		        return abde;
		}
		return null;
	}
	
	/*
	public static void testParser() throws IOException {
		boolean first = true;
		String sequence = "";
		String id = "";
        try (Scanner sc = new Scanner(new File("GenomeSplit.fna"))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.charAt(0) == '>') {
                    if (first)
                        first = false;
                    else
                    	//if (id.contains("Bacteria")){
                    	sequence = replaceNucs(sequence);
                    	processSequence(id, sequence);
                    	id = "";
                    	sequence = "";
                    	//}
                    id = line;
                } else {
                    sequence+=line;
                }
            }
        }
        System.out.println("x");
    }
*/
	
	public static Vector<Double> fakes1;
	public static Vector<Double> fakes2;
	//public static Vector<Double> fakes3;

	public static Vector<Integer> breakValues;
	
	public sequenceSearchKmer () throws IOException {
		out = new PrintStream(new File("testPutontiAxis.train"));
		breakValues = new Vector<Integer>(); 
		breakValues.addElement(0);
		int total = 0;
		for (int i = 2; i<kmerMax+1; i++) {
			breakValues.add(total + (int)Math.pow(4, i)+1);
			total = total + (int)Math.pow(4, i)+1; 
		}
		fakes1 = grabFakes("xGenomeTrained.out");
		fakes2 = grabFakes("yGenomeTrained.out");
		//fakes3 = grabFakes("yGenomeTrained.out");
		int fake1Sum = 0;
		int fake2Sum = 0;
		//int fake3Sum = 0;
		for (int i = 0; i<breakValues.size()-1; i++) {
			for (int j = breakValues.get(i)+1; j < breakValues.get(i+1); j++) {
				fake1Sum += fakes1.get(j);
				fake2Sum += fakes2.get(j);
				//fake3Sum += fakes3.get(j);
			}
			for (int j = breakValues.get(i)+1; j < breakValues.get(i+1); j++) {
				fakes1.setElementAt(fakes1.get(j)/fake1Sum, j);
				fakes2.setElementAt(fakes2.get(j)/fake2Sum, j);
				//fakes3.setElementAt(fakes3.get(j)/fake3Sum, j);

			}
			fake1Sum = 0;
			fake2Sum = 0;
			//fake3Sum = 0;
		}
	}

	public static Vector<Double> grabFakes(String file) throws IOException {
		InputStream inputStream = null;
		BufferedReader br = null;
		inputStream = new FileInputStream(file);
		br = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder sb = new StringBuilder();
		Vector<Double> fake1 = new Vector<Double>();
		String line;
		while ((line = br.readLine()) != null) {
			if (line.contains("k")) {
				fake1.add((double) -1);
			}
			else {
				fake1.add(Double.parseDouble(line.trim()));
			}
		}
		br.close();
		return fake1;
	}
	
    public static double getR (double[] x, List<Double> y2) {
    	//System.out.println(y2.size());
    	double[] y = new double[y2.size()];
    	for (int i = 0; i<y2.size(); i++) {
    		y[i] = y2.get(i);
    	}
        double xAverage = 0;
        double yAverage = 0;
       // System.out.println(x.length + "!");
        for (int i =0; i<x.length; i++) {
            xAverage += x[i];
            yAverage += y[i];
        }
        xAverage = xAverage/x.length;
        yAverage = yAverage/y.length;
        double xy = 0;
        double xSq = 0;
        double ySq = 0;
        for (int i = 0; i<x.length; i++) {
            x[i] = (x[i] - xAverage);
            y[i] = (y[i] - yAverage);
            xy += (x[i]*y[i]);
            xSq += x[i]*x[i];
            ySq += y[i]*y[i];
        }
        return (xy/Math.sqrt(xSq*ySq));
    }
	
	public static double[] runGetKmers (String sequence) {
        double[] kmerComp = new double[(int) Math.pow(4, nmer)];
        String[] toRun = sequence.split("N");
        for (int i =0; i<toRun.length; i++) {
            if (toRun[i].length()>=nmer) {
                kmerComp = getKmers(toRun[i], kmerComp);
                }
            }
        double sum = 0;
        for (int i = 0; i<kmerComp.length; i++) {
        	sum += kmerComp[i];
        }
        for (int i = 0; i<kmerComp.length; i++) {
        	kmerComp[i] = kmerComp[i]/sum;
        }
        return kmerComp;
    }
	
	public static double[] getKmers(String sequence, double[] kmerComp) {
        //initialize first set of kmers
        Long temp = null;
        Long full = Long.parseUnsignedLong("0");
        int i = 0;
        for (i=0; i<nmer; i++) {
            temp  = nucToNum(sequence.charAt(i));
            full = full + temp;
            if (i<nmer-1) {
                full = full<<2;
            }
        }
        //add it and its reverse kmer to count array
        kmerComp[full.intValue()] += 1;
        kmerComp[reverser(full)] += 1;

        //delete first nucleotide and add to the end of it
        //add it and its reverse complement to count array
        while (i<sequence.length()) {
            temp = nucToNum(sequence.charAt(i));
            full = fancyShift(full);
            try{
            	full = full + temp;
                kmerComp[full.intValue()] += 1;
                kmerComp[reverser(full)] += 1;
                i++;
            }
            
            //TODO:What the hell is going on here?
            catch(NullPointerException e) {
            	break;
            }
        }
        //return kmerComposition array
        return kmerComp;
    }
	
	public static Long fancyShift (Long a) {
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
        if (length%2==1) {
            rc = '0' + rc;
        }
        char[] twosies = rc.toCharArray();
        String newString = "";
        for (int i = 0; i<twosies.length; i=i+2) {
            if (twosies[i] == '0' && twosies[i+1] == '1') {
                twosies[i] = '1';
                twosies[i+1] = '0';
            }
            else if (twosies[i] == '1' && twosies[i+1] == '0') {
                twosies[i] = '0';
                twosies[i+1] = '1';
            }
            newString += twosies[i];
            newString += twosies[i+1];
        }
        Long l = parseLong(newString, 2);
        return l.intValue();
    }
    
    private static long parseLong(String s, int base) {
        return new BigInteger(s, base).longValue();
    }
    //Silly initializations for optimization
    public static Long aLong = Long.parseUnsignedLong("0");
    public static Long cLong = Long.parseUnsignedLong("1");
    public static Long gLong = Long.parseUnsignedLong("2");
    public static Long tLong = Long.parseUnsignedLong("3");

    //turns a nucleotide character to a binary representation
    public static Long nucToNum (char a) {
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