package tech.relaycorp.relaynet.testing.keystores

import tech.relaycorp.relaynet.keystores.SessionPublicKeyData
import tech.relaycorp.relaynet.keystores.SessionPublicKeyStore

public class MockSessionPublicKeyStore(
    private val savingException: Throwable? = null,
    private val retrievalException: Throwable? = null,
) : SessionPublicKeyStore() {
    @Suppress("MemberVisibilityCanBePrivate")
    public val keys: MutableMap<String, MutableMap<String, SessionPublicKeyData>> = mutableMapOf()

    public fun clear() {
        keys.clear()
    }

    override suspend fun saveKeyData(
        keyData: SessionPublicKeyData,
        nodeId: String,
        peerId: String
    ) {
        if (savingException != null) {
            throw savingException
        }
        this.keys.putIfAbsent(nodeId, mutableMapOf())
        this.keys[nodeId]!![peerId] = keyData
    }

    override suspend fun retrieveKeyData(nodeId: String, peerId: String): SessionPublicKeyData? {
        if (retrievalException != null) {
            throw retrievalException
        }

        return keys[nodeId]?.get(peerId)
    }

    override suspend fun delete(nodeId: String, peerId: String) {
        keys[nodeId]?.remove(peerId)
    }
}
