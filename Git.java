import java.io.File;
import java.io.IOException;
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

    public static void checkAndDeleteRepo(){
        File gitDirFile = new File("./git/");
        File objectDirFile = new File("./git/objects/");
        File indexFile = new File("./git/index");
        if(gitDirFile.exists()){
            System.out.println("the git directory exists. path is ./git/");
        }
        if(objectDirFile.exists()){
            System.out.println("the objects directory exists. path is ./git/objects/");
        }
        if(indexFile.exists()){
            System.out.println("the index file exists. path is ./git/index");
        }
        indexFile.delete();
        objectDirFile.delete();
        gitDirFile.delete();
    }

    public static void createBlob(){
        
    }
}