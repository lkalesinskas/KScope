import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class MyTabbedPane {

	private JTabbedPane tabbedPane = new JTabbedPane();
	private JFrame f = new JFrame();
	public int numOfCols;
	public double step = 10000;
	private String[] columnNames = { "First Name", "Last Name", "Sport", "# of Years", "Vegetarian" };
	public ArrayList<String> colNames = new ArrayList<String>();
	private String rowVals[][];
	String databaseName = "";
	public String sqlTableName;
	private ArrayList<String> currRow = new ArrayList<String>();
	private Object[][] data = { { "Kathy", "Smith", "Snowboarding", new Integer(5), (false) },
			{ "John", "Doe", "Rowing", new Integer(3), (true) },
			{ "Sue", "Black", "Knitting", new Integer(2), (false) },
			{ "Jane", "White", "Speed reading", new Integer(20), (true) },
			{ "Joe", "Brown", "Pool", new Integer(10), (false) } };
	private DefaultTableModel model = new DefaultTableModel(data, columnNames) {
		private static final long serialVersionUID = 1L;

		@Override
		public Class getColumnClass(int column) {
			return getValueAt(0, column).getClass();
		}
	};

	public MyTabbedPane(String tableName, Statement statement, String dbName)
			throws SQLException, ClassNotFoundException {
		discoWrapper planes = new discoWrapper();
		sqlTableName = tableName;
		databaseName = dbName;
		Class.forName("com.mysql.jdbc.Driver");

		Connection con = DBConnector.getConn(dbName);
		statement = con.createStatement();
		ResultSet results = statement.executeQuery("show columns from " + tableName);
		ResultSetMetaData rsmd = results.getMetaData();

		// Iterate through the data in the result set and display it.
		/// WILL GET THE COLUMNS NAMES
		while (results.next()) {
			// Print first element of each row
			colNames.add(results.getString(1)); // Print first
												// element of a
												// row

		}
		columnNames = Arrays.copyOf(colNames.toArray(), colNames.size(), String[].class);

		/**
		 * 1. Get the number of Rows 2. Set a 2d string array that can be
		 * dynamically changed. This way you are more memory efficient 3. Get
		 * the names of the elements in each Row 4. Add to the array 5. Display
		 * on the table
		 */
		// Get the number of Rows
		results = statement.executeQuery("select count(*) from " + tableName);
		results.next();
		int numOfRows = Integer.parseInt(results.getString(1));
		//int numOfRows = 100;
		
		// PREPARE TO GET ITEMS FROM ALL THE COLUMNS
		results = statement.executeQuery("select * from " + tableName + " limit 1000");
		rsmd = results.getMetaData();
		numOfCols = rsmd.getColumnCount(); /* Get the number of columns */
		rsmd = results.getMetaData();
		rowVals = new String[numOfRows][numOfCols];

		// MAKE THE TABLE THAT WILL HAVE THE CURRENT TABLE TO BE DISPLAYED
		DefaultTableModel test = new DefaultTableModel(null, columnNames) {
			private static final long serialVersionUID = 1L;

		};
		JTable testTable = new JTable(test);
		testTable.getTableHeader().setResizingAllowed(true);

		// MAKE THE SORTER FOR THE TABLE
		TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(testTable.getModel());
		JTextField filter = new JTextField();
		testTable.setRowSorter(rowSorter);

		// Set the Rows to appropriate values
		int numberRows = 0;
		while (results.next()) {
			currRow.clear();
			for (int i = 1; i < numOfCols; i++) {
					currRow.add(results.getString(i));
				}
		test.addRow(currRow.toArray());
		}

		//}
		

		// WHAT WILL HELP FILTER WHENEVER YOU TYPE INTO THE TEXT FILTER
		JScrollPane testJScroll = new JScrollPane(testTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//		filter.setSize(, 500 );
		filter.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				String text = filter.getText();

				if (text.trim().length() == 0) {
					rowSorter.setRowFilter(null);
				} else {
					rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
				}

			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				String text = filter.getText();

				if (text.trim().length() == 0) {
					rowSorter.setRowFilter(null);
				} else {
					rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
				}

			}

		});
		
		
		//  THE BUTTON THAT WILL GET YOU, YES YOU! THE INFORMATION NEEDED FOR DISCO
		JButton discoButton = new JButton("Run Disco");
		discoButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				/*
				 * As per Larry's orders, this will do 2 things
				 * 1.  Will execute a statement that will grab all the UIDs
				 * 2.  Will execute a statement that will grab all the X,Y and d for each plane
				 */
				
				// FIRST WE WILL EXECUTE THE UID STATEMENT
				//  WILL GET THE UIDs FOR THE TABLE THAT THE USER CREATED
				try {
					
					//  CREATE THE CONNECTION AND THEN LATER CLOSE IT
					Class.forName("com.mysql.jdbc.Driver");
					Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/disco",
							"root",
							"password");
					System.out.println(con.isClosed());
					Statement statement2 = con.createStatement();
					statement2.execute("create database if not exists " + dbName);
					ResultSet results = statement2.executeQuery("select * from " + sqlTableName + ";");
					ResultSetMetaData rsmd = results.getMetaData();
					//ResultSet results2 = statement2.executeQuery("");
					// Iterate through the data in the result set and display it.
					int trueNumberColumns = rsmd.getColumnCount();
					int numColumns = rsmd.getColumnCount();
					numColumns = numColumns - 3;
					int numberPlanesNeeded = numColumns/3;						
					//numberPlanesNeeded = numberPlanesNeeded - 1;
					int count = 0;
					int UID = 0;
					BigDecimal x = null;
					BigDecimal y = null;
					for (int i = 0; i<numberPlanesNeeded; i++) {
						count++;
						planes.planes.add(new DiscoPlane(10000));
					}
					System.out.println(trueNumberColumns);
					int planeToAdd = 0;
					while (results.next()) {
						for (int i = 4; i<=trueNumberColumns; i++) {
							UID = results.getInt(1);
							if (i%3==1) {
								x = new BigDecimal(results.getString(i));
							}
							else if (i%3==2) {
								y = new BigDecimal(results.getString(i));
							}
							else if (i%3==0) {
								planeToAdd = i/3;
								planeToAdd = planeToAdd-2;
								planes.planes.get(planeToAdd).placeSequence(new Point(x,y), UID);
							}
						}
					}
					//System.out.println(planes.planes.size());
					for (int i = 0; i<planes.planes.size(); i++) {
						planes.planes.get(i).checkLegos();
					}
					//  CLOSE THE CONNECTION
					con.close();
				} catch (SQLException | ClassNotFoundException e) {
					// TODO Auto-generated catch block
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
				
			}
			
		});
		
		//   CREATE A PANEL TO HAVE ALL THE FILTER INFO ON THE BOTTOM OF THE FRAME
		JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new BorderLayout());
		filterPanel.add(filter, BorderLayout.CENTER);
		filterPanel.add(new JLabel("Input text to filter"), BorderLayout.WEST);
		filterPanel.add(discoButton);

		// ADD TABS AND THEN ADD EVERYTHING TO A FRAME
		tabbedPane.addTab("Tab1", testJScroll);
		tabbedPane.addTab("Tab2", new JScrollPane(new JTable(model), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
		tabbedPane.addTab("Tab3", new JScrollPane(new JTable(model)));
		tabbedPane.addTab("Tab4", new JScrollPane(new JTable(model)));
		tabbedPane.setTabPlacement(JTabbedPane.TOP);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		f.add(filterPanel, BorderLayout.SOUTH);
		
		//  MAX KELLY DEFAULT CLOSE OP
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {

				JFrame frame = (JFrame) e.getSource();

				int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit the application?",
						"Exit Application", JOptionPane.YES_NO_OPTION);

				if (result == JOptionPane.YES_OPTION) {
					try {
						con.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					frame.dispose();
					System.gc();
					System.out.println("X has been clicked");
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}
			}
		});
		f.add(tabbedPane, BorderLayout.CENTER);
		f.setJMenuBar(new testMenuBar(columnNames, statement, tableName, databaseName, planes));
		f.pack();

	}

	/**
	 * ALLOWS TO CONTROL THE VISIBILITY OF THE FRAME
	 */
	public void initializeFrame() {
		f.setVisible(true);
	}
}