import java.io.*;
import java.io.File.*;
import java.nio.charset.StandardCharsets;
public class Git {
    public static void main (String [] args) {
        
    }

    public static void initializeRepo(){
       File gitDirFile = new File("./git/");
        if(!gitDirFile.exists()){
            gitDirFile.mkdir();
            
            File objectDirFile = new File("./git/objects/");
            if(!objectDirFile.exists()){
                objectDirFile.mkdir();
            }

            File indexFile = new File("./git/index");
            if(!indexFile.exists()){
                try {
                    indexFile.createNewFile();
                } catch (IOException e){
                    System.out.println("could not create file");
                }
            }
        } else {
            System.out.println("Git Repository already exists");
        }

    }
}