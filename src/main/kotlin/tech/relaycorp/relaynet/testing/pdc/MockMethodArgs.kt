package tech.relaycorp.relaynet.testing.pdc

import java.security.PublicKey
import tech.relaycorp.relaynet.bindings.pdc.Signer
import tech.relaycorp.relaynet.bindings.pdc.StreamingMode

/**
 * Arguments passed to the `preRegisterNode()` method of the PDC client.
 */
public data class PreRegisterNodeArgs(public val nodePublicKey: PublicKey)

/**
 * Arguments passed to the `registerNode()` method of the PDC client.
 */
public data class RegisterNodeArgs(public val pnrrSerialized: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (javaClass != other?.javaClass) {
            return false
        }

        other as RegisterNodeArgs

        if (!pnrrSerialized.contentEquals(other.pnrrSerialized)) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        return pnrrSerialized.contentHashCode()
    }
}

/**
 * Arguments passed to the `deliverParcel()` method of the PDC client.
 */
public data class DeliverParcelArgs(
    public val parcelSerialized: ByteArray,
    public val deliverySigner: Signer
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (javaClass != other?.javaClass) {
            return false
        }

        other as DeliverParcelArgs

        if (!parcelSerialized.contentEquals(other.parcelSerialized)) {
            return false
        }
        if (deliverySigner != other.deliverySigner) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = parcelSerialized.contentHashCode()
        result = 31 * result + deliverySigner.hashCode()
        return result
    }
}

/**
 * Arguments passed to the `collectParcels()` method of the PDC client.
 */
public data class CollectParcelsArgs(
    public val nonceSigners: List<Signer>,
    public val streamingMode: StreamingMode?
)
