package KScope.Code;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class MainRunner {
	
	public static void main(String[] args) {
		//  adding options
		Options options = new Options();
		options.addOption("pca", true, "The file path and name of the file containing the PCA equations to be parsed");
		options.addOption("trainin", true, "The path and name of the FASTA or FEATURE file that will be used to train the KDT");
		options.addOption("testin", true, "the path and name of the FASTA file that will be parsed and tested against the KDT");
		options.addOption("out", true, "the path and name of the FASTA file output by the program");
		options.addOption("traindb", "if you wish to use the training file to train the database.  Do not include this option if your database has already been trained.");
		options.addOption("numthread", true, "the number of threads you wish to use for this task");
		options.addOption("fastatofeature", true, "if you wish to turn a FASTA file into a FEATURE file.  The acceptable input is either t or f where t will turn into a FEATURE file");
		options.addOption("kmer",true,"the kmer count you wish to use");
		options.addOption("help", "print this message");
		CommandLineParser parser = new DefaultParser();
		
			CommandLine line;
			try {
				line = parser.parse(options, args);
			
		
			if(line.hasOption("help")){
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("test help", options);
				System.exit(1);
			}
			
				String pca = line.getOptionValue("pca");
				String testin = line.getOptionValue("testin");
				String trainin = line.getOptionValue("trainin");
				String out = line.getOptionValue("out");
				int numthread = Integer.parseInt(line.getOptionValue("numthread"));
				int kmer = Integer.parseInt(line.getOptionValue("kmer"));
				boolean fastatofeature = (line.getOptionValue("fastatofeature").equals("t")) ? true : false;
				KDTOnlyMain.execute(
						pca,
						testin,
						trainin,
						out,
						numthread,
						kmer,
						fastatofeature
				);
		
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				HelpFormatter formatter = new HelpFormatter();
				
				formatter.printHelp("Please include all of the following in your command line", options);
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

}
