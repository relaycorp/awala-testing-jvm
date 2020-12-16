package tech.relaycorp.relaynet.testing.pki

import java.time.ZonedDateTime

internal val CERTIFICATE_START_DATE = ZonedDateTime.now().minusMinutes(1)
internal val CERTIFICATE_END_DATE = ZonedDateTime.now().plusHours(1)
