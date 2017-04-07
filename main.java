import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class main{
	
	
	static String host = "localhost";
	static String username = "root";
	static String pswd = "password";
	static String port = "3306";
	static String dbName = "DISCO";
	static String userMadeTable = "";
	static String userMadeProcedure = "";
	static MyTabbedPane tabs;
	static Statement statement;
	
	
	public static void main(String[] args){
		try {
			///   CHANGE THE LOOK AND FEEL DEPENDING ON THE OS
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			
			
			Frame frame = new Frame();
			SwingUtilities.updateComponentTreeUI(frame);
			
			//  MAKE SURE THE CORRECT EXTERNAL JAR IS INCLUDED
			
				Class.forName("com.mysql.jdbc.Driver");
				
				Connection con = DriverManager.getConnection("jdbc:mysql://localhost/?user=root&password=password");	
				
				
				ResultSet results;
				
				//    CREATE THE DATABASE IF IT DOESN'T EXIST
				statement = con.createStatement();
				statement.execute("create database if not exists " + dbName);
				
				///    get input
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName,
						"root",
						"password");
				statement = con.createStatement();
				
				
				
			
				///////////// SET UP THE ACTIONLISTENERS FOR THE FRAME   //////////////
				
				// TEXTBOX ACTIONLISTENER
				frame.cpanel2.In.addActionListener(
						new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						
						makeUserTable(frame,statement);
						tabs.initializeFrame();
					}
				});
				
				// TEXTBOX ACTIONLISTENER
				frame.cpanel.In.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {

						makeUserTable(frame, statement);
						tabs.initializeFrame();
					}
				});
				
				// ADD BUTTON ACTIONLISTENER
				frame.cpanel2.add.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {

						makeUserTable(frame, statement);
						frame.dispose();
						
						tabs.initializeFrame();
					}
				});
				
				// ADD BUTTON MOUSELISTENER
				frame.cpanel.add.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {

						makeUserTable(frame, statement);
						tabs.initializeFrame();
					}
				});
				
				
				///////  END ACTIONLISTENERS FOR FRAME  //////////////////////////////////
				
				
			
		}
		//  THIS IS FOR WHEN WE TRY AND START IT UP AND GET NULL POINTER EXCEPTIONS
		catch(java.lang.NullPointerException nullP){
			//  DISPOSE OF FRAME
			//  CREATE NEW INSTANCE OF...ALL OF THIS?
			System.out.println("Reaching?");
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("The LaF does not exist");
		}
		
		
		
		return;
	}
	
	
//	public void addDataToTable(Statement statement, String tableName, String... inputData) throws SQLException{
//		userMadeProcedure = "use " + dbName + "; insert into " + tableName + "(";
//				for(String a : inputData){
//					userMadeProcedure += a +",";
//				}
//				userMadeProcedure = userMadeProcedure.substring(0, userMadeProcedure.length() - 1);
//				userMadeProcedure += ") values (";
//				for(String a : inputData){
//					userMadeProcedure += a +",";
//				}
//				userMadeProcedure = userMadeProcedure.substring(0, userMadeProcedure.length() - 1);
//				userMadeProcedure += ");";
//				
//	}
	
	public static void makeUserTable(Frame frame, Statement statement){
		try {
			
			// MAKE SURE THERE IS SOMETHING IN BOTH TEXTBOXES
			if(!frame.cpanel2.In.getText().equals("") 
					&& !frame.cpanel.In.getText().equals("") 
					&& Character.isDigit(frame.cpanel.In.getText().toCharArray()[0])){  // MAKE SURE THAT THE OTHER CONTAINS A NUMBER
				userMadeTable = "create table if not exists " 
					+ frame.cpanel2.In.getText() 
					+ " (ObjectID int primary key, TaxonomicRank varchar(255), "
					+ "sequence varchar(255), ";
				// INPUT THE X,Y,DIST FOR EACH PLANE REQUESTED
				for(int k = 2; k < Integer.parseInt(frame.cpanel.In.getText()) + 2; k ++){
					userMadeTable += "XCoord" + k + " double, YCoord" + k +" double, distance" + k + " double,";
				}
				// Get rid of the comma by the last double
				userMadeTable = userMadeTable.substring(0, userMadeTable.length() - 1);
				
				userMadeTable += ");";
			}
			
			System.out.println("Entire SQL Statement: " + userMadeTable);
			
		//   MAKE THE TABLE THAT THE USER HAS REQUESTED
			statement.executeUpdate(userMadeTable);
			tabs = new MyTabbedPane(frame.cpanel2.In.getText(), statement, dbName);
			frame.dispose();
		} catch (Exception nFE) {
			System.out.println(nFE.getMessage());
			frame.cpanel.In.setText("Please input a correct input");
			frame.cpanel2.In.setText("Please input a correct input");
		}
	}


}
