package privgit.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RepoController
 */
@RestController
@RequestMapping("/api/repos")
public class RepoController {
    
    // RepoPersistence RepoProcess;

    @PostMapping("")
    public static ResponseEntity<Void> PostRepo(){

        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @GetMapping("/{id}")
    public static ResponseEntity<Void> GetRepo(){
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
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
