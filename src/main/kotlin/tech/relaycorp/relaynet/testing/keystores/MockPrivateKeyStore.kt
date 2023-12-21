package tech.relaycorp.relaynet.testing.keystores

import tech.relaycorp.relaynet.keystores.KeyStoreBackendException
import tech.relaycorp.relaynet.keystores.PrivateKeyData
import tech.relaycorp.relaynet.keystores.PrivateKeyStore

@Suppress("MemberVisibilityCanBePrivate")
public class MockPrivateKeyStore(
    private val savingException: Throwable? = null,
    private val retrievalException: Throwable? = null,
) : PrivateKeyStore() {
    public val identityKeys: MutableMap<String, PrivateKeyData> = mutableMapOf()

    public val sessionKeys: MutableMap<String, MutableMap<String, MutableMap<String, ByteArray>>> =
        mutableMapOf()

    public fun clear() {
        identityKeys.clear()
        sessionKeys.clear()
    }

    override suspend fun saveIdentityKeyData(nodeId: String, keyData: PrivateKeyData) {
        if (savingException != null) {
            throw KeyStoreBackendException("Saving identity keys isn't supported", savingException)
        }
        setIdentityKey(nodeId, keyData)
    }

    /**
     * Set an identity key, bypassing all the usual validation.
     */
    public fun setIdentityKey(privateAddress: String, keyData: PrivateKeyData) {
        identityKeys[privateAddress] = keyData
    }

    override suspend fun retrieveIdentityKeyData(nodeId: String): PrivateKeyData? {
        if (retrievalException != null) {
            throw KeyStoreBackendException(
                "Retrieving identity keys isn't supported",
                retrievalException,
            )
        }

        return identityKeys[nodeId]
    }

    override suspend fun retrieveAllIdentityKeyData(): List<PrivateKeyData> =
        identityKeys.values.toList()

    override suspend fun saveSessionKeySerialized(
        keyId: String,
        keySerialized: ByteArray,
        nodeId: String,
        peerId: String?
    ) {
        if (savingException != null) {
            throw KeyStoreBackendException("Saving session keys isn't supported", savingException)
        }
        setSessionKey(nodeId, peerId, keyId, keySerialized)
    }

    /**
     * Set a session key, bypassing all the usual validation.
     */
    public fun setSessionKey(
        privateAddress: String,
        peerId: String?,
        keyId: String,
        keySerialized: ByteArray
    ) {
        sessionKeys.putIfAbsent(privateAddress, mutableMapOf())
        val peerKey = peerId ?: "unbound"
        sessionKeys[privateAddress]!!.putIfAbsent(peerKey, mutableMapOf())
        sessionKeys[privateAddress]!![peerKey]!![keyId] = keySerialized
    }

    override suspend fun retrieveSessionKeySerialized(
        keyId: String,
        nodeId: String,
        peerId: String,
    ): ByteArray? {
        if (retrievalException != null) {
            throw KeyStoreBackendException(
                "Retrieving session keys isn't supported",
                savingException
            )
        }

        return sessionKeys[nodeId]?.get(peerId)?.get(keyId)
            ?: sessionKeys[nodeId]?.get("unbound")?.get(keyId)
    }

    override suspend fun deleteKeys(nodeId: String) {
        identityKeys.remove(nodeId)
        sessionKeys.remove(nodeId)
    }

    override suspend fun deleteBoundSessionKeys(nodeId: String, peerId: String) {
        sessionKeys[nodeId]?.remove(peerId)
    }
}
