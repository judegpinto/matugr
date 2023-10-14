package com.matugr.token_request.domain

import com.matugr.token_request.external.TokenResult
import com.matugr.token_request.external.convertFromTokenOAuthNetworkingError
import com.matugr.token_request.networking.OAuthTokenErrorBody
import com.matugr.token_request.networking.TokenNetworkingResponse
import com.squareup.moshi.JsonAdapter
import java.lang.NullPointerException
import java.nio.charset.Charset
import javax.inject.Inject

/**
 * Parser for http networking responses to check if they are OAuth Errors or another http error
 */
internal class HttpErrorResponseTransformer @Inject constructor(
    private val tokenErrorResponseJsonAdapter: JsonAdapter<OAuthTokenErrorBody>
) {
    fun transformHttpResponseError(
        failure: TokenNetworkingResponse.Failure.HttpError
    ): TokenResult.Error {
        return kotlin.runCatching {
            tokenErrorResponseJsonAdapter.fromJson(failure.jsonBody.peek())
                ?: throw NullPointerException()
        }.fold(onSuccess = { oauthErrorBody ->
            oAuthError(oauthErrorBody)
        }, onFailure = {
            httpError(failure.responseCode, failure.jsonBody.readString(Charset.defaultCharset()))
        })
    }

    private fun oAuthError(oAuthTokenErrorBody: OAuthTokenErrorBody): TokenResult.Error.OAuthError {
        val tokenOAuthErrorCode = convertFromTokenOAuthNetworkingError(
            oAuthTokenErrorBody.oAuthErrorCodeBody
        )
        return TokenResult.Error.OAuthError(
            tokenOAuthErrorCode,
            oAuthTokenErrorBody.errorDescription,
            oAuthTokenErrorBody.errorUri
        )
    }

    private fun httpError(responseCode: Int, responseBody: String) =
        TokenResult.Error.HttpError(responseCode, responseBody)
}
