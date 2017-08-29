package KScope.Code;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.List;

import edu.wlu.cs.levy.CG.KDTree;

public interface TrainFile {
	
//	HashMap<double[], String> sequenceMap = new HashMap<double[], String>();
	
	public void train(KDTree test, BufferedReader br, List<double[]> equationList, int kmerToDo, boolean fastatofeature) throws Exception;
	
	public int getIntersectionCount();

}
