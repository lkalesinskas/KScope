
public class Gene {
	public String Cog = null;
	public double[] kmerVector;
	public double x;
	public double y;
	
	public Gene (String coggie, double[] kmers) {
		Cog = coggie;
		kmerVector = kmers;
	}
}
