package tech.relaycorp.relaynet.testing.pdc

import tech.relaycorp.relaynet.bindings.pdc.Signer
import tech.relaycorp.relaynet.bindings.pdc.StreamingMode
import java.security.PublicKey

public data class PreRegisterNodeArgs(internal val nodePublicKey: PublicKey)

public data class RegisterNodeArgs(internal val pnrrSerialized: ByteArray) {
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

public data class DeliverParcelArgs(
    internal val parcelSerialized: ByteArray,
    internal val deliverySigner: Signer
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

public data class CollectParcelsArgs(
    internal val nonceSigners: List<Signer>,
    internal val streamingMode: StreamingMode?
)
