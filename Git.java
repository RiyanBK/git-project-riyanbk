import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
//im j tseting
public class Git {
    public static void main (String [] args) {
        
    }

    public void initializeRepo(String repoName){
        File gitFile = new File(repoName);
        gitFile.mkdir();
    }
}