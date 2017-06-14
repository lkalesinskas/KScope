package KScope;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class dbtest {

	static String host = "localhost";
	static String username = "root";
	static String pswd = "password";
	static String port = "3306";

	public static void main(String[] args) throws ClassNotFoundException {

		Class.forName("com.mysql.jdbc.Driver");

		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/", username, pswd);
			Statement stmt;
			Statement stmt2;

			stmt2 = con.createStatement();

			stmt2.executeUpdate("CREATE DATABASE IF NOT EXISTS FIGFAMS");
			
			con.close();
			Connection con2 = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/figfams", username, pswd);
			stmt = con2.createStatement();
			
			String sql = "CREATE TABLE IF NOT EXISTS figfam " +
	                "(id VARCHAR(255) not NULL, " +
					" peg varchar(255) not NULL, " +
	                " x double, " + 
	                " y double, " + 
	                " PRIMARY KEY ( id ))"; 
			stmt.executeUpdate(sql);
			String id = ">fig|657324.3.peg.930";
			double a = -127.18;
			double b = 51.72;
//			sql = "insert into figfam values('"+id+"', -127.18, 51.72) where not exists(select "+id+" from figfam where id='" + id+"');";
			sql = "insert ignore into figfam (id, peg, x, y) values ('"+id+"','"+getPeg(id)+"', -127.18, 51.72)";
			sql = "insert ignore into figfam (id, peg, x, y) values ('>fig|657324.3.peg.933','"+getPeg(id)+"', -135.18, 51.72)";
			stmt.executeUpdate(sql);
			
			sql = "select id from figfam where x=" + a + " and y=1"+b;
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				String id2 = rs.getString("id");
				System.out.println(id2);
			}
			
			rs = stmt.executeQuery("select count(*) from figfam");
			while(rs.next()){
				System.out.println("total rows " + rs.getInt(1));
			}
			
			sql = "select x,y from figfam where peg='"+getPeg(id)+"'";
			double totalX = 0.0;
			double totalY = 0.0;
			int total = 0;
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				totalX += rs.getDouble(1);
				totalY += rs.getDouble(2);
				total++;
			}
			
			System.out.println("total: " + total);
			System.out.println("av x: " + totalX/total);
			System.out.println("av y: " + totalY/total);
			
			double x1 = 0.0;
			double y1 = 0.0;
			double x2 = 0.0;
			double y2 = 0.0;
			double distance1 = 0.0;
			double distance2 = 0.0;
			double distance = 0.0;
			double[] coord = new double[2];
			
			sql = "select x,y from figfam where x < -130 or y < 52 order by x desc, y desc";
			
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				System.out.println("found " + rs.getDouble(1) +" "+ rs.getDouble(2));
				x1=rs.getDouble(1);
				y1 = rs.getDouble(2);
				break;
			}
			sql = "select x,y from figfam where x > -130 or y > 52 order by x asc, y asc";
			
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				System.out.println("found " + rs.getDouble(1) +" "+ rs.getDouble(2));
				x2=rs.getDouble(1);
				y2 = rs.getDouble(2);
				break;
			}
			
			distance1 = Math.sqrt((-130-x2)*(-130-x2) + (52-y2)*(52-y2));
			distance2 = Math.sqrt((x1+130)*(x1+130) + (y1-52)*(y1-52));
			coord[0] = (distance1 > distance2) ? x2 : x1;
			coord[1] = (distance1 > distance2) ? y2 : y1;
			
			System.out.println("x and y " + coord[0] + " " + coord[1]);
			sql = "select peg from figfam where x=" + coord[0] +" and y=" + coord[1];
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				System.out.println("found " + rs.getString(1));
			}

			stmt.close();

			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			if (e.getErrorCode() == 1007) {
				System.out.println("db already made");
			} else
				e.printStackTrace();
		}

		
		
	}
	
	public static String getPeg(String id) {
		return id.substring(id.indexOf("peg.") + 4);
	}

}
