package com.matugr.sample.data.ui

import com.matugr.token_request.external.TokenOAuthErrorCode

/**
 * Data class that translates information from the business layer up to the UI layer.
 * Communicates the result of requests, such as refreshing a token, made to the business layer.
 */
sealed class UiToken {
    data class AuthorizationError(val detail: String): UiToken()
    data class TokenSuccess(val expiry: Long): UiToken()
    sealed class TokenError: UiToken() {
        data class OAuthError(val oAuthErrorCode: TokenOAuthErrorCode,
                              val errorDescription: String?,
                              val errorUri: String?): TokenError()

        data class HttpError(val code: Int, val jsonBody: String): TokenError()

        data class IllegalState(val errorAsString: String): TokenError()
    }
    data class NoRefreshToken(val expiry: Long): UiToken()
    object NoLocalToken: UiToken()
}
