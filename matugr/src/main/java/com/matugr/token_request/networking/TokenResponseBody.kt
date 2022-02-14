package com.matugr.token_request.networking

import com.matugr.common.oauth.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Networking class represents the expected OAuth token response
 *
 * Leveraged by moshi for networking
 *
 * See README: Token Response
 */
@JsonClass(generateAdapter = true)
internal data class TokenResponseBody(
    @Json(name = ACCESS_TOKEN) val accessToken: String,
    @Json(name = TOKEN_TYPE) val tokenType: String,
    @Json(name = REFRESH_TOKEN) val refreshToken: String?,
    @Json(name = EXPIRES_IN) val expiresIn: Long,
    @Json(name = SCOPE) val scope: String?,
)
