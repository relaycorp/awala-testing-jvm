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
    public val certificationPaths: MutableMap<String, List<Pair<ZonedDateTime, ByteArray>>> =
        mutableMapOf()

    public fun clear() {
        certificationPaths.clear()
    }

    override suspend fun saveData(
        subjectPrivateAddress: String,
        leafCertificateExpiryDate: ZonedDateTime,
        certificationPathData: ByteArray
    ) {
        if (savingException != null) {
            throw KeyStoreBackendException("Saving certificates isn't supported", savingException)
        }
        setCertificate(subjectPrivateAddress, leafCertificateExpiryDate, certificationPathData)
    }

    /**
     * Set a certificate, bypassing all the usual validation.
     */
    public fun setCertificate(
        subjectPrivateAddress: String,
        leafCertificateExpiryDate: ZonedDateTime,
        certificationPathData: ByteArray
    ) {
        certificationPaths[subjectPrivateAddress] =
            (certificationPaths[subjectPrivateAddress].orEmpty()) +
            Pair(leafCertificateExpiryDate, certificationPathData)
    }

    override suspend fun retrieveData(subjectPrivateAddress: String): List<ByteArray> {
        if (retrievalException != null) {
            throw KeyStoreBackendException(
                "Retrieving identity keys isn't supported",
                retrievalException,
            )
        }

        return certificationPaths[subjectPrivateAddress]?.map { it.second }.orEmpty()
    }

    override fun delete(subjectPrivateAddress: String) {
        if (deleteException != null) {
            throw KeyStoreBackendException("Deleting certificates isn't supported", deleteException)
        }

        certificationPaths.remove(subjectPrivateAddress)
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
