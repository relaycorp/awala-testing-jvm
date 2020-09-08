package tech.relaycorp.relaynet.testing

import tech.relaycorp.relaynet.wrappers.generateRSAKeyPair
import java.security.KeyPair

public object KeyPairSet {
    public val PUBLIC_GW: KeyPair by lazy { generateRSAKeyPair() }
    public val PRIVATE_GW: KeyPair by lazy { generateRSAKeyPair() }
    public val PRIVATE_ENDPOINT: KeyPair by lazy { generateRSAKeyPair() }
    public val PDA_GRANTEE: KeyPair by lazy { generateRSAKeyPair() }
}
