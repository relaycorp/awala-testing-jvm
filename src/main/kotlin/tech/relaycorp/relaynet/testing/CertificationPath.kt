package tech.relaycorp.relaynet.testing

import tech.relaycorp.relaynet.issueEndpointCertificate
import tech.relaycorp.relaynet.issueGatewayCertificate
import tech.relaycorp.relaynet.issueParcelDeliveryAuthorization
import tech.relaycorp.relaynet.wrappers.x509.Certificate
import java.time.ZonedDateTime

/**
 * Collection of Relaynet PKI certificates for nodes in a hypothetical certification path.
 *
 * See [KeyPairSet] for the respective key pairs used by these certificates.
 */
@Suppress("MemberVisibilityCanBePrivate")
public object CertificationPath {
    private val startDate = ZonedDateTime.now().minusMinutes(1)
    private val endDate = ZonedDateTime.now().plusHours(1)

    public val PUBLIC_GW: Certificate by lazy {
        issueGatewayCertificate(
            KeyPairSet.PUBLIC_GW.public,
            KeyPairSet.PUBLIC_GW.private,
            endDate,
            validityStartDate = startDate
        )
    }

    public val PRIVATE_GW: Certificate by lazy {
        issueGatewayCertificate(
            KeyPairSet.PRIVATE_GW.public,
            KeyPairSet.PUBLIC_GW.private,
            endDate,
            PUBLIC_GW,
            validityStartDate = startDate
        )
    }

    public val PRIVATE_ENDPOINT: Certificate by lazy {
        issueEndpointCertificate(
            KeyPairSet.PRIVATE_ENDPOINT.public,
            KeyPairSet.PRIVATE_GW.private,
            endDate,
            PRIVATE_GW,
            validityStartDate = startDate
        )
    }

    public val PDA: Certificate by lazy {
        issueParcelDeliveryAuthorization(
            KeyPairSet.PDA_GRANTEE.public,
            KeyPairSet.PRIVATE_ENDPOINT.private,
            endDate,
            PRIVATE_ENDPOINT,
            validityStartDate = startDate
        )
    }
}
