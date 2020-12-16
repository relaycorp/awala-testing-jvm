package tech.relaycorp.relaynet.testing.pki

import tech.relaycorp.relaynet.issueDeliveryAuthorization
import tech.relaycorp.relaynet.issueGatewayCertificate
import tech.relaycorp.relaynet.wrappers.x509.Certificate

/**
 * Certification path from a private gateway to a Cargo Delivery Authorization (CDA).
 *
 * See [KeyPairSet] for the respective key pairs used by these certificates.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
public object CDACertPath {
    public val PRIVATE_GW: Certificate by lazy {
        issueGatewayCertificate(
            KeyPairSet.PRIVATE_GW.public,
            KeyPairSet.PUBLIC_GW.private,
            CERTIFICATE_END_DATE,
            validityStartDate = CERTIFICATE_START_DATE
        )
    }

    @Suppress("unused")
    public val PUBLIC_GW: Certificate by lazy {
        issueDeliveryAuthorization(
            KeyPairSet.PUBLIC_GW.public,
            KeyPairSet.PUBLIC_GW.private,
            CERTIFICATE_END_DATE,
            PRIVATE_GW,
            validityStartDate = CERTIFICATE_START_DATE
        )
    }
}
