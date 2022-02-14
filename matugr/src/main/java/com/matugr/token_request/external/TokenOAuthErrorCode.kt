package com.matugr.token_request.external

import com.matugr.common.oauth.TokenErrorCodeBody

/**
 * External result error codes specific to OAuth Token Request
 *
 * See README: Token Error
 */
enum class TokenOAuthErrorCode {
    InvalidRequest,
    InvalidClient,
    InvalidGrant,
    UnAuthorizedClient,
    UnSupportedGrantType,
    InvalidScope;
}

fun convertFromTokenOAuthNetworkingError(
    tokenErrorCodeBody: TokenErrorCodeBody
): TokenOAuthErrorCode {
    return when(tokenErrorCodeBody) {
        TokenErrorCodeBody.InvalidRequest -> TokenOAuthErrorCode.InvalidRequest
        TokenErrorCodeBody.InvalidClient -> TokenOAuthErrorCode.InvalidClient
        TokenErrorCodeBody.InvalidGrant -> TokenOAuthErrorCode.InvalidGrant
        TokenErrorCodeBody.UnauthorizedClient -> TokenOAuthErrorCode.UnAuthorizedClient
        TokenErrorCodeBody.UnsupportedGrantType -> TokenOAuthErrorCode.UnSupportedGrantType
        TokenErrorCodeBody.InvalidScope -> TokenOAuthErrorCode.InvalidScope
    }
}