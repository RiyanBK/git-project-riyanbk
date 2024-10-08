
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.nio.file.*;
import java.util.Random;
import java.security.NoSuchAlgorithmException;

/*
 * potential bugs list - Kyara
 * file not found exception -> check if the method you called have "reponame"
 * if yes, remove it
 * 
 * notes for Kyara
 * Commit is a function
 * Head changes in the commit function
 * 
 * tree file is just 1 file, contains whatever in index file
 * 
 */


public class tempTester {
    public static String name = "git-project-riyanbk";
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException{
        mkTesters();
        Git test = new Git(name);
        test.initializeRepo();
        File testingFolder = new File("./testingFolder");
        // File testFile = new File("./testFile");
        // BufferedWriter tf = new BufferedWriter(new FileWriter(testFile));
        // tf.write("sos");
        // tf.close();

        test.createBlob(testingFolder);
        test.commit();
    }

    private static void mkTesters() throws IOException, NoSuchAlgorithmException {
        File testingFolder = new File("./testingFolder");
        testingFolder.mkdir();
        File indexFile = new File(testingFolder, "item");
        BufferedWriter tf = new BufferedWriter(new FileWriter(indexFile));
        tf.write("why is this so confusing");
        File testingFolder2 = new File(testingFolder, "testingFolder2");
        File test2 = new File(testingFolder2, "item2");
        BufferedWriter tf2 = new BufferedWriter(new FileWriter(test2));
        tf2.write("cry");
        tf.close();
        tf2.close();
    }
}
