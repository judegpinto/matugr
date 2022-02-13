package matugr.authorization_request

import matugr.common.oauth.AuthorizationErrorCodeBody

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
