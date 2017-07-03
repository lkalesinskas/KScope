package KScope;

import java.util.ArrayList;
import java.util.List;

public class jankcode {
	
	public static void main(String[] args){
		//                0.17218     9 0.178
		String pcatext = "0.17218     9 -0.178kmer143-0.077kmer78-0.076kmer21-0.076kmer236-0.075kmer59-0.075kmer84-0.075kmer66-0.075kmer191-0.075kmer233-0.075kmer213-0.075kmer232-0.075kmer37-0.075kmer24-0.075kmer44-0.074kmer19-0.074kmer124-0.074kmer53-0.074kmer228-0.074kmer176-0.074kmer6-0.074kmer130-0.074kmer190-0.074kmer10-0.074kmer160-0.074kmer126-0.074kmer131-0.074kmer210-0.074kmer185-0.074kmer145-0.074kmer250-0.073kmer181-0.073kmer226-0.073kmer133-0.073kmer238-0.073kmer31-0.073kmer76-0.073kmer211-0.073kmer121-0.072kmer54-0.072kmer164-0.072kmer73-0.072kmer223-0.072kmer58-0.072kmer148-0.072kmer36-0.072kmer56-0.072kmer225-0.072kmer245-0.071kmer173-0.071kmer198-0.071kmer246-0.071kmer161-0.071kmer34-0.071kmer184-0.07kmer8-0.07kmer48-0.07kmer72-0.07kmer47-0.07kmer188-0.07kmer18-0.07kmer7-0.07kmer112-0.07kmer227-0.07kmer117-0.07kmer209-0.07kmer249-0.07kmer144-0.07kmer14-0.069kmer129-0.069kmer254-0.069kmer192-0.069kmer2-0.069kmer11-0.069kmer96-0.069kmer57-0.069kmer212-0.069kmer240-0.069kmer5-0.069kmer62-0.069kmer132-0.069kmer206-0.069kmer141-0.069kmer251-0.069kmer81-0.068kmer179-0.068kmer114-0.068kmer136-0.068kmer46-0.068kmer239-0.068kmer69-0.068kmer127-0.068kmer67-0.067kmer40-0.067kmer255-0.067kmer65-0.067kmer199-0.067kmer109-0.067kmer140-0.067kmer30-0.067kmer229-0.067kmer33-0.067kmer248-0.067kmer20-0.067kmer60-0.066kmer12-0.066kmer32-0.066kmer25-0.066kmer220-0.066kmer63-0.066kmer68-0.066kmer224-0.066kmer9-0.066kmer252-0.066kmer17-0.066kmer80-0.066kmer15-0.066kmer128-0.066kmer3-0.065kmer222-0.065kmer137-0.065kmer41-0.065kmer216-0.065kmer50-0.065kmer180-0.064kmer97-0.064kmer247-0.064kmer157-0.064kmer202-0.064kmer242-0.064kmer177-0.063kmer100-0.063kmer55-0.062kmer168-0.062kmer38-0.062kmer235-0.062kmer85-0.062kmer194-0.062kmer189-0.062kmer35-0.062kmer120-0.062kmer178-0.062kmer200-0.062kmer45-0.062kmer197-0.062kmer237-0.061kmer187-0.061kmer82-0.061kmer175-0.061kmer70-0.061kmer79-0.06kmer122-0.06kmer147-0.06kmer207-0.06kmer77-0.06kmer165-0.06kmer230-0.06kmer149-0.06kmer234-0.06kmer95-0.06kmer75-0.059kmer28-0.059kmer51-0.059kmer116-0.058kmer74-0.058kmer159-0.058kmer195-0.058kmer125-0.058kmer123-0.058kmer83-0.058kmer214-0.058kmer169-0.057kmer4-0.057kmer64-0.057kmer186-0.057kmer146-0.057kmer42-0.057kmer152-0.057kmer29-0.057kmer204-0.056kmer93-0.056kmer203-0.056kmer27-0.056kmer92-0.056kmer162-0.056kmer182-0.056kmer201-0.056kmer221-0.055kmer118-0.055kmer163-0.055kmer134-0.055kmer174-0.055kmer22-0.055kmer172-0.055kmer138-0.055kmer158-0.055kmer43-0.055kmer88-0.055kmer39-0.055kmer104-0.054kmer1-0.054kmer256-0.053kmer244-0.053kmer49-0.053kmer208-0.053kmer13-0.053kmer16-0.053kmer71-0.053kmer111-0.053kmer108-0.053kmer23-0.053kmer105-0.053kmer215-0.052kmer253-0.052kmer193-0.052kmer243-0.052kmer113-0.052kmer231-0.052kmer101-0.051kmer26-0.051kmer156-0.051kmer142-0.051kmer139-0.051kmer94-0.05kmer61-0.05kmer196-0.05kmer52-0.049kmer217-0.049kmer241-0.049kmer218-0.049kmer153-0.047kmer110-0.047kmer135-0.046kmer219-0.046kmer89-0.043kmer98-0.043kmer183-0.041kmer86-0.041kmer171-0.04kmer115-0.04kmer170-0.04kmer150-0.039kmer205-0.039kmer119-0.039kmer99-0.038kmer90-0.038kmer155-0.038kmer167-0.038kmer102-0.036kmer107-0.036kmer87-0.035kmer91-0.034kmer106-0.034kmer151-0.033kmer166-0.032kmer154-0.028kmer103";
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
			if(i==0){
				String zeroSpot = arr[0].replaceAll(" ", "");
				String first = "";
				System.out.println(zeroSpot);
				if(zeroSpot.contains("-")){
					first = "-"+zeroSpot.split("-")[1];
				}
				else{
					first = zeroSpot.substring(zeroSpot.length()-5, zeroSpot.length());
				}
				System.out.println(first);
				returnarr[Integer.valueOf(arr[i+4].split("[+-]")[0]) - 1] = Double.valueOf(first);
				continue;
			}
			else if(arr[i].contains("-")){
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
