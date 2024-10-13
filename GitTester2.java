import java.io.*;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;

public class GitTester2 {
    public static String name = "git-project-riyanbk";

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Git test = new Git("git-project-riyanbk");
        test.initializeRepo();
        // tests first commit
        commitTest1(test);

        // tests second commit
        commitTest2(test);

        // clear everything
        deleteObjects();
    }

    private static void deleteObjects() throws FileNotFoundException {
        File objectesFolder = new File("./git/objects");
        for (File subFile : objectesFolder.listFiles()) {
            subFile.delete();
        }
        File Head = new File("./git/HEAD");
        PrintWriter pw = new PrintWriter(Head);
        pw.print("");
        pw.close();
        File testingFolder = new File("./testingFolder");
        testingFolderDelete(testingFolder);

    }

    private static void testingFolderDelete(File folder) throws FileNotFoundException {
        for (File subDir : folder.listFiles()) {
            if (subDir.isDirectory()) {
                testingFolderDelete(subDir);
            } else {
                subDir.delete();
            }
        }
        folder.delete();
    }

    private static void commitTest2(Git test) throws IOException, NoSuchAlgorithmException {
        File moreTester = new File("./testFile2");
        PrintWriter pw = new PrintWriter(moreTester);
        pw.print("commit2");
        pw.close();
        test.createBlob(moreTester);
        test.commit("Kyara", "test456, test456");
        moreTester.delete();
    }

    private static void commitTest1(Git test) throws IOException, NoSuchAlgorithmException {
        mkTesters();
        File testingFolder = new File("./testingFolder");
        test.createBlob(testingFolder);
        test.commit("sonarii", "test123, test123");
    }

    private static void mkTesters() throws IOException, NoSuchAlgorithmException {
        File testingFolder = new File("./testingFolder");
        testingFolder.mkdir();
        File indexFile = new File(testingFolder, "item");
        BufferedWriter tf = new BufferedWriter(new FileWriter(indexFile));
        tf.write("why is this so confusing");
        File testingFolder2 = new File(testingFolder, "testingFolder2");
        testingFolder2.mkdir();
        File test2 = new File(testingFolder2, "item2");
        BufferedWriter tf2 = new BufferedWriter(new FileWriter(test2));
        tf2.write("cry");
        tf.close();
        tf2.close();
    }
}
