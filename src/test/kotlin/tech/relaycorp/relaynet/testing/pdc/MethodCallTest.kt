package tech.relaycorp.relaynet.testing.pdc

import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@ExperimentalCoroutinesApi
abstract class MethodCallTest<CArgs, CResult, Call : MockMethodCall<CArgs, CResult>>(
    private val successfulCall: Call,
    private val methodCaller: suspend (client: MockPDCClient) -> CResult,
    private val expectedArguments: CArgs,
    private val invalidCall: Call,
    private val differentCall: MockMethodCall<*, *>,
) {
    @Test
    fun `Call should be refused if next call is for different method`() {
        val client = MockPDCClient(differentCall)

        val exception =
            assertThrows<IllegalStateException> {
                runTest {
                    methodCaller(client)
                }
            }

        val expectedCallClassName = successfulCall::class.simpleName
        val actualCallClassName = differentCall::class.simpleName
        assertEquals(
            "Expected next call to be $expectedCallClassName (got $actualCallClassName)",
            exception.message,
        )
    }

    @Test
    fun `Call should be refused if no further calls were expected`() {
        val client = MockPDCClient()

        val exception =
            assertThrows<IllegalStateException> {
                runTest {
                    methodCaller(client)
                }
            }

        assertEquals(
            "There are no calls left in the queue",
            exception.message,
        )
    }

    @Test
    fun `Call should be recorded`() =
        runTest {
            val client = MockPDCClient(successfulCall)

            methodCaller(client)

            assertTrue(successfulCall.wasCalled)
        }

    @Test
    fun `Arguments should be recorded`() =
        runTest {
            val client = MockPDCClient(successfulCall)

            methodCaller(client)

            assertEquals(expectedArguments, successfulCall.arguments)
        }

    @Test
    fun `Specified result should be returned`() =
        runTest {
            val client = MockPDCClient(successfulCall)

            val result = methodCaller(client)

            assertSame(successfulCall.successfulResult, result)
        }

    @Test
    fun `Specified exception should be thrown`() {
        val client = MockPDCClient(invalidCall)

        val actualException =
            assertThrows<Exception> {
                runTest { methodCaller(client) }
            }

        assertSame(invalidCall.exception, actualException)
    }
}
