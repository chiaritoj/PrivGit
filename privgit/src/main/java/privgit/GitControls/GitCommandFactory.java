package privgit.GitControls;

import org.apache.sshd.server.command.CommandFactory;
import org.apache.sshd.server.channel.ChannelSession;
import org.springframework.core.task.TaskExecutor;
import org.apache.sshd.server.command.Command;


public class GitCommandFactory implements CommandFactory {

    private final Integer userId;
    private final GitService gitService;
    private final TaskExecutor gitExecutor;

    public GitCommandFactory(GitService gitService, TaskExecutor gitExecutor, Integer userId) {
        this.gitService = gitService;
        this.gitExecutor = gitExecutor;
        this.userId = userId;
    }

    @Override
    public Command createCommand(ChannelSession session,String command) {
        GitRequest request = GitRequestParser.parse(command); // Parse command

        return new GitCommandHandler(request, gitService, gitExecutor, userId); // Pass it into a handler.
    }
}
