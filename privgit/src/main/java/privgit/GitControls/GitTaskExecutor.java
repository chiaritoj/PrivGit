package privgit.GitControls;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;


@Configuration
public class GitTaskExecutor {
    // Sets up configuration for Task executor pool to prevent overload.
    @Bean
    public TaskExecutor gitTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(32);
        executor.setQueueCapacity(100);

        // Keep thread named after git for identification.
        executor.setThreadNamePrefix("git-");

        executor.initialize();

        return executor;
    }
}