package KScope;

import java.util.Arrays;

public class StringSplitter {
	
	public static void main(String[] args){
		String arr = Arrays.toString(
			    "Thequickbrownfoxjumps".split("(?<=\\G.{4})")
			);
		
		System.out.println(arr);
	}

}
