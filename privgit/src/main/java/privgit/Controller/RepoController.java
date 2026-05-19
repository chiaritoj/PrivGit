package privgit.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import privgit.GitControls.GitService;
import privgit.Persistence.RepoPersistence;

/**
 * RepoController
 */
@RestController
@RequestMapping("/repos")
public class RepoController {
    
    private final GitService gitService;
    private final RepoPersistence repoPersistence;

     public RepoController(GitService gitService, RepoPersistence repoPersitence) {
        this.gitService = gitService;
        this.repoPersistence = repoPersitence;
    }

    @PostMapping("/create/{name}")
    public ResponseEntity<String> PostRepo(@PathVariable String name){
        repoPersistence.repo_create(name);
        return ResponseEntity.ok("Created Repo: " + name);
    }

    @GetMapping("/{name}")
    public ResponseEntity<String> GetRepo(@PathVariable String name) throws Exception {
        gitService.openRepo(name);
        return ResponseEntity.ok("Repo exists: " + name);
    }

    @DeleteMapping("/{id}/Delete")
    public static ResponseEntity<Void> DeleteRepo(){
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @GetMapping("/{id}/commits")
     public static ResponseEntity<Void> GetCommits(){
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @GetMapping("/{id}/branches")
     public static ResponseEntity<Void> GetBranches(){
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
    @GetMapping("/{id}/tree")
     public static ResponseEntity<Void> GetTree(){
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
