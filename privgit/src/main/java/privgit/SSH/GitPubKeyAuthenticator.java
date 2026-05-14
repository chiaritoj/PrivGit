package privgit.SSH;

import java.security.PublicKey;

import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.common.config.keys.KeyUtils;

public class GitPubKeyAuthenticator implements PublickeyAuthenticator {

    @Override
    public boolean authenticate(String username, PublicKey key, ServerSession session) {
        String fingerprint = KeyUtils.getFingerPrint(key);

        System.out.println("AUTH USER: " + username);
        System.out.println("KEY TYPE: " + key.getAlgorithm());

        // For now: allow everything
        return true;
    }
}