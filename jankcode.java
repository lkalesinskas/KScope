package KScope;

import java.util.ArrayList;
import java.util.List;

public class jankcode {
	
	public static void main(String[] args){
		String pcatext = "0.414kmer13-0.39kmer7+0.379kmer1+0.379kmer16-0.333kmer10+0.309kmer4-0.294kmer11-0.294kmer6-0.054kmer9-0.054kmer14+0.039kmer8+0.039kmer3-0.023kmer12-0.023kmer2+0.005kmer5+0.005kmer15";
//		double[] pca = parsePCAText(pcatext);
		double[] test = parsePCATExt(pcatext);
		for(int i = 0; i < test.length; i ++){
			System.out.println(test[i]);
		}
		System.out.println("fifteenth spot = " + test[12]);
	}
	
//	private static double[] parsePCAText(String text) {
//		// -0.278kmer15-0.278kmer5-0.278kmer3-0.278kmer8-0.278kmer12-0.278kmer2-0.276kmer9-0.276kmer14-0.238kmer6-0.238kmer11-0.235kmer4-0.224kmer10-0.211kmer1-0.211kmer16-0.201kmer7-0.191kmer13
//		text = text.trim();
//		double[] PCAArray = new double[text.split("kmer").length];
//		int pos = 0;
//		for(; pos < text.length();){
//			double val = 0.0;
//			if(Character.toString(text.charAt(pos)).matches("[-]")){
//				int start = pos + 1;
//				int end = pos + 5;
//				String doubleVal = text.substring(start, end);
//				val = Double.valueOf(doubleVal);
//				pos += 5;
//			}
//			if(Character.toString(text.charAt(pos+5)).matches("[1-9]")){
//				int posinarr = 0;
//				if(Character.toString(text.charAt(pos+6)).matches("[1-9]")){
//					posinarr = Integer.valueOf(text.substring(pos+5, pos+6));
//				}
//				else{
//					posinarr = Integer.valueOf(text.substring(pos+5, pos+5));
//				}
//			}
//		}
//		
//		return null;
//	}
	
	public static double[] parsePCATExt(String text){
		
		String[] arr = text.split("[kmer]");
		double[] returnarr = new double[arr.length];
		for(int i = 0; i < arr.length; i += 4){
			System.out.println("pos: " + i +" " +arr[i]);
			if(arr[i].contains("-")){
				String[] arr2 = arr[i].split("[-]");
//					System.out.println(Integer.valueOf(arr[i+4].split("[+-]")[0]));
					int posval = Integer.valueOf(arr[i+4].split("[+-]")[0]);
					returnarr[posval - 1] = Double.valueOf(arr2[1]) * -1.0;
				
			}
			
			else if(arr[i].contains("+")){
				String[] arr2 = arr[i].split("[+]");
				System.out.println();
					returnarr[Integer.valueOf(arr[i+4].split("[+-]")[0]) - 1] = Double.valueOf(arr2[1]);
			}
			
			else if(!arr[i].contains("+-") && i == 0){
				System.out.println("hitting at pos " + i);
				returnarr[Integer.valueOf(arr[i+4].split("[+-]")[0]) - 1] = Double.valueOf(arr[i]);
			}
		}
		
		return returnarr;
	}

}
