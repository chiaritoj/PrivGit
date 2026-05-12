package privgit.Persistence;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

@Service
public class RepoPersistence {
    
    private final File repo_dir = new File("RepoStorage");

    /**
     * Creates a Bare Repo that stores Git History
     * @param reponame Name for the Bare Repo
     * @return Directory of that repo
     */
    public File repo_create(String reponame){
        File repoDir = new File(repo_dir, reponame + ".git");

        if(repoDir.exists()){
            throw new IllegalArgumentException("Repo Already Exist");
        }
        try{
            Git.init()
            .setDirectory(repoDir)
            .setBare(true)
            .call()
            .close();
        }catch(GitAPIException e){
            e.printStackTrace();
            System.out.println("Error in Repo_Create");
        }
    
        return repoDir;
    }

    public boolean repo_exist(){
        return false;
    }

    public String repo_get(){
        return null;
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
