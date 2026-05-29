package privgit.GitControls;

import java.io.OutputStream;
import java.io.InputStream;

import org.apache.sshd.server.channel.ChannelSession;
import org.springframework.core.task.TaskExecutor;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.Environment;
import org.eclipse.jgit.lib.Repository;


public class GitCommandHandler implements Command {

    private final Integer userId;
    private final GitRequest request;
    private final GitService gitService;
    private final TaskExecutor gitExecutor;

    private InputStream in;
    private OutputStream out;
    private OutputStream err;
    private ExitCallback callback;

    public GitCommandHandler(GitRequest request, GitService gitService, TaskExecutor gitExecutor, Integer userId) {
        this.userId = userId;
        this.request = request;
        this.gitService = gitService;
        this.gitExecutor = gitExecutor;
    }

    @Override
    public void start(ChannelSession channel, Environment env) {
        // Run the command in a separate thread to not block the SSH connection.
        gitExecutor.execute(GitCommandHandler.this::executeGitCommand);
    }

    private void executeGitCommand() {
        try (Repository repo = gitService.openRepository(request.repository());) {
            // Determine which operation, (if supported)
            switch (request.operation()) {
                case UPLOAD_PACK -> gitService.uploadPack(userId, repo, in, out, err);
                case RECEIVE_PACK -> gitService.receivePack(userId, repo, in, out, err);
                default -> throw new IllegalStateException("Unsupported operation");
            }

            // Flush output,
            out.flush();

            // Exit with success,
            callback.onExit(0);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onExit(1, e.getMessage());
        }
    }

    @Override public void destroy(ChannelSession channel) {}

    @Override public void setInputStream(InputStream in) { this.in = in; }

    @Override public void setOutputStream(OutputStream out) { this.out = out; }

    @Override public void setErrorStream(OutputStream err) { this.err = err; }

    @Override public void setExitCallback(ExitCallback callback) { this.callback = callback; }
}