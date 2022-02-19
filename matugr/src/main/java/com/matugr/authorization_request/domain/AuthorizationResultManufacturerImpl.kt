package com.matugr.authorization_request.domain

import com.matugr.authorization_request.external.AuthRequestConfiguration
import com.matugr.authorization_request.external.AuthorizationOAuthErrorCode
import com.matugr.authorization_request.external.AuthorizationResult
import com.matugr.authorization_request.external.convertFromAuthorizationOAuthNetworkingError
import com.matugr.authorization_request.networking.AuthURI
import com.matugr.common.oauth.*
import timber.log.Timber
import java.net.URI
import javax.inject.Inject

class AuthorizationResultManufacturerImpl @Inject constructor(
    private val authRequestConfiguration: AuthRequestConfiguration
): AuthorizationResultManufacturer {
    /**
     * Built like an "unhappy path". Check one by one any known error states that may exist along
     * the way, and at the end, no errors must exist so return a success response.
     */
    override fun processAuthorizationUri(uri: URI, state: String?, codeVerifier: String): AuthorizationResult {
        val params = AuthURI(uri).getParams(authRequestConfiguration.parametersIdentifier)
        Timber.d(javaClass.simpleName, "redirect uri parameters: $params")

        // check for oauth error
        val oAuthError = params[ERROR]
        if (oAuthError != null) {
            val oauthNetworkingErrorCode = AuthorizationErrorCodeBody.authorizationErrorCodeFromOAuthValue(oAuthError)
            val oAuthErrorCode = convertFromAuthorizationOAuthNetworkingError(oauthNetworkingErrorCode)
            return AuthorizationResult.Error.OAuthError(
                oAuthErrorCode,
                params[ERROR_DESCRIPTION],
                params[ERROR_URI]
            )
        }

        // At this point, a success response is expected. keyword: "expected".
        val code = params[CODE]
        val receivedState = params[STATE]

        // Check for illegal state
        if(code == null) return AuthorizationResult.Error.IllegalStateError.NoCodeInAuthorizationResponse(uri.toASCIIString())
        if(state != receivedState) return AuthorizationResult.Error.IllegalStateError.StateDoesNotMatch(receivedState, state)

        // Success!
        return AuthorizationResult.Success(code, codeVerifier)
    }
}