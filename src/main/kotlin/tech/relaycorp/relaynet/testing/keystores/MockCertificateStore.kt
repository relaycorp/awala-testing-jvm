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
        subjectPrivateAddress: String,
        leafCertificateExpiryDate: ZonedDateTime,
        certificationPathData: ByteArray,
        issuerPrivateAddress: String
    ) {
        if (savingException != null) {
            throw KeyStoreBackendException("Saving certificates isn't supported", savingException)
        }
        setCertificate(
            subjectPrivateAddress,
            leafCertificateExpiryDate,
            certificationPathData,
            issuerPrivateAddress
        )
    }

    /**
     * Set a certificate, bypassing all the usual validation.
     */
    public fun setCertificate(
        subjectPrivateAddress: String,
        leafCertificateExpiryDate: ZonedDateTime,
        certificationPathData: ByteArray,
        issuerPrivateAddress: String
    ) {
        certificationPaths[subjectPrivateAddress to issuerPrivateAddress] =
            (certificationPaths[subjectPrivateAddress to issuerPrivateAddress].orEmpty()) +
            Pair(leafCertificateExpiryDate, certificationPathData)
    }

    override suspend fun retrieveData(
        subjectPrivateAddress: String,
        issuerPrivateAddress: String
    ): List<ByteArray> {
        if (retrievalException != null) {
            throw KeyStoreBackendException(
                "Retrieving certificates isn't supported",
                retrievalException,
            )
        }

        return certificationPaths[subjectPrivateAddress to issuerPrivateAddress]
            ?.map { it.second }.orEmpty()
    }

    override fun delete(subjectPrivateAddress: String, issuerPrivateAddress: String) {
        if (deleteException != null) {
            throw KeyStoreBackendException("Deleting certificates isn't supported", deleteException)
        }

        certificationPaths.remove(subjectPrivateAddress to issuerPrivateAddress)
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
