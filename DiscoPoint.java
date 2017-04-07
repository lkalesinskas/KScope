
public class DiscoPoint {
	//private float x;
	//private float y;
	//private float distance;
	private int id;
	//private String classification;
	
	public DiscoPoint(int UID) {
		//x = xC;
		//y = xY;
		//distance = xD;
		id = UID;
	}
	
	public void displayDiscoPoint() {
		System.out.print(id + ", ");
	}
	
	public String toString() {
		return (id + ",");
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
	public int getID() {
		return id;
	}
 }
