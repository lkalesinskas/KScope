package KScope.Code;

import java.math.BigDecimal;

public class Point {
	public static BigDecimal x;
	public static BigDecimal y;
	//public static BigDecimal z;
	public Point(BigDecimal d, BigDecimal e) {
		this.x = d;
		this.y = e;
		//this.z = f; 
	}
	
	public String toString() {
		return (x.toPlainString() + "," + y.toPlainString());
	}

}
