package privgit.GitControls;

public record GitRequest(
    GitOperation operation,
    String repository
) {}