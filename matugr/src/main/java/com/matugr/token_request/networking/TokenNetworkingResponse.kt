package com.matugr.token_request.networking

import okio.BufferedSource

/**
 * Distinguishes between 1. network response failed and 2. network response succeeded but the token
 * was not obtained.
 */
sealed class TokenNetworkingResponse() {
    data class Success(val jsonBody: BufferedSource): TokenNetworkingResponse()
    sealed class Failure: TokenNetworkingResponse() {
        object NoResponseBody: Failure()
        data class HttpError(val responseCode: Int, val jsonBody: BufferedSource): Failure()
    }
}
