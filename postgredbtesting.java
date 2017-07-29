package KScope;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class postgredbtesting {

	
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

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		kmerToDo =3;

		Class.forName("com.mysql.jdbc.Driver");

		try {
			Connection con = DriverManager.getConnection("jdbc:postgresql://[::1]:5740/root",username,pswd);
			Statement stmt;
			Statement stmt2;

			stmt2 = con.createStatement();

			stmt2.executeUpdate("CREATE DATABASE IF NOT EXISTS FIGFAMS");
			
			con.close();
			Connection con2 = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/figfams", username, pswd);
			stmt = con2.createStatement();
			
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
			int count = 0;
			int uid = 0;
			String id="";
			String line = "";
			String sequence = "";
			/**   TRAINING BEGIN   **/
//			while((line = br.readLine()) != null){
//				id = line;
//				sequence = br.readLine();
//				sequence = replaceNucs(sequence);
//				if(count < 3037271){
////					System.out.println(count);3037273
//					count++;
//					continue;
//				}
//				sql = "insert ignore into "+table+" (uid,id,sequence,";
//				for(int i = 0; i < equationList.size(); i ++){
//					sql+="z"+i;
//					if(i + 1 != equationList.size()){
//						sql +=",";
//					}
//				}
//				sql+=") values ("+uid +",'"+id+"','"+sequence+"',";
//				// calculate gene
//				double[] gene = processSequencebyKmer(sequence, kmerToDo);
//				double sumGene = 0.0;
//				for(int i2 = 0; i2 < gene.length; i2++){
//					sumGene+=gene[i2];
//				}
//				for(int i2 = 0; i2 < gene.length; i2++){
//					gene[i2] = gene[i2]/sumGene;
//				}
//				//  calculate the coordinates
//				for(int i = 0; i < equationList.size(); i ++){
//					sql+= getPCAX(gene, equationList.get(i));
//					if(i + 1 != equationList.size()){
//						sql +=",";
//					}
//				}
//				sql+=")";
//				stmt.executeUpdate(sql);
//				
//				
//				ps.setInt(1, uid);
//				uid++;
//				ps.setString(2, id);
//				ps.setString(3, sequence);
//				for(int i = 0; i < equationList.size(); i ++){
//					int spot = i + 4;
//					ps.setDouble(spot, getPCAX(gene, equationList.get(i)));
//				}
////				ps.addBatch();
//				count ++;
//				
//			}
//			ps.executeBatch();
			/**    TRAINING END     **/
			ResultSet size = stmt.executeQuery("select count(*) from pca3mertesting");
			while(size.next()){
				System.out.println("total rows " + size.getInt(1));
			}
			/**    BEGIN TESTING    **/
			File testFile = new File("TestOut3.ffn");
			Vector<Gene> testSequences = InputAndProcessGenesCategoryTest(testFile);
			System.out.println("We have " + testSequences.size() + " test sequences!");
			BufferedWriter bw = new BufferedWriter(new FileWriter("nearest100.csv"));
			bw.write("IDs at target coord, Nearest IDs with sequence");
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
							for(int c = 0; c < coord1.length; c ++){
								coord[c] = coord1[c];
							}
							
							//  random weird error happens without this
							if(coordArr[0].isNaN() || coordArr[1].isNaN()) continue;
						
							try{
								String sql = "select id from "+table+" where ";
								for(int i=0; i < coordArr.length; i ++){
									
									sql+="z"+i+"="+coordArr[i];
									if(i+1!=equationList.size()){
										sql+=" and ";
									}
								}
								ResultSet rs = stmt.executeQuery(sql);
								while(rs.next()){
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
			bw.close();
			/**   END TESTING   **/
			System.out.println("Hits: " + hit);
			System.out.println("Misclassified" + miss);
			System.out.println("Search Positive: " + searchPositive);
			System.out.println("Search Negative: " + searchNegative);
			con2.close();
			stmt.close();
			
//			String id = ">fig|657324.3.peg.930";
//			double a = -127.18;
//			double b = 51.72;
////			sql = "insert into figfam values('"+id+"', -127.18, 51.72) where not exists(select "+id+" from figfam where id='" + id+"');";
//			sql = "insert ignore into figfam (id, peg, x, y) values ('"+id+"','"+getPeg(id)+"', -127.18, 51.72)";
//			sql = "insert ignore into figfam (id, peg, x, y) values ('>fig|657324.3.peg.933','"+getPeg(id)+"', -135.18, 51.72)";
//			stmt.executeUpdate(sql);
//			
//			sql = "select id from figfam where x=" + a + " and y=1"+b;
//			ResultSet rs = stmt.executeQuery(sql);
//			while(rs.next()){
//				String id2 = rs.getString("id");
//				System.out.println(id2);
//			}
//			
//			rs = stmt.executeQuery("select count(*) from figfam");
//			while(rs.next()){
//				System.out.println("total rows " + rs.getInt(1));
//			}
//			
//			sql = "select x,y from figfam where peg='"+getPeg(id)+"'";
//			double totalX = 0.0;
//			double totalY = 0.0;
//			int total = 0;
//			rs = stmt.executeQuery(sql);
//			while(rs.next()){
//				totalX += rs.getDouble(1);
//				totalY += rs.getDouble(2);
//				total++;
//			}
//			
//			System.out.println("total: " + total);
//			System.out.println("av x: " + totalX/total);
//			System.out.println("av y: " + totalY/total);
//			
//			double x1 = 0.0;
//			double y1 = 0.0;
//			double x2 = 0.0;
//			double y2 = 0.0;
//			double distance1 = 0.0;
//			double distance2 = 0.0;
//			double distance = 0.0;
//			double[] coord = new double[2];
//			
//			sql = "select x,y from figfam where x < -130 or y < 52 order by x desc, y desc";
//			
//			rs = stmt.executeQuery(sql);
//			while(rs.next()){
//				System.out.println("found " + rs.getDouble(1) +" "+ rs.getDouble(2));
//				x1=rs.getDouble(1);
//				y1 = rs.getDouble(2);
//				break;
//			}
//			sql = "select x,y from figfam where x > -130 or y > 52 order by x asc, y asc";
//			
//			rs = stmt.executeQuery(sql);
//			while(rs.next()){
//				System.out.println("found " + rs.getDouble(1) +" "+ rs.getDouble(2));
//				x2=rs.getDouble(1);
//				y2 = rs.getDouble(2);
//				break;
//			}
//			
//			distance1 = Math.sqrt((-130-x2)*(-130-x2) + (52-y2)*(52-y2));
//			distance2 = Math.sqrt((x1+130)*(x1+130) + (y1-52)*(y1-52));
//			coord[0] = (distance1 > distance2) ? x2 : x1;
//			coord[1] = (distance1 > distance2) ? y2 : y1;
//			
//			System.out.println("x and y " + coord[0] + " " + coord[1]);
//			sql = "select peg from figfam where x=" + coord[0] +" and y=" + coord[1];
//			rs = stmt.executeQuery(sql);
//			while(rs.next()){
//				System.out.println("found " + rs.getString(1));
//			}
//
//			stmt.close();
//
//			if (con != null) {
//				con.close();
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			if (e.getErrorCode() == 1007) {
//				System.out.println("db already made");
//			} else
//				e.printStackTrace();
		
		}catch(SQLException e){
			if (e.getErrorCode() == 1007) {
				System.out.println("db already made");
			} else
				e.printStackTrace();
		}
		
		
	}
	
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
			sequence = sequence.substring(60, sequence.length() - 2);
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
		double x1 = 0.0;
		double y1 = 0.0;
		double x2 = 0.0;
		double y2 = 0.0;
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
	
	private static double getPCAX(double[] kmer, double[] pcaArr) {
		// -0.278kmer15-0.278kmer5-0.278kmer3-0.278kmer8-0.278kmer12-0.278kmer2-0.276kmer9-0.276kmer14-0.238kmer6-0.238kmer11-0.235kmer4-0.224kmer10-0.211kmer1-0.211kmer16-0.201kmer7-0.191kmer13
		
		double retval = 0.0;
		
		for(int i = 0; i < kmer.length; i ++){
			retval += pcaArr[i] * kmer[i];
		}
		
		return retval;
		
	}
	
	public static String getPeg(String id) {
		return id.substring(id.indexOf("peg.") + 4);
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
	
	// Silly initializations for optimization
		public static Long aLong = Long.parseUnsignedLong("0");
		public static Long cLong = Long.parseUnsignedLong("1");
		public static Long gLong = Long.parseUnsignedLong("2");
		public static Long tLong = Long.parseUnsignedLong("3");
		
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
