package privgit.GitControls;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.transport.UploadPack;
import org.springframework.stereotype.Service;

@Service
public class GitService {

    // Methods used for SSH primarily
    public void uploadPack(Integer userId, Repository repo, InputStream in, OutputStream out, OutputStream err) throws Exception {
        UploadPack uploadPack = createUploadPack(userId, repo);
        uploadPack.upload(in, out, err);
    }

    public void receivePack(Integer userId, Repository repo, InputStream in, OutputStream out, OutputStream err) throws Exception {
        ReceivePack receivePack = createReceivePack(userId, repo);
        receivePack.receive(in, out, err);
    }

    // Direct constructors for use by both, since HTTP requires the pack itself.
    public UploadPack createUploadPack(Integer userId, Repository repo) {
        UploadPack uploadPack = new UploadPack(repo);
        // TODO : Handle configuring repository settings at this step, to prevent unauthorized use.
        return uploadPack;
    }
    
    public ReceivePack createReceivePack(Integer userId, Repository repo) {
        ReceivePack receivePack = new ReceivePack(repo);
        // TODO : Handle configuring repository settings at this step, to prevent unauthorized use.
        receivePack.setAllowCreates(true);
        receivePack.setAllowDeletes(false);
        receivePack.setAllowNonFastForwards(false);

        return receivePack;
    }

    // Public method to resolve and open a repository by name,
    public Repository openRepository(Integer userId, String name) throws Exception {
        return openRepo(userId, resolveRepo(name));
    }

    private Repository openRepo(Integer userId, String path) throws Exception {
        File gitDir = new File(path);

        // TODO : Determine the user should have access to this repository at all.

        // Throw if not existing.
        if (!gitDir.exists())
        throw new IllegalArgumentException("Repository path does not exist: " + path);

        // Return repository.
        return new FileRepositoryBuilder()
                .setGitDir(gitDir)
                .readEnvironment()
                .build();
    }

    private String resolveRepo(String repo) {
        // Add ending if needed,
        if (!repo.endsWith(".git")) repo += ".git";
        
        // Construct repo path.
        return Path.of("data", "repos", repo).toAbsolutePath().toString();
    }
}