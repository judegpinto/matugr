package com.matugr.token_request.external

/**
 * Result container for a client token request
 */
sealed class TokenResult {
    data class Success(
        val accessToken: String,
        val tokenType: String,
        val refreshToken: String?,
        val expiresIn: Long,
        val scope: String?,
    ): TokenResult()
    sealed class Error: TokenResult() {
        data class OAuthError(
            val oAuthErrorCode: TokenOAuthErrorCode,
            val errorDescription: String?,
            val errorUri: String?): Error()
        data class HttpError(val code: Int, val jsonBody: String): Error()
        data class CannotTransformTokenJson(val jsonBody: String): Error()
        object NoResponseBody: Error()
    }
}