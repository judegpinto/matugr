package com.matugr.authorization_request.external

/**
 * Result container for a client authorization request. Error types provide granularity in
 * identifying a more exact error state.
 */
sealed class AuthorizationResult {
    data class Success(val code: String, val codeVerifier: String): AuthorizationResult()
    sealed class Error: AuthorizationResult() {
        data class OAuthError(val authorizationOAuthErrorCode: AuthorizationOAuthErrorCode,
                              val errorDescription: String?,
                              val errorUri: String?): Error()
        sealed class IllegalStateError: Error() {
            data class NoCodeInAuthorizationResponse(val authorizationResponse: String): IllegalStateError()
            data class StateDoesNotMatch(val receivedState: String?, val expectedState: String?): IllegalStateError()
        }
    }
}
