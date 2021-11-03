package tech.relaycorp.relaynet.testing.keystores

import tech.relaycorp.relaynet.keystores.SessionPublicKeyData
import tech.relaycorp.relaynet.keystores.SessionPublicKeyStore

public class MockSessionPublicKeyStore(
    private val savingException: Throwable? = null,
    private val retrievalException: Throwable? = null,
) : SessionPublicKeyStore() {
    @Suppress("MemberVisibilityCanBePrivate")
    public val keys: MutableMap<String, SessionPublicKeyData> = mutableMapOf()

    public fun clear() {
        keys.clear()
    }

    override suspend fun saveKeyData(keyData: SessionPublicKeyData, peerPrivateAddress: String) {
        if (savingException != null) {
            throw savingException
        }
        this.keys[peerPrivateAddress] = keyData
    }

    override suspend fun retrieveKeyData(peerPrivateAddress: String): SessionPublicKeyData? {
        if (retrievalException != null) {
            throw retrievalException
        }

        return keys[peerPrivateAddress]
    }
}
