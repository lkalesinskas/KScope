package KScope;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class split100k {
	
	public static void main(String[] args) throws IOException{
		
		int[] counts = new int[100];
		
		/**    generate 100 100k test files    **/
		//  make 100 file writers
		ArrayList<BufferedWriter> writeList = new ArrayList<BufferedWriter>();
		for(int i = 0; i < 100; i ++){
			BufferedWriter testWriter = new BufferedWriter(new FileWriter("test"+i+".ffn"));
			writeList.add(testWriter);
		}
		
		//  go through the giant test set (currently testOut5) and keep reading it until we have 100 files with 100k values in them
		RandomAccessFile separatedReader = new RandomAccessFile(new File("D:\\Larry Projects\\KSCOPE\\KScope2\\testOut5.ffn"), "rw");
		String separatedLine = "";
		boolean foundSpot = false;
		
		//  while there is something with less than 100k values in it
		while(lessThan100k(counts)){
			separatedLine=separatedReader.readLine();
			foundSpot=false;
			//  while a random spot with less than 100k is not found
			while(!foundSpot && lessThan100k(counts)){
				//  get the random spot
				int rand = (int)(Math.random()*100);
				// increased to 150k because not enough values were being put into the test file
				if(counts[rand] < 150000){
					
					//  put the values into the random file
					String separatedID = separatedLine;
					String separatedSequence = separatedReader.readLine();
					counts[rand] ++;
					writeList.get(rand).write(separatedID+"\n");
					writeList.get(rand).write(separatedSequence.toUpperCase()+"\n");
					foundSpot=true;
				}
			}
			
			//  check to see if at the end of the separated big file.
			long pos = separatedReader.getFilePointer();
			if(separatedReader.readLine() == null){
				separatedReader.seek(0);
			}
			else{
				separatedReader.seek(pos);
			}
		}
		separatedReader.close();
	}
	
	private static boolean lessThan100k(int[] counts) {
		for(int i = 0; i < counts.length; i ++){
			//  made 150k because 100k was yielding more like 99980
			if(counts[i] < 150000) return true;
		}
		return false;
	}

}
