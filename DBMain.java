package KScope;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.math3.stat.correlation.KendallsCorrelation;


import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeySizeException;

/**
 * 
 * @author Larry Kalesinskas
 *
 */
public class DBMain {
	private static int TOTAL_VALS = 3000000;
	public static int nmer = 0;
	public static int kmerMax = 9;
	public static int numShifts = 0;
	public static int numShiftsMinus = 0;
	public static int kmerToDo = 0;
	
	public static int searchPositive = 0;
	public static int searchNegative = 0;
	public static int hit = 0;
	public static int miss = 0;
	
	static String host = "localhost";
	static String username = "root";
	static String pswd = "password";
	static String port = "3306";
	static String table = "PCA3merTesting";

	public static void execute(String PCAFile, String TestFile, String TrainFile, String OutFile) throws ClassNotFoundException, IOException {
		kmerToDo =3;

		//  driver manager
		Class.forName("com.mysql.jdbc.Driver");

		try {
			//  connect and create database if it doesnt exist
			Connection con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/", username, pswd);
			Statement stmt;
			Statement stmt2;

			stmt2 = con.createStatement();

			stmt2.executeUpdate("CREATE DATABASE IF NOT EXISTS FIGFAMS");
			
			con.close();
			//  open connection to new db
			Connection con2 = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/figfams", username, pswd);
			stmt = con2.createStatement();
			
			//  create the table
			String sql = "CREATE TABLE IF NOT EXISTS " + table +" "+
	                "(uid int not NULL, " +
					" id TEXT not NULL, "+
	                " sequence TEXT not NULL,";
//	                " PRIMARY KEY ( id ))"; 
//			stmt.executeUpdate(sql);
			
			
			//  read from equation file
			BufferedReader eqnReader = new BufferedReader(new FileReader("percentage3merPCA2.txt"));
			List<double[]> equationList = new ArrayList<double[]>();
			String eqn = "";
			while((eqn=eqnReader.readLine()) != null){
				equationList.add(parsePCAText(eqn));
			}
			eqnReader.close();
			String prepsql = "";
			//  create insert statement of size equationList.size for all dimensions
			prepsql = "insert ignore into PCA3merTesting values (?,?,?,";
			for(int i = 0; i < equationList.size(); i ++){
				sql += "z" + i + " double,";
//					sql +=",";
			}
			sql += " PRIMARY KEY (uid))";
			stmt.executeUpdate(sql);
			for(int i = 0; i < equationList.size(); i ++){
				prepsql += "?";
				if(i + 1 != equationList.size()){
					prepsql +=",";
				}
			}
			prepsql+=")";
			PreparedStatement ps = con2.prepareStatement(prepsql);
			BufferedReader br = new BufferedReader(new FileReader("TrainOut3.ffn"));
//			BufferedWriter distanceWriter = new BufferedWriter(new FileWriter("DistanceToOriginFromTraining.csv"));
			int count = 0;
			int uid = 0;
			String id="";
			String line = "";
			String sequence = "";
			System.out.println("inputting into db");
			/**   TRAINING BEGIN   **/
			while((line = br.readLine()) != null){
				id = line;
				// read sequence
				sequence = br.readLine();
				sequence = replaceNucs(sequence);
				//  prepare sql statement
//				sql = "insert ignore into "+table+" (uid,id,sequence,";
//				for(int i = 0; i < equationList.size(); i ++){
//					sql+="z"+i;
//					if(i + 1 != equationList.size()){
//						sql +=",";
//					}
//				}
//				sql+=") values ("+uid +",'"+id+"','"+sequence+"',";
				// calculate gene
				double[] gene = processSequencebyKmer(sequence, kmerToDo);
				double sumGene = 0.0;
				for(int i2 = 0; i2 < gene.length; i2++){
					sumGene+=gene[i2];
				}
				for(int i2 = 0; i2 < gene.length; i2++){
					gene[i2] = gene[i2]/sumGene;
				}
				//  calculate the coordinates and the distance to the origin
				double distance = 0.0;
				double[] temp = new double[equationList.size()];
				for(int i = 0; i < equationList.size(); i ++){
					temp[i] = getPCAX(gene, equationList.get(i));
					sql += temp[i];
					distance += temp[i] * temp[i];
					if(i + 1 != equationList.size()){
						sql +=",";
					}
				}
				distance = Math.sqrt(distance);
//				distanceWriter.write(distance +",\n");
				if(count > 3000000) break;
				double tableName = round(distance,5);
				String tableString = Double.toString(tableName).replace(".", "");
				sql = "create table if not exists a" + tableString+" "+
		                "(uid int not NULL, " +
						" id TEXT not NULL, "+
		                " sequence TEXT not NULL,";
				for(int i = 0; i < equationList.size(); i ++){
					sql += "z" + i + " double,";
				}
				sql += " PRIMARY KEY (uid))";
				stmt.executeUpdate(sql);
				sql = "insert ignore into a"+tableString+" (uid,id,sequence,";
				for(int i = 0; i < equationList.size(); i ++){
					sql+="z"+i;
					if(i + 1 != equationList.size()){
						sql +=",";
					}
				}
				sql+=") values ("+uid +",'"+id+"','"+sequence+"',";
				for(int i = 0; i < temp.length; i ++){
					sql+= temp[i];
					if(i + 1 != equationList.size()){
						sql +=",";
					}
				}
				sql+=")";
				//  add to db
				stmt.executeUpdate(sql);
				
				
				ps.setInt(1, uid);
				uid++;
				ps.setString(2, id);
				ps.setString(3, sequence);
				for(int i = 0; i < equationList.size(); i ++){
					int spot = i + 4;
					ps.setDouble(spot, getPCAX(gene, equationList.get(i)));
				}
				// do not uncomment or get rid of prepared statement code.  sql does not seem to work without it.   not sure why
//				ps.addBatch();
				count ++;
				
			}
//			distanceWriter.close();
			ps.executeBatch();
			/**    TRAINING END     **/
			ResultSet size = stmt.executeQuery("select count(*) from pca3mertesting");
			while(size.next()){
				System.out.println("total rows " + size.getInt(1));
			}
			/**    BEGIN TESTING    **/
			File testFile = new File("TestOut3.ffn");
			Vector<Gene> testSequences = InputAndProcessGenesCategoryTest(testFile);
			System.out.println("We have " + testSequences.size() + " test sequences!");
//			BufferedWriter bw = new BufferedWriter(new FileWriter("nearest100.csv"));
//			bw.write("IDs at target coord, Nearest IDs with sequence");
			ExecutorService executor = Executors.newFixedThreadPool(10);
			for(int runs = 0; runs < 1000; runs ++){
				final int runs3 = runs;
				Runnable r = new Runnable(){
					public void run(){
						for(int sequences = runs3*100; sequences < runs3*100 + 100; sequences ++){
							if(sequences == 0) sequences=2;
							
							//  calculate the correct gene array
							double[] gene = testSequences.get(sequences).kmerVector.clone();
							double sumGene = 0.0;
							for(int i2 = 0; i2 < gene.length; i2++){
								sumGene+=gene[i2];
							}
							for(int i2 = 0; i2 < gene.length; i2++){
								gene[i2] = gene[i2]/sumGene;
							}
							
							
							//  calculate the coordinates
							Double[] coordArr = new Double[equationList.size()];
							for(int v = 0; v < equationList.size(); v ++){
								coordArr[v] = getPCAX(gene, equationList.get(v));
							}
							
							
							Double[] coord1 = coordArr;
							double[] coord = new double[coord1.length];
							double distance = 0.0;
							for(int c = 0; c < coord1.length; c ++){
								coord[c] = coord1[c];
								distance += coord[c];
							}
							distance = Math.sqrt(distance);
							String tableName = "a"+Double.toString(distance).replaceAll(".", "");
							
							//  random weird error happens without this
							if(coordArr[0].isNaN() || coordArr[1].isNaN()) continue;
						
							try{
								String sql = "select id from "+tableName+" where ";
								for(int i=0; i < coordArr.length; i ++){
									
									sql+="z"+i+"="+coordArr[i];
									if(i+1!=equationList.size()){
										sql+=" and ";
									}
								}
								ResultSet rs = stmt.executeQuery(sql);
								if(rs != null){
									do{
										String queriedID = rs.getString("id");
										if(queriedID.equals("") || queriedID.equals(null) || queriedID==null){
											if(nearest(coord,stmt).equals(testSequences.get(sequences).Cog)){
												searchPositive ++;
											}
											else {
												searchNegative ++;
											}
										}
										else if(queriedID.equals(testSequences.get(sequences).Cog)){
											hit ++;
										}
										else if(queriedID.equals(testSequences.get(sequences).Cog) == false){
											miss ++;
										}
									}while(rs.next());
								}
								else if(rs == null){
									if(nearest(coord,stmt).equals(testSequences.get(sequences).Cog)){
										searchPositive ++;
									}
									else {
										searchNegative ++;
									}
								}
								
//								printNearest100(coord, stmt, bw);
								rs.close();
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}
					
					public void printNearest100(double[] coord, Statement stmt, BufferedWriter bw) throws IOException, SQLException{
						String sql = "";
						String rt = "";
						
						
						
						//  select all points at the target coords
						sql = "select id from "+table+" where ";
						for(int i=0; i < coord.length; i ++){
							sql+="z"+i+"="+coord[i];
							if(i+1!=coord.length){
								sql+="and";
							}
						}
						ResultSet rs = stmt.executeQuery(sql);
						while(rs.next()){
							rt = rs.getString(1);
							bw.write(rt+"``````");
						}
						bw.write(",");
						
						//  select all points smaller than the target coords
						sql = "select id, sequence ";
						
						sql+=" from "+table+" where ";
						for(int i = 0; i < coord.length; i ++){
							sql+="z"+i+"<="+coord[i];
							if(i+1 != coord.length){
								sql+="or";
							}
						}
						
						sql+= " order by x desc, y desc limit 0,50";
						
						
						while(rs.next()){
							bw.write(rs.getString("id") + " " + rs.getString("sequence")+",");
						}
					//  select all points smaller than the target coords
							sql = "select id,sequence ";
							
							sql+=" from "+table+" where ";
							for(int i = 0; i < coord.length; i ++){
								sql+="z"+i+">="+coord[i];
								if(i+1 != coord.length){
									sql+="or";
								}
							}
							
							sql+= " order by x asc, y asc limit 0,50";		
						rs = stmt.executeQuery(sql);
						while(rs.next()){
							bw.write(rs.getString("id") + " " + rs.getString("sequence")+",");
						}
						
						
						
						
						return;
						
					}
				};
				executor.execute(r);
			}
			// shutdown the threads
			executor.shutdown();
			
			while(!executor.isTerminated()){
				
			}
//			bw.close();
			/**   END TESTING   **/
			System.out.println("Hits: " + hit);
			System.out.println("Misclassified" + miss);
			System.out.println("Search Positive: " + searchPositive);
			System.out.println("Search Negative: " + searchNegative);
			con2.close();
			stmt.close();
			
		
		}catch(SQLException e){
			if (e.getErrorCode() == 1007) {
				System.out.println("db already made");
			} else
				e.printStackTrace();
		}
		
		
	}

	private static double round(double distance, int places) {
		if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(distance);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}

	/**
	 * Will find the nearest point to coord in the database
	 * @param coord - the coords that we will be trying to find something close to
	 * @param stmt - the statement from the database that will let us query the db
	 * @return - the closest point to coord
	 * @throws SQLException - if anything goes wrong, throw it!
	 */
	private static String nearest(double[] coord, Statement stmt) throws SQLException {
		String sql = "";
		String rt = "";
		double[] coordSet1 = new double[coord.length];
		double[] coordSet2 = new double[coord.length];
		double distance1 = 0.0;
		double distance2 = 0.0;
		
		
		//  select all points smaller than the target coords
		sql = "select ";
		for(int i =0; i < coord.length; i ++){
			sql+="z"+i;
			if(i+1 != coord.length){
				sql+=",";
			}
		}
		sql+=" from "+table+" where ";
		for(int i = 0; i < coord.length; i ++){
			sql+="z"+i+"<="+coord[i];
			if(i+1 != coord.length){
				sql+="or";
			}
		}
		
		sql+= " order by ";
		for(int i =0; i < coord.length; i ++){
			sql+="z"+i +" desc";
			if(i+1 != coord.length){
				sql+=",";
			}
		}
		
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
			for(int i = 0; i < rs.getMetaData().getColumnCount(); i ++){
				coordSet1[i]=rs.getDouble(i+1);
			}
			break;
		}
	//  select all points smaller than the target coords
			sql = "select ";
			for(int i =0; i < coord.length; i ++){
				sql+="z"+i;
				if(i+1 != coord.length){
					sql+=",";
				}
			}
			sql+=" from "+table+" where ";
			for(int i = 0; i < coord.length; i ++){
				sql+="z"+i+">="+coord[i];
				if(i+1 != coord.length){
					sql+="or";
				}
			}
			
			sql+= " order by ";
			for(int i =0; i < coord.length; i ++){
				sql+="z"+i +" asc";
				if(i+1 != coord.length){
					sql+=",";
				}
			}
		rs = stmt.executeQuery(sql);
		while(rs.next()){
			for(int i = 0; i < rs.getMetaData().getColumnCount(); i ++){
				coordSet2[i]=rs.getDouble(i+1);
			}
			break;
		}
		
		//  calc distance using sqrt(coord1*coord1 + coord2*coord2+...)
		for(int i =0; i < coordSet1.length; i ++){
			distance1 += coordSet1[i] * coordSet1[i];
		}
		for(int i =0; i < coordSet1.length; i ++){
			distance2 += coordSet2[i] * coordSet2[i];
		}
		distance1 = Math.sqrt(distance1);
		distance2 = Math.sqrt(distance2);
		coord = (distance1 > distance2) ? coordSet1 : coordSet2;
		
		sql = "select id from "+table+" where x=" + coord[0] +" and y=" + coord[1];
		for(int i=0; i < coord.length; i ++){
			sql+="z"+i+"="+coord[i];
			if(i+1!=coord.length){
				sql+="and";
			}
		}
		rs = stmt.executeQuery(sql);
		while(rs.next()){
			rt = rs.getString(1);
		}
		return rt;
	}

	public static double[] parsePCAText(String text){
		
		String[] arr = text.split("[kmer]");
		double[] returnarr = new double[arr.length];
		for(int i = 0; i < arr.length; i += 4){
			if(i==0){
				String zeroSpot = arr[0].replaceAll(" ", "");
				String first = "";
				if(zeroSpot.contains("-")){
					first = "-"+zeroSpot.split("-")[1];
				}
				else{
					first = zeroSpot.substring(zeroSpot.length()-5, zeroSpot.length());
				}
				returnarr[Integer.valueOf(arr[i+4].split("[+-]")[0]) - 1] = Double.valueOf(first);
				continue;
			}
			else if(arr[i].contains("-")){
				String[] arr2 = arr[i].split("[-]");
					int posval = Integer.valueOf(arr[i+4].split("[+-]")[0]);
					returnarr[posval - 1] = Double.valueOf(arr2[1]) * -1.0;
				
			}
			
			else if(arr[i].contains("+")){
				String[] arr2 = arr[i].split("[+]");
				
					returnarr[Integer.valueOf(arr[i+4].split("[+-]")[0]) - 1] = Double.valueOf(arr2[1]);
			}
			else if(!arr[i].contains("+-") && i == 0){
				returnarr[Integer.valueOf(arr[i+4].split("[+-]")[0]) - 1] = Double.valueOf(arr[i]);
			}
		}
		
		return returnarr;
	}

	private static double getPCAY(double[] kmer, double[] pcaArr) {
		double retval = 0.0;
		
		for(int i = 0; i < kmer.length; i ++){
			retval += pcaArr[i] * kmer[i];
		}
		
		return retval;
	}

	private static double getPCAX(double[] kmer, double[] pcaArr) {
		
		double retval = 0.0;
		
		for(int i = 0; i < kmer.length; i ++){
			retval += pcaArr[i] * kmer[i];
		}
		
		return retval;
		
	}

	private static void findNearestSequencesS(String currpeg, File f, int filenum) throws IOException {
		String sequence = "";
		String id = "";
		BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
		String line = "";
		while ((line = bufferedReader.readLine()) != null) {
			id = line;
			if (id.equals(currpeg)) {
				sequence = bufferedReader.readLine();
				sequence = replaceNucs(sequence);
				break;
			}

			// System.out.println(sequence);
			// if (count>TOTAL_VALS) {
			id = "";
			sequence = "";
		}
		bufferedReader.close();

		PrintWriter pw = new PrintWriter(new File("Nearest\\match" + filenum + "s.fasta"));
		pw.write(id);
		pw.write('\n');
		pw.write(sequence);
		pw.close();
	}

	private static void findNearestSequencesQ(String currpeg, File f, int filenum) throws IOException {
		// TODO Auto-generated method stub
		String sequence = "";
		String id = "";
		BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
		String line = "";
		while ((line = bufferedReader.readLine()) != null) {
			id = line;
			if (id.equals(currpeg)) {
				sequence = bufferedReader.readLine();
				sequence = replaceNucs(sequence);
				break;
			}

			id = "";
			sequence = "";
		}
		bufferedReader.close();

		PrintWriter pw = new PrintWriter(new File("Nearest\\match" + filenum + "q.fasta"));
		pw.write(id + "\n" + sequence);
		pw.close();

	}

	private static List findThreshold(double d, double e, KDTree test, double[] coord) throws KeySizeException {
		// TODO Auto-generated method stub

		//  return test.range(new double[]	{ x - d, y - e, ...}, new double[]	{x + d, y + e, ...} ) 
		return test.range(new double[] { coord[0] - d, coord[1] - e }, 
				new double[] { coord[0] + d, coord[1] + e });

	}

	private static void writeToThresholdCSV(Gene gene, int sameCog, List<String> otherGenesFound, String title)
			throws FileNotFoundException {
		// TODO write out CSV file formatted peg #, same found, others in area
		PrintWriter pw = new PrintWriter(new File(title + ".csv"));
		StringBuilder sb = new StringBuilder();
		sb.append(getPeg(gene.Cog));
		sb.append(',');
		sb.append(sameCog);
		sb.append(',');
		for (String genes : otherGenesFound) {

			sb.append(genes);

		}
		sb.append('\n');
		pw.append(sb.toString());
		pw.close();

	}

	/**
	 * Will take the cluster map and write to a CSV file. Singularly purposed.
	 * Do not touch
	 * 
	 * @param clusterMap
	 * @param centers
	 * @param averages
	 * @param medians
	 * @throws FileNotFoundException
	 */
	private static void writeToCSV(HashMap<Integer, List<Double[]>> clusterMap, HashMap<Integer, Double[]> centers,
			HashMap<Integer, Double> averages) throws FileNotFoundException {
		// TODO use a print writer to write all the averages to a CSV file in
		// the following format
		// peg # (aka the Cog or key val for the hmaps), # of points in cluster,
		// mean distance, median distance

		PrintWriter pw = new PrintWriter(new File("cluster distance.csv"));
		StringBuilder sb = new StringBuilder();
		sb.append("peg #");
		sb.append(',');
		sb.append("# of points in cluster");
		sb.append(',');
		sb.append("mean distance");
		sb.append(',');
		//sb.append("median distance");
		sb.append('\n');
		for (Integer key : clusterMap.keySet()) {
			sb.append(key.toString());
			sb.append(',');
			sb.append(Integer.toString(clusterMap.get(key).size()));
			sb.append(',');
			sb.append(averages.get(key).toString());
			sb.append(',');

			// sb.append(Collections.sort((List<T>)
			// Arrays.asList(averages.values().toArray())));
			sb.append('\n');
		}
		pw.write(sb.toString());
		pw.close();
		System.out.println("CSV distance file written");

	}

	/**
	 * Will calculate the median distance for the points
	 * 
	 * @param clusterMap
	 *            the map of all the clusters. The key is the Cog (or peg) value
	 *            and the values are lists of coordinates associated with the
	 *            Cog
	 * @param centers
	 *            the map of centers. The key is the cog and the values are the
	 *            center of the clusters associated with the cog
	 * @return
	 */
	private static HashMap<Integer, Double[]> calculateMedianDistanceFromCenters(
			HashMap<Integer, List<Double[]>> clusterMap, HashMap<Integer, Double[]> centers) {
		List<List<Double[]>> clusters = new ArrayList<List<Double[]>>(clusterMap.values());
		List<Double> xvals = new ArrayList<Double>();
		List<Double> yvals = new ArrayList<Double>();
		HashMap<Integer, Double[]> medians = new HashMap<Integer, Double[]>();
		for (Integer key : clusterMap.keySet()) {
			List<Double[]> points = clusterMap.get(key);
			for (Double[] coords : points) {
				xvals.add(coords[0]);
				yvals.add(coords[1]);
			}
			Collections.sort(xvals);
			Collections.sort(yvals);
			if (xvals.size() % 2 == 0) {
				medians.put(key, new Double[] { xvals.get(xvals.size() / 2 - 1), yvals.get(yvals.size() / 2 - 1) });
			} else {
				medians.put(key, new Double[] { xvals.get(xvals.size() / 2), yvals.get(yvals.size() / 2) });
			}

		}
		return medians;
	}

	/**
	 * Calculates the average, or mean, distance from the centers for each of
	 * the cluster's values
	 * 
	 * @param clusterMap
	 *            - the map containing the list of points associated with a Cog
	 *            as the key
	 * @param centers
	 *            - the map with coordinates for the center of each cluster with
	 *            a cog for the cluster center as the key
	 * @return a hashmap with a Cog as the key and the average distance of all
	 *         points from the center
	 */
	private static HashMap<Integer, Double> calculateAverageDistanceFromCenters(
			HashMap<Integer, List<Double[]>> clusterMap, HashMap<Integer, Double[]> centers) {
		// DONE: find distance between center and all points. then add them
		// together and divide by length of distance array
		HashMap<Integer, List<Double>> distanceMap = new HashMap<Integer, List<Double>>();

		for (Integer key : clusterMap.keySet()) {

			distanceMap.put(key, new ArrayList<Double>());

			// finding the distance between a pair of coordinates and the center
			for (Double[] coords : clusterMap.get(key)) {
				Double x1 = coords[0];
				Double y1 = coords[1];
//				Double z1 = coords[2];
//				Double q1 = coords[3];
//				Double r1 = coords[4];
				Double x2 = centers.get(key)[0];
				Double y2 = centers.get(key)[1];
//				Double z2 = centers.get(key)[2];
//				Double q2 = centers.get(key)[3];
//				Double r2 = centers.get(key)[4];
				distanceMap.get(key).add(Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1) 
						));
			}

		}

