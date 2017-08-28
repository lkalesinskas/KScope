package KScope;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

public class FixTrain2merFasta {
	
	public static void main(String[] args) throws IOException{
		BufferedReader trainReader = new BufferedReader(new FileReader("TrainOut8Span2merPCA.fasta"));
		BufferedWriter trainWriter = new BufferedWriter(new FileWriter("TrainOut82mer.fasta"));
		
		
		String line = "";
		String id = "";
		String sequence = "";
		boolean first = true;
		int count = 0;
		HashMap<double[],HashMap<String, Integer>> sameMap = new HashMap<double[], HashMap<String, Integer>>();
		/**  the writers for train and test out that will be used for the spanning set   **/
		while( (line = trainReader.readLine()) != null){

/**   INSERTING INTO TREE OR STORAGE VECTOR    **/
			//  dealing with the first line
			if(line.contains(">")){
				if(first){
					count++;
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
					id += "~~" + count;
					trainWriter.write(id+"\n");
					String[] splitSeq = sequence.split("(?<=\\G.{70})");
					for(String str : splitSeq){
						trainWriter.write(str+"\n");
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
		trainReader.close();
		trainWriter.close();
		
		
//		boolean first = true;
//		RandomAccessFile fastaReader = new RandomAccessFile(new File("Train 2mer miss.fasta"), "rw");
//		BufferedWriter fixed = new BufferedWriter(new FileWriter("Train 2mer miss fixed.fasta"));
//		String line = "";
//		String id = "";
//		String sequence = "";
//		
//		while((line = fastaReader.readLine()) != null){
//			if(line.contains(">")){
//				if(first){
//					id = line;
//					first = false;
//					continue;
//				}
//				while((line = fastaReader.readLine()).length() == 70){					
//					sequence += line;
//				}
//				long pos = fastaReader.getFilePointer();
//				if((line = fastaReader.readLine()).contains(">")){
//					fastaReader.seek(pos);
//					continue;
//				}
//				else{
//					line = fastaReader.readLine();
//					sequence += line;
//					long pos2 = fastaReader.getFilePointer();
//					while(!(line = fastaReader.readLine()).contains(">")){
//						pos2 = fastaReader.getFilePointer();
//						if((line = fastaReader.readLine()).contains(">")){
//							fastaReader.seek(pos2);
//							break;
//						}
//						fastaReader.seek(pos2);
//						break;
//					}
//				}
//				
//			}
//			
//			fixed.write(id +"\n");
//			String[] splitSeq = sequence.split("(?<=\\G.{70})");
//			for(String str : splitSeq){
//				fixed.write(str+"\n");
//			}
//			
//			sequence = "";
//			id = line;
//		}
	}

}
