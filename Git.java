import java.security.*;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;

public class Git {

    public String repoName;

    public Git(String repoName) {
        this.repoName = repoName;
    }

    public static void main(String[] args) {

    }

    public void initializeRepo() {
        File gitDirFile = new File("./" + repoName + "/git/");
        if (!gitDirFile.exists()) {
            gitDirFile.mkdirs();
            System.out.println(gitDirFile.getAbsolutePath());

            File objectDirFile = new File("./" + repoName + "/git/objects/");
            if (!objectDirFile.exists()) {
                objectDirFile.mkdir();
                System.out.println(objectDirFile.getAbsolutePath());
            }

            File indexFile = new File("./" + repoName + "/git/index");
            if (!indexFile.exists()) {
                try {
                    indexFile.createNewFile();
                    System.out.println(indexFile.getAbsolutePath());
                } catch (IOException e) {
                    System.out.println("could not create file");
                }
            }
        } else {
            System.out.println("Git Repository already exists");
        }
    }

    public void checkAndDeleteRepo() {
        File gitDirFile = new File("./" + repoName + "/git/");
        File objectDirFile = new File("./" + repoName + "/git/objects/");
        File indexFile = new File("./" + repoName + "/git/index");
        File repoDir = new File("./" + repoName + "/");
        if (gitDirFile.exists()) {
            System.out.println("the git directory exists. path is ./" + repoName + "/git/");
        }
        if (objectDirFile.exists()) {
            System.out.println("the objects directory exists. path is ./" + repoName + "/git/objects/");
        }
        if (indexFile.exists()) {
            System.out.println("the index file exists. path is ./" + repoName + "/git/index");
        }
        if (repoDir.exists()) {
            System.out.println("the repo direc exists. path is ./" + repoName + "/");
        }
        indexFile.delete();
        objectDirFile.delete();
        gitDirFile.delete();
        repoDir.delete();
    }

    // WHEN TESTING DO
    // createBlob(repoName/ogFilePath)
    // this is because Git.java and GitTester.java are one level above the repoName
    // and we are running those
    public void createBlob(String ogFilePath) {
        File ogFile = new File(ogFilePath);

        if(!ogFile.exists()){
            throw new NullPointerException();
        }

        if(ogFile.getParent() == null){
            throw new NullPointerException();
        }
        //creates the file and hash
        String hash = createHash(ogFile);
        File hashedFile = new File("./" + repoName + "/git/objects/" + hash);
        try {
            hashedFile.createNewFile();
        } catch (IOException e) {
            System.out.println("couldnt do it");
        }
        // read from og file and copy contents into new file j created above
        
        Path sourceFile = Paths.get(ogFilePath);
        Path targetFile = Paths.get("./" + repoName + "/git/objects/" + hash);
        
        writeFromOGtoHashedFile(sourceFile, targetFile);

        // code below writes to the index file
        Path pathToIndex = Paths.get("./" + repoName + "/git/index");
        try{
            BufferedWriter writer = Files.newBufferedWriter(pathToIndex, StandardOpenOption.APPEND);
            writer.append(hash + " ");
            writer.append(ogFile.getName() + "\n");
            writer.close();
        } catch (IOException e){
            System.out.println("couldnt print into index");
        }
    }

    //reads the source file, and prints it to the target
    private void writeFromOGtoHashedFile(Path source, Path target){
        try{
            BufferedReader reader = Files.newBufferedReader(source);
            BufferedWriter writer = Files.newBufferedWriter(target);
            String lineInSource;
            while ((lineInSource = reader.readLine()) != null) {
                writer.append(lineInSource);
                writer.newLine();
            }
            writer.close();
            reader.close();
        } catch (IOException e) {
            System.err.println("couldnt print");
        }
    }

    /*
     * as per https://www.geeksforgeeks.org/sha-1-hash-in-java/
     * takes the contents of the files and makes a sha1 hash
     */
    private String createHash(File filePath) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(Files.readAllBytes(filePath.toPath()));
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 40) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}