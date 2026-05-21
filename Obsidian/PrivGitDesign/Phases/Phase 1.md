For this phase, we focused on creating a minimal but functional Git-hosting foundation by implementing support for core Git transport protocols and enabling seamless integration with standard Git tooling.

Providing this support requires connection via HTTP/SSH:
### Supporting SSH using Apache MINA SSHD
To begin providing SSH connection, we created a @Configuration class file to deploy a SSH server on startup, and handle its cleanup on shutdown. This was the easy part.

In this step of development, SSH authentication consumed most of our time. Our team was generally familiar with the concepts around public and private keys from previous experiences, but parsing through the errors and understanding what needed tweaks took some extra time.

Initially, we came across issues with our encryption type. Our first tests involved using Ed25519 keys, which we discovered is not natively supported by our current infrastructure. We had planned to full build authentication with our next phase, so we proceeded only using RSA keys.
*<u>In the next phase, we plan to add support for Ed25519 with the Bouncy Castle library.</u>*

### Supporting HTTP 



### Other Details