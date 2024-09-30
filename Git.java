import java.security.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;

//git reset --hard HEAD

public class Git {
    // toggle for compression
    public boolean compression = false;
    // user sets this when Git is initialized in tester
    public String repoName;

    public Git(String repoName) {
        this.repoName = repoName;
    }

    public void initializeRepo() {
        File gitDirFile = new File("./" + repoName + "/git/");
        if (!gitDirFile.exists()) {
            gitDirFile.mkdir();
        }
        // File objectDirFile = new File("./" + repoName + "/git/objects/");
        File indexFile = new File("./" + repoName + "/git/index");
        File objectDirFile = new File("./" + repoName + "/git/objects/");
        if (gitDirFile.exists() && objectDirFile.exists() && indexFile.exists()) {
            System.out.println("Git Repository already exists");
        } else {

            if (!gitDirFile.exists()) {
                gitDirFile.mkdir();
            }
            if (!objectDirFile.exists()) {
                objectDirFile.mkdir();
            }
            if (!indexFile.exists()) {
                try {
                    indexFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // if (!gitDirFile.exists()) {
        // gitDirFile.mkdirs();

        // if (!objectDirFile.exists()) {
        // objectDirFile.mkdirs();
        // }

        // if (!indexFile.exists()) {
        // try {
        // indexFile.createNewFile();
        // } catch (IOException e) {
        // System.out.println("could not create file");
        // }
        // }
        // } else {
        // System.out.println("Git Repository already exists");
        // }
    }

    // the below method checks if the repo is setup correctly before any file are
    // added in
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

    public void createBlob(File ogFile) {
        try {
            String ogFileName = ogFile.toString();
            // System.out.println ("og file name with toString():" + ogFile.toString());
            // //for testing
            // System.out.println ("og file name with getName(): " + ogFile.getName());
            // //for testing
            if (!ogFile.exists()) {
                throw new FileNotFoundException();
            }
            if (compression) {
                compressed(ogFile);
            }
            boolean isDir = ogFile.isDirectory();
            // String hash = "";
            // if (isDir) {
            // File temp = File.createTempFile(ogFile + "/dirData", null);
            // BufferedWriter writer = new BufferedWriter(new FileWriter (temp));
            // for (File file : ogFile.listFiles()) {
            // //System.out.println (file); //for testing
            // createBlob(file);
            // if (!file.isDirectory())
            // {
            // writer.write("blob " + createHash(file) + " " + file);
            // }
            // writer.write(file + "\n");
            // }
            // writer.close();
            // hash = createHash(temp);
            // } else {
            // // creates the file and hash
            // hash = createHash(ogFile);
            // }
            String hash = getHash(ogFile);

            // code below checks index and if file there, doesnt write; if there, writes
            boolean existsInIndex = false;
            Path pathToIndex = Paths.get("./" + repoName + "/git/index");

            BufferedReader reader = Files.newBufferedReader(pathToIndex);
            String line;
            while ((line = reader.readLine()) != null) {
                if (isDir) {
                    if (line.equals("tree " + hash + " " + ogFileName)) {
                        existsInIndex = true;
                        System.out.println("this file has already been indexed");
                    }
                } else {
                    if (line.equals("blob " + hash + " " + ogFileName)) {
                        existsInIndex = true;
                        System.out.println("this file has already been indexed");
                    }
                }

            }
            reader.close();

            if (!existsInIndex) {
                BufferedWriter writer = Files.newBufferedWriter(pathToIndex, StandardOpenOption.APPEND);
                if (isDir) {
                    writer.append("tree " + hash + " " + ogFileName + "\n");
                } else {
                    writer.append("blob " + hash + " " + ogFileName + "\n");
                }
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getHash(File f) {
        String hash = "";
        if (!f.isDirectory()) {
            hash = createHash(f);
        } else { // if is directory
            try {
                File temp = File.createTempFile(f + "/dirData", null);
                BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
                for (File file : f.listFiles()) {
                    // System.out.println (file); //for testing
                    createBlob(file);
                    if (!file.isDirectory()) {
                        writer.write("blob " + createHash(file) + " " + file + "\n");
                    } else {
                        writer.write("tree " + getHash(file) + " " + file + "\n");
                    }
                }
                writer.close();
                hash = getHash(temp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File hashedFile = new File("./" + repoName + "/git/objects/" + hash);
        // read from og file and copy contents into new file in objects
        if (!hashedFile.exists()) {
            Path sourceFile = Paths.get(f.getPath());
            Path targetFile = Paths.get(hashedFile.getPath());
            try {
                Files.copy(sourceFile, targetFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return hash;
    }

    // public void createTreeData (File dir) throws IOException {
    // File temp = File.createTempFile("dirData", null);
    // BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
    // for (File subFile : dir.listFiles()) {
    // if (subFile.isDirectory()) {
    // writer.write ("tree " + createHash(subFile) + " " + subFile);
    // }
    // else {
    // writer.write ("blob " + createHash(subFile) + " " + subFile);
    // }
    // }
    // }

    public void compressed(File file) {
        try {
            File compressFile = new File(repoName + "/git/objects/" + createHash(file));
            // above is the file with the name of the hash with all the contents from og
            // file
            FileOutputStream fileOut = new FileOutputStream(compressFile);
            ZipOutputStream zipOut = new ZipOutputStream(fileOut);
            zipOut.putNextEntry(new ZipEntry(compressFile.getName())); // if you want to unzip the directory structure
                                                                       // change to .getPath()
            // code above makes it so that we getting all the zip stuff ready to write into
            // compress file
            Path pathToFile = Paths.get(file.getPath());
            byte[] allBytes = Files.readAllBytes(pathToFile);
            // creates a byte array of file, which we will use to write into zipOut

            zipOut.write(allBytes);
            zipOut.close();
            fileOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void deleteEverything(File file) {
        if (file.listFiles() != null) {
            for (File childFile : file.listFiles()) {
                if (childFile.isDirectory()) {
                    deleteEverything(childFile);
                }
                childFile.delete();
            }
            file.delete();
        }
    }
}