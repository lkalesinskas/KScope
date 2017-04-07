/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author nickpredey
 */
public class ReadLines {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        File folder = new File("./Genomes");
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println(file.getAbsolutePath());
            }
        }
        int i = 0;

        int everyNumLetters = 1;
        int paceEveryNumLetters = 0;
        int numLetters = 0;
        Scanner in = new Scanner(System.in);
        System.out.println("Length of Reads wanted? ");
        numLetters = in.nextInt();
        String headerString = "";

        try (FileWriter fw = new FileWriter("GenomeSplit.fna")) {
            BufferedWriter bw = new BufferedWriter(fw);
            //PrintStream out = new PrintStream(new File("GenomeSplit.fna"));

            for (File a : listOfFiles) {
                BufferedReader reader = new BufferedReader(new FileReader(a));
                headerString = reader.readLine();
                System.out.println(headerString);
                String fileLine = "";
                char[] fileLineArray;

                everyNumLetters = 1;
                paceEveryNumLetters = 0;
                System.out.println("EveryNumLetters: " + everyNumLetters);

                while ((fileLine = reader.readLine()) != null) {
                    fileLineArray = fileLine.toCharArray();
                    for (int index = 0; index < fileLineArray.length; index++) {
                        if (paceEveryNumLetters == 0) {
                            bw.write(headerString + '-' + everyNumLetters + ':' + (everyNumLetters + numLetters - 1));
                            bw.newLine();
                        }
                        paceEveryNumLetters++;
                        everyNumLetters++;
                        bw.write(fileLineArray[index]);

                        if (paceEveryNumLetters == numLetters) {
                            bw.write('\n');
                            paceEveryNumLetters = 0;
                        }
                    }
                }
                bw.newLine();
            }
        }
        //PrintStream out = new PrintStream(new File("GenomeSplit.fna"));
    }

}
