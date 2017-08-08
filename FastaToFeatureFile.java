package KScope;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FastaToFeatureFile {
	
public static void fastaToFeature(String optionValue, String equationFile) throws IOException {
		
		BufferedReader bufferedReader = new BufferedReader(new FileReader(equationFile));
		String line = "";
		
		//  reading and parsing PCA equations
		List<double[]> equationList = new ArrayList<double[]>();
		while ((line = bufferedReader.readLine()) != null) {
			equationList.add(KDTOnlyMain.parsePCAText(line));
		}
		bufferedReader.close();
			
			
		int kmerToDo = 3;
		BufferedReader br = new BufferedReader(new FileReader(optionValue));
		BufferedWriter bw = new BufferedWriter(new FileWriter(optionValue+".feature"));
		String id = "";
		String sequence = "";
		boolean first = true;
		while((line = br.readLine()) != null){
			if(line.contains(">")){
				if(first){
					id = line;
					first = false;
					continue;
				}
				else{
					
					if(id.contains("hypothetical") || id.contains("Hypothetical") || id.contains("USS-DB") || sequence.length() < 100){
						sequence = "";
						id=line;
						continue;
					}
					// fix sequence to have replaced length and unwanted parts removed
					sequence = KDTOnlyMain.replaceNucs(sequence);
					sequence = sequence.substring(60, sequence.length() - 2);
					double[] gene = KDTOnlyMain.processSequencebyKmer(sequence, kmerToDo);
					double sumGene = 0.0;
					for(int i2 = 0; i2 < gene.length; i2++){
						sumGene+=gene[i2];
					}
					for(int i2 = 0; i2 < gene.length; i2++){
						gene[i2] = gene[i2]/sumGene;
					}
					
					Double[] coordArr = new Double[equationList.size()];
					//  get the coordinates for each point
					for(int v = 0; v < equationList.size(); v ++){
						coordArr[v] = KDTOnlyMain.getPCAX(gene, equationList.get(v));
						bw.write(coordArr[v] +",");
					}
					bw.write(id.replaceAll(",", "")+"\n");
					
					
					sequence = "";
					id = line;
					
				}
			}
			else{
				sequence += line;
			}
		}
		br.close();
		bw.close();
	}

}
