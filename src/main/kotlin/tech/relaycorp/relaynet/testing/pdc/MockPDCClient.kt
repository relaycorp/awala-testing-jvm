package tech.relaycorp.relaynet.testing.pdc

import java.security.PublicKey
import java.util.LinkedList
import kotlinx.coroutines.flow.Flow
import tech.relaycorp.relaynet.bindings.pdc.PDCClient
import tech.relaycorp.relaynet.bindings.pdc.ParcelCollection
import tech.relaycorp.relaynet.bindings.pdc.Signer
import tech.relaycorp.relaynet.bindings.pdc.StreamingMode
import tech.relaycorp.relaynet.messages.control.PrivateNodeRegistration
import tech.relaycorp.relaynet.messages.control.PrivateNodeRegistrationRequest

/**
 * PDC client implementation whose methods are mocked.
 *
 * @param calls The sequence of expected mock method calls to be called before the client is closed
 */
public class MockPDCClient public constructor(
    vararg calls: MockMethodCall<*, *>,
) : PDCClient {
    private val callQueue: LinkedList<MockMethodCall<*, *>> = LinkedList()

    /**
     * Reports whether the client was closed.
     */
    public var wasClosed: Boolean = false
        private set

    init {
        calls.forEach { callQueue.add(it) }
    }

    override fun close() {
        wasClosed = true

        check(callQueue.isEmpty()) { "Client was closed with calls in the queue" }
    }

    override suspend fun preRegisterNode(nodePublicKey: PublicKey): PrivateNodeRegistrationRequest {
        val call = getNextCall<PreRegisterNodeCall>()
        val args = PreRegisterNodeArgs(nodePublicKey)
        return call.call(args)
    }

    override suspend fun registerNode(pnrrSerialized: ByteArray): PrivateNodeRegistration {
        val call = getNextCall<RegisterNodeCall>()
        val args = RegisterNodeArgs(pnrrSerialized)
        return call.call(args)
    }

    override suspend fun deliverParcel(
        parcelSerialized: ByteArray,
        deliverySigner: Signer,
    ) {
        val call = getNextCall<DeliverParcelCall>()
        val args = DeliverParcelArgs(parcelSerialized, deliverySigner)
        return call.call(args)
    }

    override suspend fun collectParcels(
        nonceSigners: Array<Signer>,
        streamingMode: StreamingMode,
    ): Flow<ParcelCollection> {
        val call = getNextCall<CollectParcelsCall>()
        val args = CollectParcelsArgs(nonceSigners.asList(), streamingMode)
        return call.call(args)
    }

    private inline fun <reified Call : MockMethodCall<*, *>> getNextCall(): Call {
        val call = callQueue.poll()
        check(call != null) { "There are no calls left in the queue" }
        check(call is Call) {
            val expectedClassName = Call::class.simpleName
            val actualClassName = call::class.simpleName
            "Expected next call to be $expectedClassName (got $actualClassName)"
        }
        return call
    }
}
