package tech.relaycorp.relaynet.testing.pki

import tech.relaycorp.relaynet.issueDeliveryAuthorization
import tech.relaycorp.relaynet.issueEndpointCertificate
import tech.relaycorp.relaynet.issueGatewayCertificate
import tech.relaycorp.relaynet.wrappers.x509.Certificate

/**
 * Certification path from an Internet gateway to a Parcel Delivery Authorization (PDA).
 *
 * See [KeyPairSet] for the respective key pairs used by these certificates.
 */
@Suppress("MemberVisibilityCanBePrivate")
public object PDACertPath {
    public val INTERNET_GW: Certificate by lazy {
        issueGatewayCertificate(
            KeyPairSet.INTERNET_GW.public,
            KeyPairSet.INTERNET_GW.private,
            CERTIFICATE_END_DATE,
            validityStartDate = CERTIFICATE_START_DATE
        )
    }

    public val PRIVATE_GW: Certificate by lazy {
        issueGatewayCertificate(
            KeyPairSet.PRIVATE_GW.public,
            KeyPairSet.INTERNET_GW.private,
            CERTIFICATE_END_DATE,
            INTERNET_GW,
            validityStartDate = CERTIFICATE_START_DATE
        )
    }

    public val PRIVATE_ENDPOINT: Certificate by lazy {
        issueEndpointCertificate(
            KeyPairSet.PRIVATE_ENDPOINT.public,
            KeyPairSet.PRIVATE_GW.private,
            CERTIFICATE_END_DATE,
            PRIVATE_GW,
            validityStartDate = CERTIFICATE_START_DATE
        )
    }

    @Suppress("unused")
    public val PDA: Certificate by lazy {
        issueDeliveryAuthorization(
            KeyPairSet.PDA_GRANTEE.public,
            KeyPairSet.PRIVATE_ENDPOINT.private,
            CERTIFICATE_END_DATE,
            PRIVATE_ENDPOINT,
            validityStartDate = CERTIFICATE_START_DATE
        )
    }
}
