package com.matugr.sample.data.ui

/**
 * Data class that translates information from the business layer up to the UI layer.
 * Communicates the result of requests, such as refreshing a token, made to the business layer.
 */
sealed class UiToken {
    data class AuthorizationError(val detail: String): UiToken()
    data class TokenSuccess(val expiry: Long): UiToken()
    data class TokenError(val errorAsString: String): UiToken()
    data class NoRefreshToken(val expiry: Long): UiToken()
    object LocalTokenInvalid: UiToken()
}
