package privgit.Controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ssh-keys")
public class SSHKeyController{

    @PostMapping("")
    public Integer postSHHKey(){
        return null;
    }

    @DeleteMapping("{id}")
    public boolean DeleteSSHKey(){
        return false;
    }



}