package privgit.GitControls;

public class GitRequestParser {

    public static GitRequest parse(String command) {

        // Handle empty cases,
        if (command == null || command.isBlank())
        throw new IllegalArgumentException("Empty SSH command");
        
        // Split command on space, for operation and destination repository.
        String[] parts = command.split(" ", 2);

        // Trim trailing whitespace from the operation,
        String op = parts[0].trim();
        
        // If the repo destination is 2 characters or longer, we try trimming quotes and whitespace.
        String repo = parts.length > 1 ? parts[1].replace("'", "").trim() : null;

        // Parse operation into an enum,
        GitOperation operation = switch (op) {
            case "git-upload-pack" -> GitOperation.UPLOAD_PACK;
            case "git-receive-pack" -> GitOperation.RECEIVE_PACK;
            case "git-upload-archive" -> GitOperation.UPLOAD_ARCHIVE;
            default -> throw new IllegalArgumentException("Unknown git command: " + op);
        };

        // Toss the request object back to the factory.
        return new GitRequest(operation, repo);
    }

}
