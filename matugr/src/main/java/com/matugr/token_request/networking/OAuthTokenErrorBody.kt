package com.matugr.token_request.networking

import com.matugr.common.oauth.TokenErrorCodeBody
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Networking class to propagate OAuth-specific networking errors
 *
 * Leveraged by moshi for networking
 *
 * See README: Token Error
 */
@JsonClass(generateAdapter = true)
internal data class OAuthTokenErrorBody(
    @Json(name = "error") val oAuthErrorCodeBody: TokenErrorCodeBody,
    @Json(name = "error_description") val errorDescription: String?,
    @Json(name = "error_uri") val errorUri: String?
)
