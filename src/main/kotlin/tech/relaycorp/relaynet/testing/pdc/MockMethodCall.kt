package tech.relaycorp.relaynet.testing.pdc

import tech.relaycorp.relaynet.messages.control.PrivateNodeRegistrationRequest
import java.security.PublicKey

public sealed class MockMethodCall<CallArguments, CallResult> constructor(
    private val result: Result<CallResult>
) {
    public var wasCalled: Boolean = false
        private set

    public var arguments: CallArguments? = null
        private set

    internal val successfulResult
        get() = result.getOrNull()

    internal val exception
        get() = result.exceptionOrNull()

    public fun call(args: CallArguments): CallResult {
        wasCalled = true
        arguments = args
        return result.getOrThrow()
    }
}

public data class PreRegisterNodeArgs(internal val nodePublicKey: PublicKey)

public class PreRegisterNodeCall(result: Result<PrivateNodeRegistrationRequest>) :
    MockMethodCall<PreRegisterNodeArgs, PrivateNodeRegistrationRequest>(result)

public data class RegisterNodeArgs(internal val pnrrSerialized: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RegisterNodeArgs

        if (!pnrrSerialized.contentEquals(other.pnrrSerialized)) return false

        return true
    }

    override fun hashCode(): Int {
        return pnrrSerialized.contentHashCode()
    }
}

public class RegisterNodeCall(result: Result<RegisterNodeArgs>) :
    MockMethodCall<PreRegisterNodeArgs, RegisterNodeArgs>(result)
