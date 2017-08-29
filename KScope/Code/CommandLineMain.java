package KScope;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CommandLineMain {
	
	public static void main(String[] args) throws Exception{
		//  adding options
		Options options = new Options();
//		options.addOption("ram", true, "<arg> must be either t or f.  If the value is 't' then this will execute in RAM only mode otherwise it will execute the DB main.  Suggest DB used on machines with lower RAM");
		options.addOption("pca", true, "The file containing the PCA equations to be parsed");
		options.addOption("trainin", true, "The path and name of the FASTA or FEATURE file that will be used to train the KDT");
		options.addOption("testin", true, "the path and name of the FASTA file that will be parsed and tested against the KDT");
		options.addOption("out", true, "the path and name of the FASTA file output by the program");
		options.addOption("traindb", "if you wish to use the training file to train the database.  Do not include this option if your database has already been trained.");
		options.addOption("numthread", true, "the number of threads you wish to use for this task");
		options.addOption("fastatofeature", true, "if you wish to turn a FASTA file into a FEATURE file.  You will need to supply a PCA file using the -pca command and the number of kmers using the -kmer command to run properly.  You will then need to rerun the program with your new FEATURE file. <arg> must be your FASTA file");
		options.addOption("kmer",true,"the kmer count you wish to use");
		options.addOption("help", "print this message");
		CommandLineParser parser = new DefaultParser();
		try{
			CommandLine line = parser.parse(options, args);
		
			if(line.hasOption("help")){
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("test help", options);
				System.exit(1);
			}
			
//			else if(line.hasOption("fastatofeature")){
//				
//				FastaToFeatureFile.fastaToFeature(line.getOptionValue("fastatofeature"), line.getOptionValue("pca"), Integer.valueOf(line.getOptionValue("kmer")));
//				System.exit(2);
//			}
			
//			else if(line.getOptionValue("ram").equals("t")){
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
//			}
//			else if(line.getOptionValue("ram").equals("f")){
//				String pca = line.getOptionValue("pca");
//				String testin = line.getOptionValue("testin");
//				String trainin = line.getOptionValue("trainin");
//				String out = line.getOptionValue("out");
//				boolean train = line.hasOption("traindb") ? true : false;
//				int numthread = Integer.parseInt(line.getOptionValue("numthread"));
//				DBMain.execute(
//						pca,
//						testin,
//						trainin,
//						out,
//						train,
//						numthread
//				);
//			}
		}catch(Exception e){
			HelpFormatter formatter = new HelpFormatter();
			  
			formatter.printHelp("please include all of the following in your command line", options);
			e.printStackTrace();
		}
		
		
	}

}