		// add up the distances and divide by the size of the list
		Double distances = 0.0;
		HashMap<Integer, Double> averages = new HashMap<Integer, Double>();
		for (Integer key : distanceMap.keySet()) {
			distances = 0.0;

			for (Double distance : distanceMap.get(key)) {
				distances += distance;
			}
			// map the key to the respective average
			averages.put(key, distances / distanceMap.get(key).size());
		}
		return averages;
	}

	/**
	 * will calculate the centers of the clusters in the KDTree
	 * 
	 * @param clusterMap
	 *            - a map with the list of coordinates associated with a cluster
	 *            and a cog as a key for the points
	 * @return will return a hashmap with the coordinates of the center as the
	 *         value and the cluster's cog as the key
	 */
	private static HashMap<Integer, Double[]> calculateClusterCenters(HashMap<Integer, List<Double[]>> clusterMap) {
		// DONE: go through and calculate the x and y coordinates for each of
		// the clusters
		HashMap<Integer, Double[]> centers = new HashMap<Integer, Double[]>();
		Double totalX = 0.0;
		Double totalY = 0.0;
//		Double totalZ = 0.0;
//		Double totalQ = 0.0;
//		Double totalR = 0.0;
		for (Integer key : clusterMap.keySet()) {
			totalX = 0.0;
			totalY = 0.0;
//			totalZ = 0.0;
//			totalQ = 0.0;
//			totalR = 0.0;
			// find the center of the cluster
			for (Double[] coords : clusterMap.get(key)) {
				totalX += coords[0];
				totalY += coords[1];
//				totalZ += coords[2];
//				totalQ += coords[3];
//				totalR += coords[4];
			}
			centers.put(key, new Double[] { totalX / clusterMap.get(key).size(), totalY / clusterMap.get(key).size(), 
					 });
		}

		return centers;
	}

	//
	/**
	 * 	The current way to take in and process genes from a file
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static Vector<Gene> InputAndProcessGenesCategoryTest(File f) throws IOException{
		boolean first = true;
		String sequence = "";
		Vector<Gene> storage = new Vector<Gene>();
		String id = "";
		BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
		String line = "";
		int count = 0;
		while ((line = bufferedReader.readLine()) != null) {
			if(line.contains("USS-DB")){
				sequence = bufferedReader.readLine();
				id = "";
				sequence = "";
				continue;
			}
			id = line;
			sequence = bufferedReader.readLine();
			sequence = replaceNucs(sequence);
			if(!id.contains("hypothetical") || !id.contains("Hypothetical")){
				storage.add(new Gene(id, processSequencebyKmer(sequence, kmerToDo)));
				count++;
			}
			
			
			 if (count>100000) {
//			if (count > 1000) {
				break;
			}
			id = "";
			sequence = "";
		}
		bufferedReader.close();
		return storage;
	}
	
	
	/**
	 * looks through the file and takes in the gene sequences and
	 * classifications
	 * 
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static Vector<Gene> InputAndProcessGenesLine(File f, double[] xEQN, double[] yEQN ) throws IOException {
		boolean first = true;
		String sequence = "";
		Vector<Gene> storage = new Vector<Gene>();
		String id = "";
		BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
		String line = "";
		int count = 0;
		while ((line = bufferedReader.readLine()) != null) {
			id = line;
			sequence = bufferedReader.readLine();
			sequence = replaceNucs(sequence);
			// System.out.println(sequence);
//			storage.add(new Gene(id, processSequencebyKmer(sequence, kmerToDo)));
			storage.add(new Gene(id, getPCAX(processSequencebyKmer(sequence, kmerToDo), xEQN), getPCAY(processSequencebyKmer(sequence, kmerToDo), yEQN)));
			
			count++;
			 if (count>TOTAL_VALS) {
//			if (count > 1000) {
				break;
			}
			id = "";
			sequence = "";
		}
		bufferedReader.close();
		return storage;
	}

	public static String getPeg(String id) {
		return id.substring(id.indexOf("peg.") + 4);
	}

	public static Vector<Gene> InputAndProcessGenes(File f) throws IOException {
		boolean first = true;
		String sequence = "";
		Vector<Gene> storage = new Vector<Gene>();
		String id = "";
		BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
		String line = "";
		int count = 0;
		while ((line = bufferedReader.readLine()) != null) {
			if (line.contains(">")) {
				if (first)
					first = false;
				else
					sequence = replaceNucs(sequence);
				// TODO: Filter out unneeded data in ID

				storage.add(new Gene(id, processSequencebyKmer(sequence, kmerToDo)));
				count++;
				if (count > 100000) {
					break;
				}
				id = "";
				sequence = "";
				id = line.replace("'", "").replaceAll(",", "");
			} else {
				sequence += line;
			}
		}
		bufferedReader.close();
		return storage;
	}

	public static Double getMutualInformation(double[] comps, double[] y) {
		// MutualInformation mi = new MutualInformation();
		return MutualInformation.calculateMutualInformation(comps, y);
	}

	public static Double getR(double[] comps, double[] y) {
		double xAverage = 0;
		double yAverage = 0;
		double zAverage = 0;
		// THIS NORMALIZES!!
		double xCount = 0;
		double yCount = 0;
		double zCount = 0;
		for (int i = 0; i < comps.length; i++) {
			xCount += comps[i];
			yCount += y[i];
			
		}
		for (int i = 0; i < comps.length; i++) {
			comps[i] = comps[i] / xCount;
			y[i] = y[i] / yCount;
			
		}
		for (int i = 0; i < comps.length; i++) {
			xAverage += comps[i];
			yAverage += y[i];
		}
		xAverage = xAverage / comps.length;
		yAverage = yAverage / y.length;
		double xy = 0;
		double xSq = 0;
		double ySq = 0;
		for (int i = 0; i < comps.length; i++) {
			comps[i] = comps[i] - xAverage;
			y[i] = y[i] - yAverage;
			xy += (comps[i] * y[i]);
			xSq += comps[i] * comps[i];
			ySq += y[i] * y[i];
		}
		double r = xy / Math.sqrt(xSq * ySq);
		return r;
	}

	public static Double getRTau(double[] comps, double[] y) {

		KendallsCorrelation kcor = new KendallsCorrelation();
		return kcor.correlation(comps, y);
	}

	public static String replaceNucs(String sequence) {
		sequence = sequence.replaceAll("B", "N");
		sequence = sequence.replaceAll("D", "N");
		sequence = sequence.replaceAll("E", "N");
		sequence = sequence.replaceAll("F", "N");
		sequence = sequence.replaceAll("H", "N");
		sequence = sequence.replaceAll("I", "N");
		sequence = sequence.replaceAll("J", "N");
		sequence = sequence.replaceAll("K", "N");
		sequence = sequence.replaceAll("L", "N");
		sequence = sequence.replaceAll("M", "N");
		sequence = sequence.replaceAll("O", "N");
		sequence = sequence.replaceAll("P", "N");
		sequence = sequence.replaceAll("Q", "N");
		sequence = sequence.replaceAll("R", "N");
		sequence = sequence.replaceAll("S", "N");
		sequence = sequence.replaceAll("U", "N");
		sequence = sequence.replaceAll("V", "N");
		sequence = sequence.replaceAll("W", "N");
		sequence = sequence.replaceAll("X", "N");
		sequence = sequence.replaceAll("Y", "N");
		sequence = sequence.replaceAll("Z", "N");
		sequence = sequence.toUpperCase();
		return sequence;
	}

	/**
	 * returns the sequence of the genomes, which is then turned into the axis
	 * on the grid
	 * 
	 * @param f
	 *            the file of genomes
	 * @return a string sequence representing the genomes
	 * @throws IOException
	 */
	public static String inputGenomeSequence(File f) throws IOException {
		String sequence = "";
		String id = "";
		BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
		bufferedReader.readLine();
		StringBuilder stringbuilder = new StringBuilder();
		String line = "";
		while ((line = bufferedReader.readLine()) != null) {
			if (line.contains(">") == false) {
				stringbuilder.append(line);
			} else {
				// just so we have all included kmers within the nnn
				stringbuilder.append("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
			}
		}
		sequence = stringbuilder.toString();
		bufferedReader.close();
		return sequence;
	}

	/**
	 * Method to get kmer composition
	 * 
	 * @param sequence
	 *            the sequence found from the input genome sequence method
	 * @param mermer
	 *            the size of the kmer
	 * @return a double representing the number of matched kmers found in the
	 *         sequence
	 * @throws FileNotFoundException
	 */
	public static double[] processSequencebyKmer(String sequence, int mermer) {
		nmer = mermer;
		numShifts = 64 - (2 * (nmer - 1));
		numShiftsMinus = numShifts - 2;
		double[] comps = runGetKmers(sequence);
		return comps;
	}

	public static double[] runGetKmers(String sequence) {
		double[] kmerComp = new double[(int) Math.pow(4, nmer)];
		String[] toRun = sequence.split("N");
		for (int i = 0; i < toRun.length; i++) {
			if (toRun[i].length() >= nmer) {
				kmerComp = getKmers(toRun[i], kmerComp);
			}
		}
		double sum = 0;
		for (int i = 0; i < kmerComp.length; i++) {
			sum += kmerComp[i];
		}
		for (int i = 0; i < kmerComp.length; i++) {
			kmerComp[i] = kmerComp[i];
		}
		return kmerComp;
	}

	public static double[] getKmers(String sequence, double[] kmerComp) {
		// initialize first set of kmers
		Long temp = null;
		Long full = Long.parseUnsignedLong("0");
		int i = 0;
		for (i = 0; i < nmer; i++) {
			temp = nucToNum(sequence.charAt(i));
			try {
				full = full + temp;
			} catch (NullPointerException e) {
				System.out.println(sequence.substring(i));
			}
			// full = full + temp;
			if (i < nmer - 1) {
				full = full << 2;
			}
		}
		// add it and its reverse kmer to count array
		kmerComp[full.intValue()] += 1;
		kmerComp[reverser(full)] += 1;

		// delete first nucleotide and add to the end of it
		// add it and its reverse complement to count array
		while (i < sequence.length()) {
			temp = nucToNum(sequence.charAt(i));
			full = fancyShift(full);
			try {
				full = full + temp;
				kmerComp[full.intValue()] += 1;
				kmerComp[reverser(full)] += 1;
				i++;
			}

			// TODO:What the hell is going on here?
			catch (NullPointerException e) {
				break;
			}
		}
		// return kmerComposition array
		return kmerComp;
	}

	public static Long fancyShift(Long a) {
		a = a << numShifts;
		a = a >>> numShiftsMinus;
		return a;
	}

	public static int reverser(Long xx) {
		xx = ~xx;
		xx = Long.reverse(xx);
		xx = xx >>> numShiftsMinus;
		String rc = Long.toBinaryString(xx);
		int length = rc.length();
		if (length % 2 == 1) {
			rc = '0' + rc;
		}
		char[] twosies = rc.toCharArray();
		String newString = "";
		for (int i = 0; i < twosies.length; i = i + 2) {
			if (twosies[i] == '0' && twosies[i + 1] == '1') {
				twosies[i] = '1';
				twosies[i + 1] = '0';
			} else if (twosies[i] == '1' && twosies[i + 1] == '0') {
				twosies[i] = '0';
				twosies[i + 1] = '1';
			}
			newString += twosies[i];
			newString += twosies[i + 1];
		}
		Long l = parseLong(newString, 2);
		return l.intValue();
	}

	private static long parseLong(String s, int base) {
		return new BigInteger(s, base).longValue();
	}

	// Silly initializations for optimization
	public static Long aLong = Long.parseUnsignedLong("0");
	public static Long cLong = Long.parseUnsignedLong("1");
	public static Long gLong = Long.parseUnsignedLong("2");
	public static Long tLong = Long.parseUnsignedLong("3");

	// turns a nucleotide character to a binary representation
	public static Long nucToNum(char a) {
		switch (a) {
		case 'A':
			return aLong;
		case 'C':
			return cLong;
		case 'G':
			return gLong;
		case 'T':
			return tLong;
		case 'U':
			return tLong;
		default:
			return null;
		}
	}

}
