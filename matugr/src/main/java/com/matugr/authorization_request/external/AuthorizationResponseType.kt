package com.matugr.authorization_request.external

import com.matugr.authorization_request.networking.ResponseType

/**
 * OAuth Response Type
 *
 * See README: Response Type
 */
enum class AuthorizationResponseType {
    Code;

    internal fun toNetworkingResponseType(): ResponseType {
        return when(this) {
            Code -> ResponseType.Code
        }
    }
}
