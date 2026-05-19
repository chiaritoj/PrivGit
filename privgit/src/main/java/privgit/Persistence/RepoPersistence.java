package privgit.Persistence;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Service;

@Service
public class RepoPersistence {
    
     private final Path repoRoot = Path.of( "data", "repos");

    /**
     * Creates a Bare Repo that stores Git History
     * @param reponame Name for the Bare Repo
     * @return Directory of that repo
     */
    public File repo_create(String reponame){
         Path repoPath = repoRoot.resolve(reponame + ".git");

        if(Files.exists(repoPath)){
            throw new IllegalArgumentException("Repo Already Exist");
        }
        try{

            Files.createDirectories(repoRoot);

            Git.init()
                .setDirectory(repoPath.toFile())
                .setBare(true)
                .call()
                .close();
            
            return repoPath.toFile();
        }catch(Exception e){
            throw new RuntimeException("Failed to create repo for " + reponame, e);
        }
    }

    public boolean repo_exist(){
        return false;
    }

    public File repo_get(String name){
        return repoRoot.resolve(name).toFile();
    }

    public boolean repo_Delete(){
        return false;
    }

    public ArrayList<String> repo_lists(){
        return null;
    }

    public static void main(String[] args) {
        RepoPersistence TestPer = new RepoPersistence();
        File check = TestPer.repo_create("Test");
        System.out.println(check);
    }
}
