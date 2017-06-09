package KScope;

import java.sql.Connection;
import java.util.Vector;

public class Lego {
	public Vector<DiscoPoint> data;
	public double xPosition = 0;
	public double yPosition = 0;
	public double heightWidth = 0;
	
	public Lego(String Cog) {
		data = new Vector<DiscoPoint>();
		data.add(new DiscoPoint(Cog));
	}
	
	public void addPoint(DiscoPoint a) {
		data.add(a);
	}
	
	public void displayLego() {
		System.out.println("Data Found!");
		System.out.println("Lego Position: "  + xPosition + " , " + yPosition);
		System.out.println("Lego Height/Width " + heightWidth);
		for (int i = 0; i<data.size(); i++) {
			data.get(i).displayDiscoPoint();
		}
		System.out.println();
		System.out.println("****************************");
	}
}
