package KScope;

import java.util.ArrayList;
import java.util.List;

public class jankcode {
	
	List<String> otherGenesFound = new ArrayList<String>();
	int sameCog = 0;
	for(String cog : rangeVals){
		sameCog = 0;
		if(cog.equals(testSequences.get(i).Cog)){
			sameCog ++;
		}
		else{
			otherGenesFound.add(cog);
		}
		lowHitSameCogMap.put(testSequences.get(i).Cog, sameCog);
		
	}
	otherGenesFound.add(0, Integer.toString(sameCog));
	lowHitThresholdMap.put(testSequences.get(i).Cog, otherGenesFound);
	otherGenesFound.clear();
	rangeVals = findThreshold(0.001, 0.01, test, coord);
	for(String genes : rangeVals){
		sameCog = 0;
		if(genes.equals(testSequences.get(i).Cog)){
			sameCog ++;
		}
		else{
			otherGenesFound.add(genes);
		}
	}
	otherGenesFound.add(0, Integer.toString(sameCog));
	medHitThresholdMap.put(testSequences.get(i).Cog, otherGenesFound);
	otherGenesFound.clear();
	rangeVals = findThreshold(0.01, 0.1, test, coord);
	
	//  hit with high threshold
	sameCog = 0;
	for(String genes : rangeVals){
		sameCog = 0;
		if(genes.equals(testSequences.get(i).Cog)){
			sameCog ++;
		}
		else{
			otherGenesFound.add(genes);
		}
		
		
	}
	otherGenesFound.add(0, Integer.toString(sameCog));
	highHitThresholdMap.put(testSequences.get(i).Cog, otherGenesFound);
	otherGenesFound.clear();

}
