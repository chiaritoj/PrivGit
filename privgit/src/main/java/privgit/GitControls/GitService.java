package privgit.GitControls;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UploadPack;
import org.springframework.stereotype.Service;
import org.eclipse.jgit.transport.ReceivePack;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

@Service
public class GitService {

    public void uploadPack(Repository repo, InputStream in, OutputStream out, OutputStream err) throws Exception {
        UploadPack uploadPack = new UploadPack(repo);
        uploadPack.upload(in, out, err);
    }

    public void receivePack(Repository repo, InputStream in, OutputStream out, OutputStream err) throws Exception {
        ReceivePack receivePack = new ReceivePack(repo);
        receivePack.receive(in, out, err);
    }

    public Repository openRepo(String path) throws Exception {
        File gitDir = new File(path);

        // Throw if not existing.
        if (!gitDir.exists())
        throw new IllegalArgumentException("Repository path does not exist: " + path);

        // Return repository.
        return new FileRepositoryBuilder()
                .setGitDir(gitDir)
                .readEnvironment()
                .build();
    }
}