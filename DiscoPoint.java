
public class DiscoPoint {
	//private float x;
	//private float y;
	//private float distance;
	private String Cog;
	//private String classification;
	
	public DiscoPoint(String Cog) {
		//x = xC;
		//y = xY;
		//distance = xD;
		this.Cog = Cog;
	}
	
	public void displayDiscoPoint() {
		System.out.print(Cog + ", ");
	}
	
	public String toString() {
		return (Cog + ",");
	}
	/*
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
	
	public float getDistance() {
		return distance;
	}
	*/
	public String getCog() {
		return Cog;
	}
 }
