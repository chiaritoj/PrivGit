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
To begin support HTTP-based Git operations, we created configuration files that manage incoming HTTP requests, routing traffic through the appropriate ports and connecting those requests into calls for our [[GitService]] class.

Handling HTTP requests was relatively straightforward since our team already had experience working REST APIs and HTTP communication.  Routing those requests through Git services did require additional time and research, but the workload was manageable once we understood how Git operations are handled internally.

An issue we encountered had to do with Tomcat routing traffic through port 8080 by default, instead of ports 80 and 443. We resolved this by configuring Tomcat services with run profiles and adding an additional HTTP connector that allowed the application to accept requests on port 80 when SSL is not available.

For testing the functionality, assume we want to clone a repository from our remote source:
e.g. `git clone http://localhost/git/Test1.git`

1. When the app is initialized, Spring boot starts a local HTTP server on our computer. We route HTTP through port 80 using the [[HttpConnectorConfig]] class.
2. After the HTTP request is received by the application, The [[GitHttpConfig]] class establishes a servlet layer to route Upload and Receive operations through to our [[GitService]] class.
3. The [[GitService]] class then utilizes the JGit library to perform the Git operations on the bare repositories within the system.

### Other Details
Git functionality operates using 2, sometimes 3 operations: 
1. Upload Pack - The operation to "upload" resources back to the client
2. Receive Pack - The operation to "receive" resources from a client
3. Upload Archive - The operation to "upload" a snapshot of the code to the client

Upload Archive is typically disabled in Enterprise applications, as supporting it is CPU-intensive, and its main usage is generally overshadowed by the former operations.

It is mentioned above that we handled port configurations with run profiles; For this phase of this project, we provided two run profiles, `dev` and `prod`. 

The developer run profile hosts SSH connections on port 2222 and API endpoints on port 8080, whereas the production profile provides SSH on port 22 and API endpoints on 443 by default. Testing is typically done with the production profile as general users will be utilizing these true ports when deployed. Since local testing does not have great need for SSL certification, we opened traffic to port 80 as well to allow testing as port 443 will not accept traffic without it. 

To support deployment and testing, we created a PowerShell script that starts the application with a provided run profile. In the future, we plan on creating an additional Bash script to allow easy deployment on Linux-based systems.
