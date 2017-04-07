import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
//import java.nio.file.Files;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

/**
 * 
 */

/**
 * @author Max Kelly
 *
 */
public class FileFinder{
	public int UID = 0;
	public double XCoord = 0, YCoord = 0, distance = 0;
	public String TaxonomicalClassification = null, sequence = null;
	public ArrayList<Double> extraDimensions = new ArrayList<Double>();
	public Object[][] toObjectArray(){
		return null;
	}
	
	FileFinder(String Directory, Statement statement, String tableName, String[] columnNames) throws FileNotFoundException, IOException{
		File test = new File(Directory);
		int x = 0;
		int iterations = (columnNames.length - 3) / 3;
		
		//TODO: We're missing the last one.
		//  reading the file
		try(BufferedReader br = new BufferedReader(new FileReader(test))){
			String line;
			int planes = Integer.parseInt(br.readLine().trim());
			while((line = br.readLine()) != null){
				UID = Integer.parseInt(line.trim());
				TaxonomicalClassification = br.readLine().trim();
				//  UNCOMMENT IF ISSUES COME UP WHEN RUNNING AND TRYING TO PARSE CLASSIFICATION
//				TaxonomicalClassification = TaxonomicalClassification.replace("'", "");
//				TaxonomicalClassification = TaxonomicalClassification.replace("[", "");
//				TaxonomicalClassification = TaxonomicalClassification.replace("]", "");
//				TaxonomicalClassification = TaxonomicalClassification.replace("-", "");
				sequence = br.readLine().substring(0, 50);
				x+=3;
				extraDimensions.clear();
				
				for (int i = 0; i<iterations; i++) {
					line = br.readLine();
					//System.out.println(line);
					String[] sub = line.split(",");
					extraDimensions.add(Double.parseDouble(sub[0].trim()));
					extraDimensions.add(Double.parseDouble(sub[1].trim()));
					extraDimensions.add(Double.parseDouble(sub[2].trim()));
					x++;
				}
				
				//  WILL SEND THE ENTIRE QUERY TO DB
				if (TaxonomicalClassification.length()>250) {
					TaxonomicalClassification = TaxonomicalClassification.replace("'", "").substring(0, 250);
				}
				else {
					TaxonomicalClassification = TaxonomicalClassification.replace("'", "");
				}
				statement.executeUpdate( "insert into " + tableName + "(" + Arrays.toString(columnNames).substring(1, Arrays.toString(columnNames).length() - 1) + ") "
						+ "values (" + UID + ", '" + TaxonomicalClassification + "', '" + sequence + "'," + extraDimensions.toString().substring(1, extraDimensions.toString().length() - 1) 
						+ ");" );
				
				for (int i = 0; i<(planes-iterations); i++) {
					br.readLine();
					x++;
				}
			}
		}
		
		catch(Exception e){
			System.out.println(TaxonomicalClassification);
			System.out.println("FileFinder" + e.getMessage());
			e.printStackTrace();
		}
	}
	


}
