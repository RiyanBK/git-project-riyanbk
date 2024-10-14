import java.security.*;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

//git reset --hard HEAD

public class Git implements GitInterface {
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

    private void createBlob(File ogFile) {
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

    public void stage(String filePath) {
        File f = new File(filePath);
        createBlob(f);
    }

    public void checkout(String commitHash) {
        ArrayList<String> files = new ArrayList();
        files.add(commitHash);
        File headFile = new File("./git/HEAD");
        PrintWriter pw = new PrintWriter(headFile);
        pw.print(commitHash);// appends the hash of the commit
        pw.close();

        BufferedReader bf = new BufferedReader(new FileReader(headFile));
        if (bf.ready()) {
            File prevCommit = new File("./git/objects/" + bf.readLine());
            bf = new BufferedReader(new FileReader(prevCommit));
            StringBuilder sbTemp = new StringBuilder();
            for (int i = 0; i < 46; i++) {
                if (i >= 6) {
                    sbTemp.append((char) bf.read());
                } else {
                    bf.read();
                }
            }
        }

        File currentTree = new File("./git/objects/" + sbTemp.toString());
        files.add(sbTemp.toString());
        BufferedReader br = new BufferedReader(new FileReader(currentTree));
        while (br.ready()) {
            String temp = br.readLine();
            files.add(temp.substring(0, 46));
        }

        // traverse commit history to store all previous hashes of tree and parent
        br.close();

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

    // creates a commit
    public String commit(String author, String message) {
        try {
            StringBuilder sb = new StringBuilder();
            String treeHash = storeToTree();
            // if the file HEAD is not empty, take HEAD content. Else(1st commit) leave it
            // blank
            sb.append("tree: " + treeHash + "\nparent: ");
            File headFile = new File("./git/HEAD");
            BufferedReader bf = new BufferedReader(new FileReader(headFile));
            if (bf.ready()) {
                sb.append(bf.readLine() + "\n");
            } else {
                sb.append("\n");
            }

            // author, committer, and content are all given by user
            sb.append("author: " + author + "\ncommitter: " + author + "\n" + message);

            // creates a temp file to make a blob from it
            File tempFile = new File("./tempFile");
            PrintWriter pw = new PrintWriter(tempFile);
            pw.print(sb.toString());
            pw.close();

            // creates the commit file by using the same method above
            String commitHash = getHash(tempFile);
            File commitFile = new File(".git/objects/" + commitHash);
            pw = new PrintWriter(commitFile);
            pw.print(sb.toString());
            pw.close();

            // updates head file
            pw = new PrintWriter(headFile);
            pw.print(commitHash);// appends the hash of the commit
            bf.close();
            pw.close();
            tempFile.delete();

            // wipes clean the index file
            File indexFile = new File("./git/index");
            pw = new PrintWriter(indexFile);
            pw.print("");
            pw.close();

            return commitHash;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }

    // stores current index file to tree
    public String storeToTree() throws IOException {
        // use head file to find previous commit -> find previous tree file through
        // "tree: [hash code]" -> read its content and add to stringBuilder
        StringBuilder sb = new StringBuilder();
        File headFile = new File("./git/HEAD");
        BufferedReader bf = new BufferedReader(new FileReader(headFile));
        if (bf.ready()) {
            File prevCommit = new File("./git/objects/" + bf.readLine());
            bf = new BufferedReader(new FileReader(prevCommit));
            StringBuilder sbTemp = new StringBuilder();
            for (int i = 0; i < 46; i++) {
                if (i >= 6) {
                    sbTemp.append((char) bf.read());
                } else {
                    bf.read();
                }
            }

            prevCommit = new File("./git/objects/" + sbTemp.toString());
            BufferedReader br = new BufferedReader(new FileReader(prevCommit));
            while (br.ready()) {
                sb.append(br.readLine() + "\n");
            }
            br.close();
        }

        // reads in new content from index file
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