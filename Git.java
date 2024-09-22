import java.security.*;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.zip.DeflaterOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Git {
    public boolean compression = true;
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

    public void createBlob(String ogFilePath) {
        File ogFile = new File(ogFilePath);
        String ogFileName = ogFile.getName();

        if(!ogFile.exists()){
            throw new NullPointerException();
        }

        if(!ogFile.isFile() || ogFile.isDirectory()){
            throw new IllegalArgumentException();
        }

        if(compression){
            ogFile = compressed(ogFile);
        }

        //creates the file and hash
        String hash = createHash(ogFile);
        File hashedFile = new File("./" + repoName + "/git/objects/" + hash);
        
        // read from og file and copy contents into new file in objects
        if(!hashedFile.exists()){
            //System.out.println(ogFile.getPath());
            Path sourceFile = Paths.get(ogFile.getPath());
            //System.out.println(hashedFile.getPath());
            Path targetFile = Paths.get(hashedFile.getPath());
            writeFromOGtoHashedFile(sourceFile, targetFile);
        }

        // code below checks index and if file there doesnt write if there writes
        boolean existsInIndex = false;
        Path pathToIndex = Paths.get("./" + repoName + "/git/index");
        try{
            BufferedReader reader = Files.newBufferedReader(pathToIndex);
            String line;
            while((line = reader.readLine()) != null){
                if(line.equals(hash + " " + ogFileName)){
                    existsInIndex = true;
                    System.out.println("this file has already been indexed");
                }
            }
            reader.close();

            if(!existsInIndex){
                BufferedWriter writer = Files.newBufferedWriter(pathToIndex, StandardOpenOption.APPEND);
                writer.append(hash + " " + ogFileName + "\n");
                writer.close();
                System.out.println(ogFile.getName());
            }
        } catch (IOException e){
            System.out.println("couldnt print into index");
        }
    }

    public File compressed (File file){
        try{
            File compressedFile = File.createTempFile("compress", null);
            FileInputStream fis = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(compressedFile);
            DeflaterOutputStream dos = new DeflaterOutputStream(fos);
            int data = fis.read();
            while(data != -1){
                dos.write(data);
                data = fis.read();
            }
            dos.finish();
            fis.close();
            fos.close();
            dos.close();
            return compressedFile;
        } catch (IOException e){
            System.out.println("couldnt print");
        }
        return file;
    }
    

    //reads the source file, and prints it to the target
    private void writeFromOGtoHashedFile(Path source, Path target){
        try{
            InputStream reader = Files.newInputStream(source);
            OutputStream writer = Files.newOutputStream(target);
            int data;
            while ((data = reader.read()) != -1) {
                writer.write(data);
            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            System.err.println("couldnt print");
        }
    }

    /*
     * as per https://www.geeksforgeeks.org/sha-1-hash-in-java/
     * takes the contents of the files and makes a sha1 hash
     */
    public String createHash(File filePath) {
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


    public void deleteEverything(File file){
        for(File childFile : file.listFiles()){
            if(childFile.isDirectory()){
                deleteEverything(childFile);
            }
            childFile.delete();
        }
        file.delete();
    }
}