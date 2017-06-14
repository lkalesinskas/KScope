package KScope;


public class Gene {
	//  cog = peg number
	public String Cog = null;
	public String sequence = null;
	//  kmer composition of that gene
	public double[] kmerVector;
	
	//  coordinates in the plane
	public double x;
	public double y;
	public double z;
	public double q;
	public double r;
	
	public Gene (String coggie, double[] kmers) {
		Cog = coggie;
		kmerVector = kmers;
	}
	
	public Gene(String coggie, double x, double y){
		Cog = coggie;
		this.x = x;
		this.y = y;
	}
}
