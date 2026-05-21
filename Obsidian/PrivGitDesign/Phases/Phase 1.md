For this phase, we focused on creating a minimal but functional Git-hosting foundation by implementing support for core Git transport protocols and enabling seamless integration with standard Git tooling.

Providing this support requires connection via HTTP/SSH:
### Supporting SSH using Apache MINA SSHD
To begin providing SSH connection, we created a @Configuration class file to deploy a SSH server on startup, and handle its cleanup on shutdown. This was the easy part.

In this step of development, SSH authentication consumed most of our time. Our team was generally familiar with the concepts around public and private keys from previous experiences, but parsing through the errors and understanding what needed tweaks took some extra time.

Initially, we came across issues with our encryption type. Our first tests involved using Ed25519 keys, which we discovered is not natively supported by our current infrastructure. We had planned to full build authentication with our next phase, so we proceeded only using RSA keys.
*<u>In the next phase, we plan to add support for Ed25519 with the Bouncy Castle library.</u>*

In terms of how our architecture supports the commands explicitly, this is the high-level flow:
1. The [[SSHServerConfig]] class configures the SSH server to the specified port, enabling only public key authentication. The server then sets an [[GitPubKeyAuthenticator|authenticator]] to parse the provided public key, and a [[GitCommandFactory|command factory]] to begin handling user commands.
2. After receiving the command in the factory, the command is ran through a [[GitRequestParser|parser]] and passed along to a new [[GitCommandHandler|handler object]].
3. The handler then manages the input and output streams provided by the SSH connection, passing the command execution into a [[GitTaskExecutor|TaskExecutor]].
4. The executor then processes the command asynchronously in a thread pool to prevent blocking the SSH connection, calling the [[GitService]] class.
5. The [[GitService]] class then utilizes the JGit library to finish the Git operations stored bare in the system.
### Supporting HTTP 



### Other Details