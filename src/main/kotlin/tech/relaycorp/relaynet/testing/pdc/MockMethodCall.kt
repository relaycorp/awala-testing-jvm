package tech.relaycorp.relaynet.testing.pdc

import kotlinx.coroutines.flow.Flow
import tech.relaycorp.relaynet.bindings.pdc.ParcelCollection
import tech.relaycorp.relaynet.messages.control.PrivateNodeRegistration
import tech.relaycorp.relaynet.messages.control.PrivateNodeRegistrationRequest

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

    internal fun call(args: CallArguments): CallResult {
        wasCalled = true
        arguments = args
        return result.getOrThrow()
    }
}

public class PreRegisterNodeCall(result: Result<PrivateNodeRegistrationRequest>) :
    MockMethodCall<PreRegisterNodeArgs, PrivateNodeRegistrationRequest>(result)

public class RegisterNodeCall(result: Result<PrivateNodeRegistration>) :
    MockMethodCall<RegisterNodeArgs, PrivateNodeRegistration>(result)

public class DeliverParcelCall(throwable: Throwable? = null) :
    MockMethodCall<DeliverParcelArgs, Unit>(
        if (throwable == null)
            Result.success(Unit)
        else
            Result.failure(throwable)
    )

public class CollectParcelsCall(result: Result<Flow<ParcelCollection>>) :
    MockMethodCall<CollectParcelsArgs, Flow<ParcelCollection>>(result)
