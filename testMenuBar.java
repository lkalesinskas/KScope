import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

public class testMenuBar extends JMenuBar {
	
	////  GLOBALS   /////
	JMenu menu = new JMenu("Menu Test");
	FileFinder file;
	public String databaseName;
	public String tableName;
	
	public testMenuBar(String[] columnNames, Statement statement, String tableName1, String databaseName1, discoWrapper planes){
		tableName = tableName1;
		databaseName = databaseName1;
		JMenuItem menuItem = new JMenuItem("Open File", KeyEvent.VK_T);
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription("Has Menu Items");
		this.add(menu);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setEnabled(true);
		menuItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
		menuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					
					JFileChooser chooser = new JFileChooser();
					FileNameExtensionFilter filter = new FileNameExtensionFilter("File fasta", "train", "fas");
					chooser.setFileFilter(filter);
					int returnVal = chooser.showOpenDialog((Component) e.getSource());
					chooser.setFileHidingEnabled(false);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						System.out.println("You chose to open this file: " + chooser.getSelectedFile().getName());
						System.out.println(chooser.getSelectedFile().getAbsolutePath());
						System.out.println("replaced: " + chooser.getSelectedFile().getAbsolutePath().replace("\\", "\\\\"));

						file = new FileFinder(chooser.getSelectedFile().getAbsolutePath().replace("\\", "\\\\"), 
								main.statement,
								tableName,
								columnNames);	
						}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.getMessage();
				}
			}
		});
		menu.add(menuItem);
		
		
//		menuItem = new JMenuItem("Search UID", KeyEvent.VK_C);
//		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
//		menuItem.setEnabled(true);
//		
//		menuItem.getAccessibleContext().setAccessibleDescription("This also doesn't really do anything");
//		menuItem.addActionListener(new ActionListener(){
//			
//			public void actionPerformed(ActionEvent e){
//				JOptionPane popup = new JOptionPane();
//				try {
//					statement.execute("select * from " + tableName + "where ObjectID = " + popup.showInputDialog("What is the ObjectID you would like to search for?") + ";");
//					
//				} catch (HeadlessException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				} catch (SQLException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//			}
//		});
//		menu.add(menuItem);
		
		menuItem = new JMenuItem("Coordinate Lego Search", KeyEvent.VK_A);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setEnabled(true);
		
		menuItem.getAccessibleContext().setAccessibleDescription("This also doesn't really do anything");
		menuItem.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){
				System.out.println("I'm going to add shit here.....");
				BigDecimal xSearch = new BigDecimal(JOptionPane.showInputDialog("Type in X-Coordinate!", "Type in X-Coordinate:"));
				BigDecimal ySearch = new BigDecimal(JOptionPane.showInputDialog("Type in Y-Coordinate!", "Type in Y-Coordinate:"));
				Vector<DiscoPoint> result = planes.planes.get(0).classifySequence(xSearch, ySearch);
				java.sql.Connection con = null;
				PreparedStatement statement = null;
				ResultSet resultSet = null;
				try {
					con = DBConnector.getConn(databaseName);
					statement = con.prepareStatement("SELECT * FROM " + tableName + " WHERE ObjectID = ?");
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
		
				for (int i = 0; i<result.size(); i++) {
					try {
						statement.setInt(1, result.get(i).getID());
						resultSet = statement.executeQuery();
						if (resultSet.next()) {
						    System.out.println(resultSet.getString(2));
						} else {
						    // No match!
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			
				/*JOptionPane popup = new JOptionPane();
				try {
					statement.execute("select * from " + tableName + "where ObjectID = " + popup.showInputDialog("What is the ObjectID you would like to search for?") + ";");
					
				} catch (HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			*/}
		});
		menu.add(menuItem);
		
		menuItem = new JMenuItem("FASTA Search!", KeyEvent.VK_A);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.setEnabled(true);
		menuItem.getAccessibleContext().setAccessibleDescription("This also doesn't really do anything");
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String test1= JOptionPane.showInputDialog("Please input FileName: ");
				PrintStream xx = null;
				try {
					xx = new PrintStream(new File("Outter.txt"));
				} catch (FileNotFoundException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				try {
					sequenceSearchKmer abc = new sequenceSearchKmer();
					boolean first = true;
					String sequence = "";
					String id = "";
			        try (Scanner sc = new Scanner(new File(test1))) {
			            while (sc.hasNextLine()) {
			                String line = sc.nextLine().trim();
			                if (line.contains(">")) {
			                    if (first)
			                        first = false;
			                    else {
			                    	sequence = sequenceSearchKmer.replaceNucs(sequence);
			                    	xx.println("-----------");
			                    	xx.println(id);
			                    	for (int i = 2; i<9; i++) {
				                    	BigDecimal[] arrayToUse = sequenceSearchKmer.processSequence(id, sequence, i);
			                    		Vector<DiscoPoint> storage1 = planes.planes.get(i-2).classifySequence(arrayToUse[0], arrayToUse[1]);
			                    		xx.println("Plane " + i + ": ");
			                    			if (storage1!=null) {
				                    			Vector<String> toPrint = searchDatabase(storage1);
				                    			xx.print(toPrint);
				                    		}
				                    		xx.println();
			                    	}
			                    	id = "";
			                    	sequence = "";
			                    }
			                    id = line;
			                } else {
			                    sequence+=line;
			                }
			            }
			        }
			        xx.close();
			        System.out.println("x");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		menu.add(menuItem);
		
	}
	
	public Vector<String> searchDatabase(Vector<DiscoPoint> result) {
		java.sql.Connection con = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		Vector<String> returnVector = new Vector<String>();
		try {
			con = DBConnector.getConn(databaseName);
			statement = con.prepareStatement("SELECT * FROM " + tableName + " WHERE ObjectID = ?");
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	
		for (int i = 0; i<result.size(); i++) {
			try {
				statement.setInt(1, result.get(i).getID());
				resultSet = statement.executeQuery();
				if (resultSet.next()) {
				    returnVector.add(resultSet.getString(2));
				} else {
				    // No match!
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return returnVector;
	}
}
