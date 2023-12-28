package tech.relaycorp.relaynet.testing.pki

import tech.relaycorp.relaynet.issueDeliveryAuthorization
import tech.relaycorp.relaynet.issueGatewayCertificate
import tech.relaycorp.relaynet.wrappers.x509.Certificate

/**
 * Certification path from an Internet gateway to a Cargo Delivery Authorization (CDA).
 *
 * See [KeyPairSet] for the respective key pairs used by these certificates.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
public object CDACertPath {
    public val PRIVATE_GW: Certificate by lazy {
        issueGatewayCertificate(
            KeyPairSet.PRIVATE_GW.public,
            KeyPairSet.INTERNET_GW.private,
            CERTIFICATE_END_DATE,
            validityStartDate = CERTIFICATE_START_DATE
        )
    }

    @Suppress("unused")
    public val INTERNET_GW: Certificate by lazy {
        issueDeliveryAuthorization(
            KeyPairSet.INTERNET_GW.public,
            KeyPairSet.PRIVATE_GW.private,
            CERTIFICATE_END_DATE,
            PRIVATE_GW,
            validityStartDate = CERTIFICATE_START_DATE
        )
    }
}
