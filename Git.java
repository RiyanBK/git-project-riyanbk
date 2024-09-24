import java.security.*;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Git {
    //toggle for compression
    public boolean compression = false;
    //user sets this when Git is initialized in tester
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

    //the below method checks if the repo is setup correctly before any file are added in
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
        System.out.println("\n" + "initializing a repo works" + "\n");
        deleteEverything(gitDirFile);
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
            compressed(ogFile);
            System.out.println(ogFile.getName() + " this og file names name after compression");
        }

        //creates the file and hash
        String hash = createHash(ogFile);
        File hashedFile = new File("./" + repoName + "/git/objects/" + hash);
        if(compression){
            ogFile.renameTo(hashedFile);
            //hashedFile.delete();
        }
        
        
        // read from og file and copy contents into new file in objects
        if(!hashedFile.exists()){
            Path sourceFile = Paths.get(ogFile.getPath());
            Path targetFile = Paths.get(hashedFile.getPath());
            try{
                Files.copy(sourceFile, targetFile);
            } catch (IOException e){
                e.printStackTrace();
            }
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
            }
        } catch (IOException e){
            System.out.println("couldnt print into index");
        }
    }

    /*
     * ok guys
     * because a tempFile is created, when using compressed stuff its best to create
     * a String with the name of the file before it got compressed. this way its easier
     * to manage and keep track of stuff.
     * if we didnt have that, then we would be overwriting the original file, which is bad
     * 
     * example from tester
     *      File testFile = new File( "./"+repoName+"/testFile" + i + ".txt");
            String testFileName = testFile.getName();
            
            if(repo.compression){
                testFile = repo.compressed(testFile);
            }
            repo.createBlob("./"+ repoName + "/" +testFileName);
        
    as you can see we createBlob on the testFileName, not testFile.getName()
    */
    public void compressed (File file){
        // try{
        //     File compressedFile = File.createTempFile("compress", null);
        //     FileInputStream fis = new FileInputStream(file);
        //     FileOutputStream fos = new FileOutputStream(compressedFile);
        //     DeflaterOutputStream dos = new DeflaterOutputStream(fos);
        //     int data = fis.read();
        //     while(data != -1){
        //         dos.write(data);
        //         data = fis.read();
        //     }
        //     dos.finish();
        //     fis.close();
        //     fos.close();
        //     dos.close();
        //     return compressedFile;
        // } catch (IOException e){
        //     System.out.println("couldnt print");
        // }
        // return file;
        
        try {
            //File compressFile = File.createTempFile("compress", null);
            File compressFile = new File(repoName + "/git/objects/" + file.getName());
            //FileInputStream fis = new FileInputStream(file);
            FileOutputStream fileOut = new FileOutputStream(compressFile);
            ZipOutputStream zipOut = new ZipOutputStream(fileOut);
            zipOut.putNextEntry(new ZipEntry(compressFile.getPath())); 
            Path pathToFile = Paths.get(file.getPath());
            byte[] allBytes = Files.readAllBytes(pathToFile);
            //System.out.println(compressFile.getPath());
            System.out.println(compressFile.toString());
            zipOut.write(allBytes);
            zipOut.close();
            fileOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        //return null;
    }
    
    /*
     * as per https://www.geeksforgeeks.org/sha-1-hash-in-java/
     * takes the contents of the files and makes a sha1 hash
     */
    public String createHash(File filePath) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
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