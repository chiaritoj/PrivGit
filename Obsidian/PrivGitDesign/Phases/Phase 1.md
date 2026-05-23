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

To support HTTP-based Git operations, we created configuration files that manages incoming HTTP requests, route traffic through the appropriate ports and connect those requests into Git services.

Handling HTTP requests was relatively straightforward since our team already had experience working REST APIs and HTTP communication.  Routing those requests through Git services did required additional time and more research, but it was easily manageable once we understand how Git operations were handled internally.

One issue we encountered was that the application was routing traffic through port 8080 instead of port 80. We resolved this by configuring Tomcat services and adding an HTTP connector that allowed the application to accept requests on port 80.

For how our system design work all-together assume we have an HTTP requests
git clone http://localhost/git/Test1.git

1. When we run the app using mvn spring-boot:run, Spring boot starts a local HTTP server on our computer. We route HTTP through port 80 using [[HttpConnectorConfig]].
2. After the HTTP requests is routed to the Spring boot application, [[GitHttpConfig]] allows the requests to be handled as Git Operation such as clone fetch or push by using [[GitService]] class.
3. The [[GitService]] class then uses the JGit library to perform Git operations on the bare repositories stored within the system.

### Other Details

Git have 3 operations: Upload_Pack where we are able to clone/fetch/pull, Receive_Pack where we are able to push and Upload_Archive where we download repo as archive like zip. However, we decided not to use Upload_Archive because our project handles archive downloads by using HTTP endpoints to return a zip file meaning Upload_Archive is meaningless or deprecated. 

For port configuration, we need to use Spring boot profiles to separate local from production. Instead of hardcoding one port, we can run the application with different profiles, such as dev or prod, and each profile can define its own server port and SSH port.

To make this much easier to run we created a PowerShell scripts that starts the application with the right Spring profile however we notice this does not work with Linux systems. In the future, we will create a Bash script so the same workflow works with Linux-based systems.
