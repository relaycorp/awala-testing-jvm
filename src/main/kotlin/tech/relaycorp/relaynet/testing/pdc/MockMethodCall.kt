package tech.relaycorp.relaynet.testing.pdc

import kotlinx.coroutines.flow.Flow
import tech.relaycorp.relaynet.bindings.pdc.ParcelCollection
import tech.relaycorp.relaynet.messages.control.PrivateNodeRegistration
import tech.relaycorp.relaynet.messages.control.PrivateNodeRegistrationRequest

public abstract class MockMethodCall<CallArguments, CallResult> internal constructor(
    private val result: Result<CallResult>,
) {
    /**
     * Report whether the method call was actually called.
     */
    public var wasCalled: Boolean = false
        private set

    /**
     * The arguments with which the call was made.
     */
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

/**
 * Mock call to pre-register a private node.
 *
 * @param result The registration request to return to mimic a success scenario, or the exception
 *     to throw in a failure scenario.
 */
public class PreRegisterNodeCall(
    result: Result<PrivateNodeRegistrationRequest>,
) : MockMethodCall<PreRegisterNodeArgs, PrivateNodeRegistrationRequest>(result)

/**
 * Mock call to register a private node.
 *
 * @param result The private node registration to return to mimic a success scenario, or the
 *     exception to throw in a failure scenario.
 */
public class RegisterNodeCall(
    result: Result<PrivateNodeRegistration>,
) : MockMethodCall<RegisterNodeArgs, PrivateNodeRegistration>(result)

/**
 * Mock call to deliver a parcel.
 *
 * @param throwable The exception to throw if mimicking a failure scenario.
 */
public class DeliverParcelCall(
    throwable: Throwable? = null,
) : MockMethodCall<DeliverParcelArgs, Unit>(
        if (throwable == null) {
            Result.success(Unit)
        } else {
            Result.failure(throwable)
        },
    )

/**
 * Mock call to collect parcels.
 *
 * @param result The flow of parcel collections to return to mimic a success scenario, or the
 *     exception to throw in a failure scenario.
 */
public class CollectParcelsCall(
    result: Result<Flow<ParcelCollection>>,
) : MockMethodCall<CollectParcelsArgs, Flow<ParcelCollection>>(result)
