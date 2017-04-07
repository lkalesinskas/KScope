
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author nickpredey
 */
public class FinalAccuracyParser {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(new File("Outter.txt"));
        String mainCog = "";
        boolean initial = true;
        boolean inAPlane = false;
        String cogID = "";
        double numCogs = 0;
        int planeNumber = 0;
        boolean inPlane8 = false;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.startsWith(">") || (planeNumber == 8 && inPlane8)) {
                if (!initial) {
                    System.out.println("Num hits: " + (int) numCogs + "/" + 7 + "\n");
                    numCogs = 0;
                }
                initial = false;
                System.out.println(line);
                mainCog = line.split("\\|")[2]; //get the third position of line
                cogID = getCogId(mainCog);
                System.out.println("Our cog: " + cogID);
                //System.out.println("Main cog: " + mainCog);
            } else if (line.startsWith("[>")) {
                if (planeNumber == 8) {
                    inPlane8 = true;
                }
                inPlane8 = false;
                //this compares the main COG id to the numbers in the plane
                if (compareTwoCogs(cogID, getCogId(line.split("\\|")[2]))) {
                    System.out.println(" match");
                    numCogs++;
                } else {
                    System.out.println(" no match");
                }
                inAPlane = false;
            } else if (line.startsWith("P")) { //if its a plane
                inAPlane = true;
                System.out.print(line);
                planeNumber = Integer.parseInt(line.split("\\s+")[1].replace(":", ""));
            } else if (line.equals("") && inAPlane) {
                System.out.println(" no match");
                inAPlane = false;
            }
            if (!sc.hasNextLine()) {
                if (!initial) {
                    System.out.println("Num hits: " + (int) numCogs + "/" + 7);
                    numCogs = 0;
                }
            }
        }
    }

    /**
     * Gets a full cog ID and returns the set of characters at the end as the
     * correct ID.
     *
     * @param name
     * @return ID
     */
    public static String getCogId(String name) {
        StringBuilder cogId = new StringBuilder("");
        for (int i = name.length() - 1; i >= 0; i--) {
            char c = name.charAt(i);
            //if the character we are looking at is a letter, not a number.
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                cogId.append(c);
            } else {
                break;
            }
        }
        return cogId.reverse().toString();
    }

    /**
     * Compares our cog to one of the cogs in the plane to see if they share any
     * of the same characters
     *
     * @param ourCog
     * @param compCog
     * @return true if it's a match, false otherwise
     */
    public static boolean compareTwoCogs(String ourCog, String compCog) {
        for (int i = 0; i < ourCog.length(); i++) {
            if (compCog.contains(Character.toString(ourCog.charAt(i)))) {
                return true;
            }
        }
        return false;
    }

}
