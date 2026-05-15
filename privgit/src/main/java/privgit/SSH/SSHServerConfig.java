package privgit.SSH;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.context.annotation.Configuration;

import privgit.GitControls.GitCommandFactory;
import privgit.GitControls.GitService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.auth.pubkey.UserAuthPublicKeyFactory;
import org.apache.sshd.server.SshServer;

@Configuration
public class SSHServerConfig {

    @Value("${privgit.ssh.port}")
    private int sshPort;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public SshServer sshServer(GitService gitService) throws IOException {

        Path hostKeyPath = Path.of("data/ssh/hostkey");
        Files.createDirectories(hostKeyPath.getParent());

        SshServer server = SshServer.setUpDefaultServer();
        server.setPort(sshPort);

        server.setKeyPairProvider(
            new SimpleGeneratorHostKeyProvider(hostKeyPath)
        );

        server.setUserAuthFactories(
            List.of(new UserAuthPublicKeyFactory())
        );

        server.setPublickeyAuthenticator(new GitPubKeyAuthenticator());

        server.setCommandFactory(new GitCommandFactory(gitService));

        return server;
    }
}
