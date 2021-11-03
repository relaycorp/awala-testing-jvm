package tech.relaycorp.relaynet.testing.keystores

import tech.relaycorp.relaynet.keystores.PrivateKeyData
import tech.relaycorp.relaynet.keystores.PrivateKeyStore

public class MockPrivateKeyStore(
    private val savingException: Throwable? = null,
    private val retrievalException: Throwable? = null,
) : PrivateKeyStore() {
    @Suppress("MemberVisibilityCanBePrivate")
    public val keys: MutableMap<String, PrivateKeyData> = mutableMapOf()

    public fun clear() {
        keys.clear()
    }

    override suspend fun saveKeyData(keyData: PrivateKeyData, keyId: String) {
        if (savingException != null) {
            throw savingException
        }
        keys[keyId] = keyData
    }

    override suspend fun retrieveKeyData(keyId: String): PrivateKeyData? {
        if (retrievalException != null) {
            throw retrievalException
        }

        return keys[keyId]
    }
}
