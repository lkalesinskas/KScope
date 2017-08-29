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
	public double s;
	public double t;
	public double x2;
	
	public Gene (String coggie, double[] kmers) {
		Cog = coggie;
		kmerVector = kmers;
	}
	
	public Gene(String coggie, double x, double y){
		Cog = coggie;
		this.x = x;
		this.y = y;
	}

	public Gene(String id, double[] kmers, String sequence2) {
		Cog = id;
		kmerVector = kmers;
		sequence=sequence2;
	}
}
