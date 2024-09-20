import java.io.File;
import java.io.IOException;
public class Git {
    public static void main (String [] args) {
        
    }

    public static void initializeRepo(String repoName){
       File gitDirFile = new File("./" + repoName + "/git/");
        if(!gitDirFile.exists()){
            gitDirFile.mkdirs();

            File objectDirFile = new File("./"+ repoName +"/git/objects/");
            if(!objectDirFile.exists()){
                objectDirFile.mkdir();
            }

            File indexFile = new File("./" + repoName + "/git/index");
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

    public static void checkAndDeleteRepo(String repoName){
        File gitDirFile = new File("./" + repoName + "/git/");
        File objectDirFile = new File("./"+ repoName +"/git/objects/");
        File indexFile = new File("./" + repoName + "/git/index");
        File repoDir = new File("./" + repoName + "/");
        if(gitDirFile.exists()){
            System.out.println("the git directory exists. path is ./" + repoName + "/git/");
        }
        if(objectDirFile.exists()){
            System.out.println("the objects directory exists. path is ./"+ repoName + "/git/objects/");
        }
        if(indexFile.exists()){
            System.out.println("the index file exists. path is ./" + repoName + "/git/index");
        }
        if(repoDir.exists()){
            System.out.println("the repo direc exists. path is ./" + repoName + "/");
        }
        indexFile.delete();
        objectDirFile.delete();
        gitDirFile.delete();
        repoDir.delete();
    }

    public static void createBlob(){

    }
}