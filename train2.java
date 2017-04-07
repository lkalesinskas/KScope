import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.List;
import java.util.Vector;

import JavaMI.MutualInformation;

public class train2 {
	public static void main (String[] args) throws IOException {
		train2 a = new train2();
		a.testParser();
	}
	public static BufferedWriter out;
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
	
	public static void processSequence(String identifier, String sequence) throws IOException {
		if (identifier.isEmpty()==false) {
			out.write(String.valueOf(uniqueID));
			out.newLine();
			uniqueID++;
			out.write(identifier);
			out.newLine();
			out.write(sequence);
			out.newLine();
			//PrintStream out = new PrintStream (new File ("trainingData.discoplanes"));
			//out.println(identifier);
			//out.println(sequence);
			int start = 0;
			int end = 0;
			int hinge = 0;
			List<Double> storage1;
			List<Double> storage2;
			double x = 0;
			double y = 0;
			double distance = 0;
			for (int i = 2; i<=kmerMax; i++) {
				nmer = i;
				numShifts = 64 - (2*(nmer-1));
		        numShiftsMinus = numShifts - 2;
		        double[] comps = runGetKmers(sequence);
		        start = breakValues.get(hinge);
		        end = breakValues.get(hinge+1);
		        storage1 = fakes1.subList(start+1, end);
		        storage2 = fakes2.subList(start+1, end);
		        //System.out.println(storage1.size());
		        //System.out.println(comps.length);
		        //System.out.println("________________________");
		        hinge++;
		        x = getR(comps, storage1);
		        y = getR(comps, storage2);
		        distance = (Double) Math.sqrt((x*x)+(y*y));
		        out.write(x + ", " + y + ", " + distance);
		        out.newLine();
			}
		}
	}
	
	public static double getRMI(double[] x, List<Double> y2) {
		double[] y = new double[y2.size()];
    	for (int i = 0; i<y2.size(); i++) {
    		y[i] = y2.get(i)*1000;
    	}
    	
    	for (int i = 0; i<x.length; i++) {
    		x[i] = x[i]*1000;
    	}
    	//System.out.println(x[0] + "," +  y[0]);
    	return MutualInformation.calculateMutualInformation(x, y);
	}
	
	
	
	
	public static void testParser() throws IOException {
		boolean first = true;
		String sequence = "";
		String id = "";
		BufferedReader bufferedReader = new BufferedReader(new FileReader("trainingData.txt"));
		String line = "";
		while ((line = bufferedReader.readLine())!=null){
                if (line.contains(">")) {
                    if (first)
                        first = false;
                    else
                    	sequence = replaceNucs(sequence);
                    	processSequence(id, sequence);
                    	id = "";
                    	sequence = "";
                    id = line.replace("'", "").replaceAll(",", "");
                } else {
                    sequence+=line;
                }
            }
        System.out.println("x");
        }
    

	
	public static Vector<Double> fakes1;
	public static Vector<Double> fakes2;
	public static Vector<Integer> breakValues;
	
	public train2 () throws IOException {
		out = new BufferedWriter(new FileWriter("testPearsonReads2.train"));
		breakValues = new Vector<Integer>(); 
		breakValues.addElement(0);
		int total = 0;
		for (int i = 2; i<kmerMax+1; i++) {
			breakValues.add(total + (int)Math.pow(4, i)+1);
			total = total + (int)Math.pow(4, i)+1; 
		}
		fakes1 = grabFakes("xGenomeTrained.out");
		fakes2 = grabFakes("yGenomeTrained.out");
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
		testParser();
		System.exit(0);
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
	
    public static Double getR (double[] comps, List<Double> y2) {
    	Double[] y = new Double[y2.size()];
    	for (int i = 0; i<y2.size(); i++) {
    		y[i] = y2.get(i);
    	}
        double xAverage = 0;
        double yAverage = 0;
        for (int i =0; i<comps.length; i++) {
            xAverage += comps[i];
            yAverage += y[i];
        }
        xAverage = xAverage/comps.length;
        yAverage = yAverage/y.length;
        double xy = 0;
        double xSq = 0;
        double ySq = 0;
        for (int i = 0; i<comps.length; i++) {
            comps[i] = (comps[i] - xAverage);
            y[i] = (y[i] - yAverage);
            xy += (comps[i]*y[i]);
            xSq += comps[i]*comps[i];
            ySq += y[i]*y[i];
        }
        return (Double) (xy/Math.sqrt(xSq*ySq));
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