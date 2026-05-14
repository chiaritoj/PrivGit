package privgit.SSH;

import org.apache.sshd.server.command.CommandFactory;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.Environment;

import java.io.InputStream;
import java.io.OutputStream;


public class GitCommandFactory implements CommandFactory {

    @Override
    public Command createCommand(ChannelSession session,String command) {

        System.out.println("INTERCEPTED SSH COMMAND: " + command);

        return new Command() {

            @Override
            public void setInputStream(InputStream in) {}

            @Override
            public void setOutputStream(OutputStream out) {}

            @Override
            public void setErrorStream(OutputStream err) {}

            @Override
            public void setExitCallback(org.apache.sshd.server.ExitCallback callback) {}

            @Override
            public void start(ChannelSession channel, Environment env) {
                System.out.println("START: " + command);
            }

            @Override
            public void destroy(ChannelSession channel) {
                System.out.println("END: " + command);
            }
        };
    }
}
