package com.matugr.common.oauth

import com.squareup.moshi.Json

/**
 * OAuth errors defined by OAuth Standard.
 *
 * See README: Authorization Request, Token Request.
 */
const val INVALID_REQUEST_CODE = "invalid_request"
const val UNAUTHORIZED_CLIENT_CODE = "unauthorized_client"
const val ACCESS_DENIED_CODE = "access_denied"
const val UNSUPPORTED_RESPONSE_TYPE_CODE = "unsupported_response_type"
const val INVALID_SCOPE_CODE = "invalid_scope"
const val SERVER_ERROR_CODE = "server_error"
const val TEMPORARILY_UNAVAILABLE_CODE = "temporarily_unavailable"
const val INVALID_CLIENT_CODE = "invalid_client"
const val INVALID_GRANT_CODE = "invalid_grant"
const val UNSUPPORTED_GRANT_TYPE_CODE = "unsupported_grant_type"
enum class AuthorizationErrorCodeBody {
    @Json(name = INVALID_REQUEST_CODE) InvalidRequest,
    @Json(name = UNAUTHORIZED_CLIENT_CODE) UnauthorizedClient,
    @Json(name = ACCESS_DENIED_CODE) AccessDenied,
    @Json(name = UNSUPPORTED_RESPONSE_TYPE_CODE) UnsupportedResponseType,
    @Json(name = INVALID_SCOPE_CODE) InvalidScope,
    @Json(name = SERVER_ERROR_CODE) ServerError,
    @Json(name = TEMPORARILY_UNAVAILABLE_CODE) TemporarilyUnavailable,
}

enum class TokenErrorCodeBody {
    @Json(name = INVALID_REQUEST_CODE) InvalidRequest,
    @Json(name = INVALID_CLIENT_CODE) InvalidClient,
    @Json(name = INVALID_GRANT_CODE) InvalidGrant,
    @Json(name = UNAUTHORIZED_CLIENT_CODE) UnauthorizedClient,
    @Json(name = UNSUPPORTED_GRANT_TYPE_CODE) UnsupportedGrantType,
    @Json(name = INVALID_SCOPE_CODE) InvalidScope,
}
