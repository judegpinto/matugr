package com.matugr.token_request.external

/**
 * Domain class wraps different token requests clients are allowed to make
 */
sealed class TokenGrantType {
    data class AuthorizationCode(val authorizationCode: String, val codeVerifier: String): TokenGrantType()
    data class RefreshToken(val refreshToken: String): TokenGrantType()
}