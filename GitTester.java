import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.nio.file.*;
import java.util.Random;


//remember that when testing the repository is always a level below where you run the tester
//you might have to specify stuff like this repo.createBlob( "./"+ repoName + "/" +testFile.getName());
public class GitTester {
    public static String repoName = "seanozalpasan";
    public static int howMany = 5;
    public static boolean deleteAtEndOfTest = false;
    
    public static void main(String[] args) {
        Git repo = new Git(repoName);
        repo.initializeRepo();
    
        isRepoSetupCorrectly();
        
        createAndWriteFiles(howMany);

        for(int i = 0; i<howMany ; i++){
            File testFile = new File( "./"+repoName+"/testFile" + i + ".txt");
            String testFileName = testFile.getName();

            if(repo.compression){
                testFile = repo.compressed(testFile);
            }

            repo.createBlob("./"+ repoName + "/" +testFileName);
            //checks to see if all files are in objects folder
            String hash = repo.createHash(testFile);
            Path pathToHashedFile = Paths.get("./"+ repoName + "/git/objects/" + hash);
            if(Files.exists(pathToHashedFile)){
                System.out.println(i+" hash created successfully and its in objects");
            } else {
                System.out.println(i+" hashed file does not seem to be in objects foldeer. check again");
            }
            //checks to see if everything is written in index
            String correctIndex = hash + " " + testFile.getName();
            Path pathToIndex = Paths.get("./"+ repoName + "/git/index");
            try{
                BufferedReader reader = Files.newBufferedReader(pathToIndex);
                String line;
                while((line = reader.readLine()) != null){
                    if(correctIndex.equals(line)){
                        System.out.println(i+ " this file is correctly stored in index");
                    }
                }
                reader.close();
            } catch(IOException e){
                System.out.println("smth went wrong");
            }
        }

        //out here the deletion of txt files as well as stuff in objects folder and index
        if(deleteAtEndOfTest){
            File fileOfRepo = new File("./" + repoName + "/");
            repo.deleteEverything(fileOfRepo);
            System.out.println("everything should be gone");
        }


    }
    //this method creates a certain amount of txt files, based on the howMany parameter
    //the method will then write in the files
    public static void createAndWriteFiles(int howMany) {
        try {
            for (int i = 0; i < howMany; i++) {
                File testFile = new File("./" + repoName + "/testFile" + i + ".txt");
                if(!testFile.exists()){
                    testFile.createNewFile();

                    Path pathoftestfile = Paths.get(testFile.getPath());
                    BufferedWriter writer = Files.newBufferedWriter(pathoftestfile);

                    Random rand = new Random();
                    int randomNumber = rand.nextInt(100) + 1;

                    for (int j = 0; j < randomNumber; j++) {
                        writer.append(j + "");
                    }
                    writer.close();
                } else {
                    System.out.println("this file alr exists reset by deleteEverything()");
                }
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