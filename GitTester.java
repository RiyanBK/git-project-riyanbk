import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.nio.file.*;
import java.util.Random;

public class GitTester {
    public static String repoName = "sean";
    
    public static void main(String[] args) {
        Git repo = new Git(repoName);
        repo.initializeRepo();
    
        isRepoSetupCorrectly();
        
        int howMany = 5;

        createAndWriteFiles(howMany);

        for(int i = 0; i<howMany ; i++){
            File testFile = new File("testFile" + i + ".txt");
            System.out.println(testFile.getAbsolutePath());
            repo.createBlob(testFile.getName());
            //add checks in here
        }

        //out here add the deletion of txt files as well as shi in objects folder and index



    }
    //this method creates a certain amount of txt files, based on the howMany parameter
    //the method will then write in the files, and you can use this for testing
    public static void createAndWriteFiles(int howMany) {
        try {
            for (int i = 0; i < howMany; i++) {
                File testFile = new File("testFile" + i + ".txt");
                testFile.createNewFile();

                Path pathoftestfile = Paths.get(testFile.getPath());
                BufferedWriter writer = Files.newBufferedWriter(pathoftestfile, StandardOpenOption.APPEND);
                
                Random rand = new Random();
                int randomNumber = rand.nextInt(30) + 1;
                
                for (int j = 0; j < randomNumber; j++) {
                    writer.append(j + " ");
                }
                writer.close();
            }

        } catch (IOException e) {
            System.out.println("couldnt print out the files");
        }
    }

    public static void isRepoSetupCorrectly(){
        File gitDirFile = new File("./" + repoName + "/git/");
        if (!gitDirFile.exists()) {
            System.out.println(gitDirFile.getAbsolutePath());

            File objectDirFile = new File("./" + repoName + "/git/objects/");
            if (!objectDirFile.exists()) {
                System.out.println(objectDirFile.getAbsolutePath());
            }

            File indexFile = new File("./" + repoName + "/git/index");
            if (!indexFile.exists()) {
                System.out.println(indexFile.getAbsolutePath());
            } else {
                System.out.println("could not create file");
            }
        }
    }

}