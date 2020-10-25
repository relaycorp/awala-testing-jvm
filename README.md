# relaynet-jvm-testing

Testing utilities for Relaynet implementations on the JVM.

## Install

This module is available on JCenter as `tech.relaycorp:relaynet-testing`. [Find the latest version on Bintray](https://bintray.com/relaycorp/maven/tech.relaycorp.relaynet.testing).

## Usage

### Mock certification path

This module exposes a series of valid certificates that represent a mock certification path from a public gateway to a Parcel Delivery Authorization (PDA). They can be found in the object `CertificationPath`, and their corresponding key pairs are in `KeyPairSet`. Both are generated at runtime and can be used as follows:

```kotlin
val parcel = Parcel("https://endpoint.com", CertificationPath.PDA, "payload".toByteArray())
val parcelSerialized = parcel.serialize(KeyPairSet.PDA_GRANTEE.private)
```

Refer to the API documentation to find all the entities in the path.

### Mock PDC Client

You can use the `MockPDCClient` provided by this module to replace an actual PDC Client (e.g., [PoWeb's](https://github.com/relaycorp/relaynet-poweb-jvm)) in a unit test suite. This way, you'll avoid making real calls to an external system, and you'll be able to inspect how the client was used.

The first step is to make the unit under test (UUT) use the mock client instead of the real one, which could be done with dependency injection or by mocking a method that would return an instance of the client. Either way, make sure the UUT uses the interface `PDCClient` from the core Relaynet library instead of a concrete implementation.

The mock client is initialised with the exact sequence of method calls you'd expect your UUT to make. For example, if you're testing the node registration flow, you'd want to check that the pre-registration and registration methods are called in that order, so you'd initialise the mock client as follows:

```kotlin
val registrationAuthorization = "the authorization".toByteArray()
val preRegistrationCall = PreRegisterNodeCall(
    Result.success(
        PrivateNodeRegistrationRequest(KeyPairSet.PRIVATE_GW.public, registrationAuthorization)
    )
)
val registrationCall = RegisterNodeCall(
    Result.success(
        PrivateNodeRegistration(
            CertificationPath.PRIVATE_GW,
            CertificationPath.PUBLIC_GW
        )
    )
)

val client = MockPDCClient(preRegistrationCall, registrationCall)
unitUnderTest.setClient(client)
unitUnderTest.call()

assertTrue(client.wasClosed)

assertEquals(KeyPairSet.PRIVATE_GW.public, preRegistrationCall.arguments.nodePublicKey)

val registrationRequestSerialized = registrationCall.arguments!!.pnrrSerialized
val registrationRequest = PrivateNodeRegistrationRequest.deserialize(registrationRequestSerialized)
assertEqual(KeyPairSet.PRIVATE_GW.public, registrationRequest.privateNodePublicKey)
assertEqual(registrationAuthorization.asList(), registrationRequest.pnraSerialized.asList())
```

If you want to reproduce a scenario where a call fails, simply initialise the respective call with the exception. For example:

```kotlin
val registrationCall = RegisterNodeCall(
    Result.failure(Exception("Something went wrong"))
)
```

Refer to the API documentation to learn how to mock other methods.

## API Documentation

The API documentation is available online on [docs.relaycorp.tech](https://docs.relaycorp.tech/relaynet-jvm-testing/).
