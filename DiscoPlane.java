package KScope;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class DiscoPlane {
	HashMap<String, Lego> legoStorage;
	public int kmer;
	public BigDecimal step;
	public double range = .0003;
	
	public DiscoPlane(int step) {
		BigDecimal s = new BigDecimal(step);
		this.step = s;
		legoStorage = new HashMap<>();
	}
	
	public void placeSequence (Point toPlace, String Cog) {
		toPlace.x = (toPlace.x.multiply(step).divide(step));
		toPlace.x = toPlace.x.setScale(4, BigDecimal.ROUND_HALF_UP);
		toPlace.y = (toPlace.y.multiply(step).divide(step));
		toPlace.y = toPlace.y.setScale(4, BigDecimal.ROUND_HALF_UP);
		//toPlace.z = (toPlace.z.multiply(step).divide(step));
		//toPlace.z = toPlace.z.setScale(4, BigDecimal.ROUND_HALF_UP);
		String toHash = toPlace.toString();
		//System.out.println(toHash);
		if (legoStorage.containsKey(toHash)==false) {
			legoStorage.put(toHash, new Lego(Cog));
		}
		else {
			legoStorage.get(toHash).addPoint(new DiscoPoint(Cog));
		}	
	}
	
	public void checkLegos () {
		int count = legoStorage.keySet().size();
		System.out.println("Legos Populated: " + count);
	}
	
	public Vector<DiscoPoint> classifySequence (BigDecimal x, BigDecimal y) {
		BigDecimal xWindow = ((x.multiply(step).divide(step)));
		xWindow = xWindow.setScale(4, BigDecimal.ROUND_HALF_UP);
		BigDecimal yWindow = (((y.multiply(step).divide(step))));
		yWindow = yWindow.setScale(4, BigDecimal.ROUND_HALF_UP);
		//BigDecimal zWindow = (((z.multiply(step).divide(step))));
		//zWindow = zWindow.setScale(4, BigDecimal.ROUND_HALF_UP);
		//System.out.println(xWindow.toString());
		double ourX = xWindow.doubleValue();
		double ourY = yWindow.doubleValue();
		//double ourZ = zWindow.doubleValue();
		double minX = ourX - range;
		double maxX = ourX + range;
		double minY = ourY - range;
		double maxY = ourY + range;
		//double minZ = ourZ - range;
		//double maxZ = ourZ - range;
		String searcher = xWindow + "," + yWindow;
		if (legoStorage.containsKey(searcher)) { //the key is in the HashMap
			return legoStorage.get(searcher).data;
		}
		else {		//the key is not in the HashMap and we have to Search Around...
			ArrayList<String> keySet = new ArrayList<>(legoStorage.keySet());
			double distance = Integer.MAX_VALUE;
			BigDecimal xHit = null;
			BigDecimal yHit = null;
			for (String a : keySet) {
				String[] b = a.split(",");
				double testX = Double.parseDouble(b[0]);
				double testY = Double.parseDouble(b[1]);
				if (testX>minX && testX<maxX && testY>minY && testY<maxY && distanceFormula(testX, testY, ourX, ourY)<distance) {
					distance = distanceFormula(testX, testY, ourX, ourY);
					xHit = new BigDecimal(b[0]);
					yHit = new BigDecimal(b[1]);
				}
			}
			searcher = xHit + "," + yHit;
			if (legoStorage.containsKey(searcher)) {
				return legoStorage.get(searcher).data;
			}
			else {
				return null;
			}
		}
	}
		
	

	public double distanceFormula (double x, double y, double testX, double testY) {
		double term1 = (x - testX) * (x-testX);
		double term2 = (y - testY) * (y-testY);
		return Math.sqrt(term1 + term2);
	}
}

/*
class mainRunner {
	public static void main (String[] args) {
		DiscoPlane a = new DiscoPlane(10000);
		a.placeSequence(new Point(new BigDecimal(".5001"), new BigDecimal(".5002")), 1000);
		a.placeSequence(new Point(new BigDecimal(".5002"), new BigDecimal(".5002")), 1001);
		Vector<DiscoPoint> b = a.classifySequence(new BigDecimal(".5000"), new BigDecimal(".5000"));
		for (int i = 0; i<b.size(); i++) {
			b.elementAt(i).displayDiscoPoint();
		}
	}
}*/
