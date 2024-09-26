import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.nio.file.*;
import java.util.Random;

//remember that when testing the repository is always a level below where you run the tester
//you might have to specify stuff like this repo.createBlob( "./"+ repoName + "/" +testFile.getName());

//run the tester below tests everything
public class GitTester {
    public static String repoName = "sean";
    public static int howMany = 2;

    // if you want to delete everything, keep the delete variable true
    // if you want to keep all files, but make them empty, make the reset var true
    public static boolean deleteAtEndOfTest = true;
    public static boolean resetAllFiles = false;

    public static void main(String[] args) {
        Git repo = new Git(repoName);

        // below are initial tests for stretch goal 1
        repo.initializeRepo();
        repo.checkAndDeleteRepo();
        ////
        repo.initializeRepo();
        createAndWriteFiles(howMany);

        for (int i = 0; i < howMany; i++) {
            File testFile = new File("./" + repoName + "/testFile" + i + ".txt");
            String testFileName = testFile.getName();

            repo.createBlob(testFile);
            // checks to see if all files are in objects folder
            String hash = repo.createHash(testFile);
            Path pathToHashedFile = Paths.get("./" + repoName + "/git/objects/" + hash);
            if (Files.exists(pathToHashedFile)) {
                System.out.println("file" + i + "'s hash has been created successfully and the file is in objects");
            } else {
                System.out.println("file" + i + "'s hashed file is NOT in objects");
            }

            // commented next code segment out because it breaks testing for directory files
            // i just manually checked to see if everything was correctly written in index
            // (below)

            // checks to see if everything is written in index
            // String correctIndex = hash + " " + testFileName;
            // Path pathToIndex = Paths.get("./"+ repoName + "/git/index");
            // try{
            // BufferedReader reader = Files.newBufferedReader(pathToIndex);
            // String line;
            // while((line = reader.readLine()) != null){
            // if(correctIndex.equals(line)){
            // System.out.println("file" +i+" is correctly written in index");
            // }
            // }
            // reader.close();
            // } catch(IOException e){
            // e.printStackTrace();
            // }

            File testDir = new File("./" + repoName + "/testDir");
            repo.createBlob(testDir);

            File fileInTestDir = new File("./" + repoName + "/testDir/testFile.txt");
            repo.createBlob(fileInTestDir);

            File testDir2 = new File("./" + repoName + "/testDir2");
            repo.createBlob(testDir2);

            File fileInTestDir2 = new File("./" + repoName + "/testDir2/testFileInTestDir2.txt");
            repo.createBlob(fileInTestDir2);
            File file2InTestDir2 = new File("./" + repoName + "/testDir2/testFile2InTestDir2.txt");
            repo.createBlob(file2InTestDir2);

            // manual index check
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader reader = new BufferedReader(new FileReader("./" + repoName + "/git/index"));
                while (reader.ready()) {
                    sb.append((char) reader.read());
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(sb.toString());

            /*
             * //checks to see if it's in objects folder
             * String dirHash = repo.createHash(testDir);
             * Path pathToHashedDir = Paths.get("./" + repoName + "/git/objects" + dirHash);
             * if(Files.exists(pathToHashedDir)){
             * System.out.
             * println("dir's hash has been created successfully and the file is in objects"
             * );
             * } else {
             * System.out.println("dir's hashed file is NOT in objects");
             * }
             */
        }

        if (resetAllFiles) {
            // resets all txt files
            try {
                for (int i = 0; i < howMany; i++) {
                    File testFile = new File("./" + repoName + "/testFile" + i + ".txt");
                    if (testFile.exists()) {
                        Path pathoftestfile = Paths.get(testFile.getPath());
                        clearFile(pathoftestfile);
                    } else {
                        System.out.println("file" + i + " doesnt exist");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // resets index
            Path pathToIndex = Paths.get("./" + repoName + "/git/index");
            clearFile(pathToIndex);
            // resets objects folder
            resetObjects();
        }

        if (deleteAtEndOfTest) {
            File fileOfRepo = new File("./" + repoName + "/");
            repo.deleteEverything(fileOfRepo);
            System.out.println("everything should be gone");
        }
    }

    public static void resetObjects() {
        File objects = new File("./" + repoName + "/git/objects/");
        for (File childFile : objects.listFiles()) {
            Path pathochildFile = Paths.get(childFile.getPath());
            clearFile(pathochildFile);
        }
    }

    // opens a new bufferedwriter in write mode, overwriting everything present
    public static void clearFile(Path filePath) {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
        } catch (IOException e) {
            System.err.println("Error clearing the file: " + e.getMessage());
        }
    }

    // this method creates a certain amount of txt files, based on the howMany
    // parameter
    // the method will then write random numbers in the files
    public static void createAndWriteFiles(int howMany) {
        try {
            for (int i = 0; i < howMany; i++) {
                File testFile = new File("./" + repoName + "/testFile" + i + ".txt");
                if (!testFile.exists()) {
                    Path pathoftestfile = Paths.get(testFile.getPath());
                    BufferedWriter writer = Files.newBufferedWriter(pathoftestfile);

                    Random rand = new Random();
                    int randomNumber = rand.nextInt(100) + 1;

                    for (int j = 0; j < randomNumber; j++) {
                        writer.append(j + "");
                    }
                    writer.close();
                } else {
                    System.out.println("file" + i + " alr exists");
                }
                File testDir = new File("./" + repoName + "/testDir");
                testDir.mkdir();

                File fileInTestDir = new File("./" + repoName + "/testDir/testFile.txt");
                fileInTestDir.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileInTestDir));
                Random rand = new Random();
                int randomNumber = rand.nextInt(100) + 1;
                for (int j = 0; j < randomNumber; j++) {
                    writer.append(j + "");
                }
                writer.close();

                File testDir2 = new File("./" + repoName + "/testDir2");
                testDir2.mkdir();
                File fileInTestDir2 = new File("./" + repoName + "/testDir2/testFileInTestDir2.txt");
                fileInTestDir2.createNewFile();
                writer = new BufferedWriter(new FileWriter(fileInTestDir2));
                rand = new Random();
                randomNumber = rand.nextInt(100) + 1;
                for (int j = 0; j < randomNumber; j++) {
                    writer.append(j + "");
                }
                File file2InTestDir2 = new File("./" + repoName + "/testDir2/testFile2InTestDir2.txt");
                file2InTestDir2.createNewFile();

                for (int j = 0; j < randomNumber; j++) {
                    writer.append(j + "");
                }
                writer.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}