import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JOptionPane;

public class trainAxes {	
	public static void main (String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader("zGenome.txt"));
		String fileLine = "";
		String sequence = "";
		System.out.println("Grabbing Seq");
        while ((fileLine = reader.readLine()) != null) {
        	sequence += fileLine.trim();
        	if (sequence.length()%20000==0) {
        		System.out.println(sequence.length());
        	}
        }
        System.out.println(sequence.length());
		processSequence(sequence);
		
	}
	
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
	
	
	public static void processSequence(String sequence) throws FileNotFoundException {		
			int start = 0;
			int end = 0;
			int hinge = 0;
			List<Float> storage1;
			List<Float> storage2;
			float x = 0;
			float y = 0;
			float distance = 0;
			PrintStream out = new PrintStream(new File("zGenomeTrained.out"));
			for (int i = 2; i<=kmerMax; i++) {
				nmer = i;
				out.println("k" + nmer);
				numShifts = 64 - (2*(nmer-1));
		        numShiftsMinus = numShifts - 2;
		        float[] comps = runGetKmers(sequence);
		        for (int j = 0; j < comps.length; j++) {
		        	out.println((int)comps[j]);
		        }
		        //start = breakValues.get(hinge);
		        //end = breakValues.get(hinge+1);
		       // storage1 = fakes1.subList(start+1, end);
		        //storage2 = fakes2.subList(start+1, end);
		        //System.out.println(storage1.size());
		        //System.out.println(comps.length);
		        //System.out.println("________________________");
		        //hinge++;
		        //x = getR(comps, storage1);
		       // y = getR(comps, storage2);
		        //distance = (float) Math.sqrt((x*x)+(y*y));
		       // out.println(x + ", " + y + ", " + distance);
			}
			out.close();
	}
	
	

	
	public static Vector<Float> fakes1;
	public static Vector<Float> fakes2;
	public static Vector<Integer> breakValues;
	
	public trainAxes () throws IOException {
		out = new PrintStream(new File("PseudoGenomes.train"));
		breakValues = new Vector<Integer>(); 
		breakValues.addElement(0);
		int total = 0;
		for (int i = 2; i<kmerMax+1; i++) {
			breakValues.add(total + (int)Math.pow(4, i)+1);
			total = total + (int)Math.pow(4, i)+1; 
		}
		fakes1 = grabFakes("GC25.ref");
		fakes2 = grabFakes("GC50.ref");
		int fake1Sum = 0;
		int fake2Sum = 0;
		
		for (int i = 0; i<breakValues.size()-1; i++) {
			for (int j = breakValues.get(i)+1; j < breakValues.get(i+1); j++) {
				fake1Sum += fakes1.get(j);
				fake2Sum += fakes2.get(j);
			}
			for (int j = breakValues.get(i)+1; j < breakValues.get(i+1); j++) {
				fakes1.setElementAt(fakes1.get(j)/fake1Sum, j);
				fakes2.setElementAt(fakes2.get(j)/fake2Sum, j);
			}
			fake1Sum = 0;
			fake2Sum = 0;
		}
		System.exit(0);
	}

	public static Vector<Float> grabFakes(String file) throws IOException {
		InputStream inputStream = null;
		BufferedReader br = null;
		inputStream = new FileInputStream(file);
		br = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder sb = new StringBuilder();
		Vector<Float> fake1 = new Vector<Float>();
		String line;
		while ((line = br.readLine()) != null) {
			if (line.contains("k")) {
				fake1.add((float) -1);
			}
			else {
				fake1.add(Float.parseFloat(line.trim()));
			}
		}
		br.close();
		return fake1;
	}
	
    public static float getR (float[] x, List<Float> y2) {
    	float[] y = new float[y2.size()];
    	for (int i = 0; i<y2.size(); i++) {
    		y[i] = y2.get(i);
    	}
        float xAverage = 0;
        float yAverage = 0;
        for (int i =0; i<x.length; i++) {
            xAverage += x[i];
            yAverage += y[i];
        }
        xAverage = xAverage/x.length;
        yAverage = yAverage/y.length;
        float xy = 0;
        float xSq = 0;
        float ySq = 0;
        for (int i = 0; i<x.length; i++) {
            x[i] = (x[i] - xAverage);
            y[i] = (y[i] - yAverage);
            xy += (x[i]*y[i]);
            xSq += x[i]*x[i];
            ySq += y[i]*y[i];
        }
        return (float) (xy/Math.sqrt(xSq*ySq));
    }
	
	public static float[] runGetKmers (String sequence) {
        float[] kmerComp = new float[(int) Math.pow(4, nmer)];
        String[] toRun = sequence.split("N");
        for (int i =0; i<toRun.length; i++) {
            if (toRun[i].length()>=nmer) {
                kmerComp = getKmers(toRun[i], kmerComp);
                }
            }
        float sum = 0;
        for (int i = 0; i<kmerComp.length; i++) {
        	sum += kmerComp[i];
        }
        for (int i = 0; i<kmerComp.length; i++) {
        	kmerComp[i] = kmerComp[i];
        }
        return kmerComp;
    }
	
	public static float[] getKmers(String sequence, float[] kmerComp) {
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