import java.security.*;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

//git reset --hard HEAD

public class Git {
    // toggle for compression
    public boolean compression = false;
    // user sets this when Git is initialized in tester
    public String repoName;

    // I removed the need for repo name, so come up with whatever you want
    // if it doesn't work on your laptop, you can try "./*repo name*/git" or smth. I
    // use windows - Kyara Zhou
    public Git(String repoName) {
        this.repoName = repoName;
    }

    public void initializeRepo() {
        File gitDirFile = new File("./git/");

        if (!gitDirFile.exists()) {
            gitDirFile.mkdir();
        }
        File indexFile = new File("./git/index");
        File objectDirFile = new File("./git/objects/");
        File headFile = new File("./git/HEAD/");

        if (gitDirFile.exists() && objectDirFile.exists() && indexFile.exists() && headFile.exists()) {
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
            if (!headFile.exists()) {
                try {
                    headFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // the below method checks if the repo is setup correctly before any file are
    // added in
    public void checkAndDeleteRepo() {
        File gitDirFile = new File("./git/");
        File objectDirFile = new File("./git/objects/");
        File indexFile = new File("./git/index");
        File repoDir = new File("./");
        if (gitDirFile.exists()) {
            System.out.println("the git directory exists. path is ./git/");
        }
        if (objectDirFile.exists()) {
            System.out.println("the objects directory exists. path is ./git/objects/");
        }
        if (indexFile.exists()) {
            System.out.println("the index file exists. path is ./git/index");
        }
        if (repoDir.exists()) {
            System.out.println("the repo direc exists. path is ./");
        }
        System.out.println("\n" + "initializing a repo works" + "\n");
        deleteEverything(gitDirFile);
    }

    public void createBlob(File ogFile) {
        try {
            String ogFileName = ogFile.toString();
            if (!ogFile.exists()) {
                throw new FileNotFoundException();
            }
            if (compression) {
                compressed(ogFile);
            }
            boolean isDir = ogFile.isDirectory();
            String hash = getHash(ogFile);

            // code below checks index and if file there, doesnt write; if there, writes
            boolean existsInIndex = false;
            // Path pathToIndex = Paths.get("./" + repoName + "/git/index");
            Path pathToIndex = Paths.get("./git/index");

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
        // File hashedFile = new File("./" + repoName + "/git/objects/" + hash);
        File hashedFile = new File("./git/objects/" + hash);
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

    public void commit(String user, String content) throws IOException {
        StringBuilder sb = new StringBuilder();
        String treeHash = storeToTree();
        sb.append("tree: " + treeHash + "\n");
        File headFile = new File("./git/HEAD");
        BufferedReader bf = new BufferedReader(new FileReader(headFile));
        if (bf.ready()) {
            sb.append("parent: " + bf.readLine() + "\n");
        } else {
            // first commit
        }

        // don't forget to wipe the index file clean

        // updates head file; should be the last step of the code
        PrintWriter pw = new PrintWriter(headFile);
        pw.print("");// appends the hash of the commit
        pw.close();

        bf.close();
    }

    // simply adds indexFile into the tree file

    // fix: a new tree file is created every time; contains previous tree contents;
    // store in objects folder
    public String storeToTree() throws IOException {
        // stores everthing to tree file
        StringBuilder sb = new StringBuilder();
        /**
         * steps for getting the prev file
         * 1. read file to see if head exist
         * if file does exist, then proceed. Else, skip loop
         * 2. read the 2nd line of file to find the previous file
         */

        // checks if head exist; if not exist, then it means its the first commit

        // establishes the base logic for commiting if has previous file, check to see if works
        File headFile = new File("./git/HEAD");
        BufferedReader bf = new BufferedReader(new FileReader(headFile));
        if (bf.ready()) {
            File prevCommit = new File("./git/objects" + bf.readLine()); // check this part
            bf = new BufferedReader(new FileReader(prevCommit));
            StringBuilder sbTemp = new StringBuilder();
            for (int i = 0; i < 46; i++) {
                if (i > 6) {
                    sbTemp.append(bf.read());
                } else {
                    bf.read();
                }
            }
            prevCommit = new File ("./git/objects" + sbTemp.toString());
            BufferedReader br = new BufferedReader(new FileReader(prevCommit));
            while (br.ready()) {
                sb.append(br.readLine() + "\n");
            }


        }

        // find the hash of the previous tree
        // BufferedReader br = new BufferedReader ();
        // while (br.ready()) {
        // sb.append(br.readLine() + "\n");
        // }

        BufferedReader br = new BufferedReader(new FileReader("./git/index"));
        while (br.ready()) {
            sb.append(br.readLine() + "\n");
        }

        // creates a temp file so we can make a blob for it
        File tempFile = new File("./tempFile");
        PrintWriter pw = new PrintWriter(tempFile);
        pw.print(sb.toString());
        pw.close();

        // creates the treeFile here
        String treeHash = getHash(tempFile);
        File tree = new File(".git/objects/" + treeHash);
        pw = new PrintWriter(tree);
        pw.print(sb.toString());
        pw.close();
        br.close();
        tempFile.delete();

        // stores to tree here
        return treeHash;
    }
}