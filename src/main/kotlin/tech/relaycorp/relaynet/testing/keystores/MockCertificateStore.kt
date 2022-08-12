package tech.relaycorp.relaynet.testing.keystores

import java.time.ZonedDateTime
import tech.relaycorp.relaynet.keystores.CertificateStore
import tech.relaycorp.relaynet.keystores.KeyStoreBackendException

@Suppress("MemberVisibilityCanBePrivate")
public class MockCertificateStore(
    private val savingException: Throwable? = null,
    private val retrievalException: Throwable? = null,
    private val deleteException: Throwable? = null,
) : CertificateStore() {
    public val certificationPaths: MutableMap<
        Pair<String, String>,
        List<Pair<ZonedDateTime, ByteArray>>
        > = mutableMapOf()

    public fun clear() {
        certificationPaths.clear()
    }

    override suspend fun saveData(
        subjectId: String,
        leafCertificateExpiryDate: ZonedDateTime,
        certificationPathData: ByteArray,
        issuerId: String
    ) {
        if (savingException != null) {
            throw KeyStoreBackendException("Saving certificates isn't supported", savingException)
        }
        setCertificate(
            subjectId,
            leafCertificateExpiryDate,
            certificationPathData,
            issuerId
        )
    }

    /**
     * Set a certificate, bypassing all the usual validation.
     */
    public fun setCertificate(
        subjectId: String,
        leafCertificateExpiryDate: ZonedDateTime,
        certificationPathData: ByteArray,
        issuerId: String
    ) {
        certificationPaths[subjectId to issuerId] =
            (certificationPaths[subjectId to issuerId].orEmpty()) +
            Pair(leafCertificateExpiryDate, certificationPathData)
    }

    override suspend fun retrieveData(
        subjectId: String,
        issuerId: String
    ): List<ByteArray> {
        if (retrievalException != null) {
            throw KeyStoreBackendException(
                "Retrieving certificates isn't supported",
                retrievalException,
            )
        }

        return certificationPaths[subjectId to issuerId]
            ?.map { it.second }.orEmpty()
    }

    override fun delete(subjectId: String, issuerId: String) {
        if (deleteException != null) {
            throw KeyStoreBackendException("Deleting certificates isn't supported", deleteException)
        }

        certificationPaths.remove(subjectId to issuerId)
    }

    override suspend fun deleteExpired() {
        if (deleteException != null) {
            throw KeyStoreBackendException("Deleting certificates isn't supported", deleteException)
        }

        certificationPaths.forEach { (address, list) ->
            certificationPaths[address] = list.filter { it.first >= ZonedDateTime.now() }
        }
    }
}
