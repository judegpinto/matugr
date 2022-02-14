package com.matugr.authorization_request.external

import com.matugr.common.oauth.AuthorizationErrorCodeBody

/**
 * Result container for a client authorization request
 */
sealed class AuthorizationResult {
    data class Success(val code: String, val codeVerifier: String): AuthorizationResult()
    sealed class Error: AuthorizationResult() {
        data class OAuthError(val oAuthErrorCodeBody: AuthorizationErrorCodeBody,
                              val errorDescription: String?,
                              val errorUri: String?): Error()
        data class HttpError(val code: Int, val jsonBody: String): Error()
        sealed class IllegalStateError: Error() {
            data class NoCodeInAuthorizationResponse(val authorizationResponse: String): IllegalStateError()
            data class StateDoesNotMatch(val receivedState: String?, val expectedState: String?): IllegalStateError()
        }
    }
}
