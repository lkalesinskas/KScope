package KScope;

import java.io.BufferedReader;
import java.util.List;

import edu.wlu.cs.levy.CG.KDTree;

public interface TrainFile {
	
	public void train(KDTree test, int intersectionCount, BufferedReader br, List<double[]> equationList, int kmerToDo) throws Exception;

}
