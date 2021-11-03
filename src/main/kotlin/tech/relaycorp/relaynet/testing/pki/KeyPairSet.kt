package tech.relaycorp.relaynet.testing.pki

import java.security.KeyPair
import tech.relaycorp.relaynet.wrappers.generateRSAKeyPair

/**
 * Collection of RSA key pairs for Relaynet nodes in a hypothetical certification path.
 */
public object KeyPairSet {
    public val PUBLIC_GW: KeyPair by lazy { generateRSAKeyPair() }
    public val PRIVATE_GW: KeyPair by lazy { generateRSAKeyPair() }
    public val PRIVATE_ENDPOINT: KeyPair by lazy { generateRSAKeyPair() }
    public val PDA_GRANTEE: KeyPair by lazy { generateRSAKeyPair() }
}
