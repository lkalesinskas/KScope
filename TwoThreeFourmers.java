package KScope;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;

public class TwoThreeFourmers {

	public static void main(String[] args) throws IOException {
		RandomAccessFile twomerReader = new RandomAccessFile(new File("outfile2mer.fasta"), "rw");
		RandomAccessFile threemerReader = new RandomAccessFile(new File("outfile3mer.fasta"), "rw");
		RandomAccessFile fourmerReader = new RandomAccessFile(new File("outfile4mer.fasta"), "rw");
		BufferedWriter writer234 = new BufferedWriter(new FileWriter("outfile234combo.csv"));
		
		BufferedReader reader2 = new BufferedReader(new FileReader("TestOut8Span2merPCA.fasta"));
		BufferedReader reader3 = new BufferedReader(new FileReader("testOut6.fasta"));
		BufferedReader reader4 = new BufferedReader(new FileReader("TestOut7Span4merPCA.fasta"));
		
		System.out.println("inserting into hashset");
		ArrayList<String> testSet = new ArrayList<String>();
		String line = "";
		for(int i = 0; (line = reader2.readLine()) != null;){
			if(line.contains(">")){
				testSet.add(line);
				i ++;
			}
			if(i > 100001) break;
		}
		for(int i = 0; (line = reader3.readLine()) != null;){
			if(line.contains(">")){
				if(!testSet.contains(line))
					testSet.add(line);
				i ++;
			}
			if(i > 100001) break;
		}
		for(int i = 0; (line = reader4.readLine()) != null;){
			if(line.contains(">")){
				if(!testSet.contains(line)) 
					testSet.add(line);
				i ++;
			}
			if(i > 100001) break;
		}
		
		System.out.println("done inserting into hashset");
		System.out.println("hashset size " + testSet.size());
		String line2 = "";
		String line3 = "";
		String line4 = "";
		int line2mer = 0;
		int line3mer = 0;
		int line4mer = 0;
		for(String key : testSet){
			writer234.write(key.replaceAll(",", ""));
			//  find it in 2mers
			writer234.write(",");
			for(line2mer = 0; (line2 = twomerReader.readLine()) != null; line2mer ++){
				if(line2.contains(">")){
					if(line2.equals(key)){
						writer234.write(Integer.toString(line2mer));
						break;
					}
				}
			}
			//  find it in 3mers
			writer234.write(",");
			for(line3mer = 0; (line3 = threemerReader.readLine()) != null; line3mer ++){
				if(line3.contains(">")){
					if(line3.equals(key)){
						writer234.write(Integer.toString(line3mer));
						break;
					}
				}
			}
			//  find it in 4mers
			writer234.write(",");
			for(line4mer = 0; (line4 = fourmerReader.readLine()) != null; line4mer ++){
				if(line4.contains(">")){
					if(line4.equals(key)){
						writer234.write(Integer.toString(line4mer));
						break;
					}
				}
			}
			writer234.write("\n");
			//  reset to beginning of the files
			twomerReader.seek(0);threemerReader.seek(0);fourmerReader.seek(0);
		}
		
//		while((line2 = twomerReader.readLine()) != null){
//			long position = twomerReader.getFilePointer();
//			if(twomerReader.readLine() == null){
//				System.out.println("poop");
//			}
//			else{
////				System.out.println(twomerReader.getFilePointer());
//				twomerReader.seek(position);
//			}
//		}
//		
		System.out.println("done");
		
//		
//		for(line2mer = 0; (line2 = twomerReader.readLine()) != null; line2mer++){
//			boolean found = false;
//			if(line2.contains(">")){
//				for(line3mer = 0; (line3 = threemerReader.readLine()) != null; line3mer ++){
//					if(line3.contains(">")){
//						if(line2.equals(line3)){
//							writer234.write(line2.replaceAll(",", "") +", from 2mer line " + line2mer + ", " + line3.replaceAll(",", "") +", from 3mer line " + line3mer + ",\n");
//							found = true;
//							break;
//						}
//					}
//				}
//				
//				if(!found){
//					for(line4mer = 0; (line4 = fourmerReader.readLine()) != null; line4mer ++){
//						if(line4.contains(">")){
//							if(line2.equals(line4)){
//								writer234.write(line2.replaceAll(",", "") +", from 2mer line " + line2mer + ", " + line4.replaceAll(",", "") + ", from 4mer line " + line4mer +",\n");
//							}
//						}
//					}
//				}
//			}
//			threemerReader.seek(0); fourmerReader.seek(0);
//		}
//		
//		System.out.println("finished with 2 and three mers");
//		threemerReader.seek(0); fourmerReader.seek(0);
//		
//		for(line3mer = 0; (line3 = threemerReader.readLine()) != null; line3mer ++){
//			if(line3.contains(">")){
//				for(line4mer = 0; (line4 = fourmerReader.readLine()) != null; line4mer ++){
//					if(line4.contains(">")){
//						if(line3.equals(line4)){
//							writer234.write(line3.replaceAll(",", "") + ", from 3mer line " + line3mer + ", " + line4.replaceAll(",", "") + ", from 4mer line " + line4mer + ",\n");
//							break;
//						}
//					}
//				}
//			}
//			fourmerReader.seek(0);
//		}
		System.out.println("finished all");
		twomerReader.close();
		threemerReader.close();
		fourmerReader.close();
		writer234.close();
		reader2.close();
		reader3.close();
		reader4.close();
	}

}
