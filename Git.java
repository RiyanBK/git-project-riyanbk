import java.security.*;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

public class Git {
    public static void main(String[] args) {

    }

    public static void initializeRepo(String repoName) {
        File gitDirFile = new File("./" + repoName + "/git/");
        if (!gitDirFile.exists()) {
            gitDirFile.mkdirs();

            File objectDirFile = new File("./" + repoName + "/git/objects/");
            if (!objectDirFile.exists()) {
                objectDirFile.mkdir();
            }

            File indexFile = new File("./" + repoName + "/git/index");
            if (!indexFile.exists()) {
                try {
                    indexFile.createNewFile();
                } catch (IOException e) {
                    System.out.println("could not create file");
                }
            }
        } else {
            System.out.println("Git Repository already exists");
        }
    }

    public static void checkAndDeleteRepo(String repoName) {
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

    /*
     * 3 STEPS TO THIS
     * ----------
     * create the hash --> blob string
     * make file and put it in objects
     * write the string to the index file
     */
    public static void createBlob(String filePath) {
        try {
            // getInstance() method is called with algorithm SHA-1
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(filePath.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 40 digits long
            while (hashtext.length() < 40) {
                hashtext = "0" + hashtext;
            }

        }
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}