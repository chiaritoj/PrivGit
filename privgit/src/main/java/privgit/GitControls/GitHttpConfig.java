package privgit.GitControls;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.http.server.GitServlet;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.ReceivePack;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import privgit.Persistence.RepoPersistence;


@Configuration
public class GitHttpConfig {
    private final RepoPersistence repoPersistence;

    public GitHttpConfig(RepoPersistence repoPersistence){
        this.repoPersistence = repoPersistence;
    }

    @Bean
    public ServletRegistrationBean<GitServlet> gitServlet() {
        GitServlet servlet = new GitServlet();
        servlet.setRepositoryResolver((req, name) -> {
            try {
                File repo = Path.of(
                     "data", "repos", name).toFile();

                if (!repo.exists()) {
                    throw new RepositoryNotFoundException(name);
                }

                return new FileRepositoryBuilder()
                    .setGitDir(repo)
                    .build();
                
            } catch (IOException e) {
                throw new RuntimeException("Failed to open repository: " + name, e);
            }
        });

        servlet.setReceivePackFactory((req, repo) -> {
            ReceivePack receivePack = new ReceivePack(repo);
            receivePack.setAllowCreates(true);
            receivePack.setAllowDeletes(true);
            receivePack.setAllowNonFastForwards(true);
            return receivePack;
        });
        return new ServletRegistrationBean<>(servlet, "/git/*");
    }
    
}
