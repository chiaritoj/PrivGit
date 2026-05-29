package privgit.HTTP;

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

import privgit.GitControls.GitService;
import privgit.Persistence.RepoPersistence;


@Configuration
public class GitHttpConfig {
    private final RepoPersistence repoPersistence;
    private final GitService gitService;

    public GitHttpConfig(RepoPersistence repoPersistence, GitService gitService) {
        this.repoPersistence = repoPersistence;
        this.gitService = gitService;
    }

    @Bean
    public ServletRegistrationBean<GitServlet> gitServlet() {
        GitServlet servlet = new GitServlet();

        Integer userId = 1; // TODO : Determine a userid from this connection.

        servlet.setRepositoryResolver((req, name) -> {
            try {return gitService.openRepository(name);}

            catch (Exception e) {throw new RepositoryNotFoundException(name);}
        });

        servlet.setReceivePackFactory((req, repo) -> {
            return gitService.createReceivePack(userId, repo);
        });

        servlet.setUploadPackFactory((req, repo) ->
            gitService.createUploadPack(userId, repo)
        );

        return new ServletRegistrationBean<>(servlet, "/git/*");
    }
    
}
