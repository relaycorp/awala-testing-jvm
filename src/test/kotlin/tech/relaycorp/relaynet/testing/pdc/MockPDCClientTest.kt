package tech.relaycorp.relaynet.testing.pdc

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import tech.relaycorp.relaynet.bindings.pdc.ParcelCollection
import tech.relaycorp.relaynet.bindings.pdc.Signer
import tech.relaycorp.relaynet.bindings.pdc.StreamingMode
import tech.relaycorp.relaynet.messages.control.PrivateNodeRegistration
import tech.relaycorp.relaynet.messages.control.PrivateNodeRegistrationRequest
import tech.relaycorp.relaynet.testing.pki.KeyPairSet
import tech.relaycorp.relaynet.testing.pki.PDACertPath

@ExperimentalCoroutinesApi
class MockPDCClientTest {
    @Nested
    inner class Close {
        @Test
        fun `Client should not be reported closed initially`() {
            val client = MockPDCClient()

            assertFalse(client.wasClosed)
        }

        @Test
        fun `Client should be reported closed only when actually closed`() {
            val client = MockPDCClient()

            client.close()

            assertTrue(client.wasClosed)
        }

        @Test
        fun `Client should not be closed if there are calls in the queue`() {
            val client = MockPDCClient(
                PreRegisterNodeCall(
                    Result.success(
                        PrivateNodeRegistrationRequest(KeyPairSet.INTERNET_GW.public, ByteArray(0))
                    )
                )
            )

            val exception = assertThrows<IllegalStateException> { client.close() }

            assertEquals("Client was closed with calls in the queue", exception.message)
            assertTrue(client.wasClosed)
        }
    }

    private val exception = Exception("Something went wrong")

    @Nested
    inner class PreRegisterNode :
        MethodCallTest<PreRegisterNodeArgs, PrivateNodeRegistrationRequest, PreRegisterNodeCall>(
            PreRegisterNodeCall(
                Result.success(
                    PrivateNodeRegistrationRequest(
                        KeyPairSet.INTERNET_GW.public,
                        ByteArray(0)
                    )
                )
            ),
            { client -> client.preRegisterNode(KeyPairSet.INTERNET_GW.public) },
            PreRegisterNodeArgs(KeyPairSet.INTERNET_GW.public),
            PreRegisterNodeCall(Result.failure(exception)),
            RegisterNodeCall(Result.failure(exception))
        )

    val pnra = "the registration authorization".toByteArray()

    @Nested
    inner class RegisterNode :
        MethodCallTest<RegisterNodeArgs, PrivateNodeRegistration, RegisterNodeCall>(
            RegisterNodeCall(
                Result.success(
                    PrivateNodeRegistration(
                        PDACertPath.PRIVATE_GW,
                        PDACertPath.INTERNET_GW,
                        "example.org"
                    )
                )
            ),
            { client -> client.registerNode(pnra) },
            RegisterNodeArgs(pnra),
            RegisterNodeCall(Result.failure(exception)),
            PreRegisterNodeCall(Result.failure(exception))
        )

    val parcelSerialized = "parcel".toByteArray()
    val signer = Signer(PDACertPath.INTERNET_GW, KeyPairSet.INTERNET_GW.private)

    @Nested
    inner class DeliverParcel :
        MethodCallTest<DeliverParcelArgs, Unit, DeliverParcelCall>(
            DeliverParcelCall(),
            { client -> client.deliverParcel(parcelSerialized, signer) },
            DeliverParcelArgs(parcelSerialized, signer),
            DeliverParcelCall(exception),
            PreRegisterNodeCall(Result.failure(exception))
        )

    val collectedParcelsFlow = emptyFlow<ParcelCollection>()

    @Nested
    inner class CollectParcels :
        MethodCallTest<CollectParcelsArgs, Flow<ParcelCollection>, CollectParcelsCall>(
            CollectParcelsCall(Result.success(collectedParcelsFlow)),
            { client -> client.collectParcels(arrayOf(signer), StreamingMode.CloseUponCompletion) },
            CollectParcelsArgs(listOf(signer), StreamingMode.CloseUponCompletion),
            CollectParcelsCall(Result.failure(exception)),
            PreRegisterNodeCall(Result.failure(exception))
        )
}
