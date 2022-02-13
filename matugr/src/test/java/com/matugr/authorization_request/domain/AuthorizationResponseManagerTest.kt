package com.matugr.authorization_request.domain

import com.matugr.authorization_request.external.AuthRequestConfiguration
import com.matugr.authorization_request.external.AuthorizationResult
import com.matugr.common.external.UriCharacter
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CancellableContinuation
import org.junit.Before
import org.junit.Test
import java.net.URI

class AuthorizationResponseManagerTest {

    private val authRequestConfiguration: AuthRequestConfiguration =
        AuthRequestConfiguration(UriCharacter.ParametersIdentifier.Fragment)
    private val continuation: CancellableContinuation<AuthorizationResult> =
        mockk(relaxed = true)
    private val validState: String = "ValidState"
    private val validAuthorizationCode: String = "ValidAuthorizationCode"
    private val validCodeVerifier = "ValidCodeVerifier"
    private val validRedirectUri: URI =
        URI.create("testauth://#state=$validState&code=$validAuthorizationCode")

    private lateinit var authorizationResponseManager: AuthorizationResponseManager

    @Before
    fun setup() {
        authorizationResponseManager = AuthorizationResponseManager(authRequestConfiguration)
    }

    @Test
    fun `when valid authorization parameters exist in uri then success returned`() {
        every { continuation.isCancelled } returns false
        val pendingAuthorization = PendingAuthorization(continuation, validCodeVerifier, validState)

        authorizationResponseManager.storePendingAuthorization(pendingAuthorization)
        authorizationResponseManager.handleRedirectUri(validRedirectUri)

        verify {
            pendingAuthorization.authorizationContinuation.resume(
                AuthorizationResult.Success(validAuthorizationCode, validCodeVerifier),
                null
            )
        }
    }

    @Test
    fun `when invalid state exists in uri then error returned`() {
        every { continuation.isCancelled } returns false
        val invalidState = "invalid_state"
        val invalidStateRedirectUri: URI =
            URI.create("testauth://#state=$invalidState&code=$validAuthorizationCode")
        val pendingAuthorization =
            PendingAuthorization(continuation, validCodeVerifier, validState)

        authorizationResponseManager.storePendingAuthorization(pendingAuthorization)
        authorizationResponseManager.handleRedirectUri(invalidStateRedirectUri)

        verify {
            pendingAuthorization.authorizationContinuation.resume(
                AuthorizationResult.Error.IllegalStateError.StateDoesNotMatch(invalidState, validState),
                null
            )
        }
    }

    @Test
    fun `when no authorization code exists in uri then error returned`() {
        every { continuation.isCancelled } returns false
        val redirectUriWithoutAuthorizationCode: String = "testauth://#no_code_in_uri"
        val pendingAuthorization =
            PendingAuthorization(continuation, validCodeVerifier, validState)

        authorizationResponseManager.storePendingAuthorization(pendingAuthorization)
        authorizationResponseManager.handleRedirectUri(
            URI.create(redirectUriWithoutAuthorizationCode)
        )

        verify {
            pendingAuthorization.authorizationContinuation.resume(
                AuthorizationResult.Error.IllegalStateError.NoCodeInAuthorizationResponse(redirectUriWithoutAuthorizationCode),
                null
            )
        }
    }

    @Test
    fun `when pending authorization is cancelled then internal continuation is cancelled`() {
        every { continuation.isCancelled } returns false
        val pendingAuthorization =
            PendingAuthorization(continuation, validCodeVerifier, validState)

        authorizationResponseManager.storePendingAuthorization(pendingAuthorization)
        authorizationResponseManager.cancelPendingAuthorization()

        verify {
            pendingAuthorization.authorizationContinuation.cancel()
        }
    }
}