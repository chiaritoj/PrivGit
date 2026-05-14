package privgit.SSH;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.auth.pubkey.UserAuthPublicKeyFactory;
import org.apache.sshd.server.SshServer;

@Configuration
public class SSHServerConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public SshServer sshServer() throws IOException {

        Path hostKeyPath = Path.of("data/ssh/hostkey");
        Files.createDirectories(hostKeyPath.getParent());

        SshServer server = SshServer.setUpDefaultServer();
        server.setPort(2222);

        server.setKeyPairProvider(
            new SimpleGeneratorHostKeyProvider(hostKeyPath)
        );

        server.setUserAuthFactories(
            List.of(new UserAuthPublicKeyFactory())
        );

        server.setPublickeyAuthenticator(new GitPubKeyAuthenticator());

        server.setCommandFactory(new GitCommandFactory());

        return server;
    }
}
