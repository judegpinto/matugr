package com.matugr.token_request.domain

import com.matugr.token_request.external.TokenResult
import com.matugr.token_request.networking.TokenNetworkingResponse
import com.matugr.token_request.networking.TokenResponseBody
import com.squareup.moshi.JsonAdapter
import javax.inject.Inject

internal class TokenSuccessResponseTransformer @Inject constructor(
    private val tokenResponseBodyJsonAdapter: JsonAdapter<TokenResponseBody>
) {
    /**
     * OAuth error bodies are not necessarily enormous, so the json body will be handled as
     * a [String]
     */
    fun transformSuccessResponse(success: TokenNetworkingResponse.Success): TokenResult {
        val jsonAsString = success.jsonBody.readUtf8()
        val tokenResponse = tokenResponseBodyJsonAdapter.fromJson(jsonAsString)
            ?: return TokenResult.Error.CannotTransformTokenJson(jsonAsString)
        return TokenResult.Success(
            tokenResponse.accessToken,
            tokenResponse.tokenType,
            tokenResponse.refreshToken,
            tokenResponse.expiresIn,
            tokenResponse.scope
        )
    }
}