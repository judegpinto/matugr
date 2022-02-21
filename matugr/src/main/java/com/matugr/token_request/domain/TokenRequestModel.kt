/*
 * Copyright 2022 The Matugr Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.matugr.token_request.domain

import com.matugr.common.tools.DispatcherProvider
import com.matugr.token_request.external.TokenGrantType
import com.matugr.token_request.external.TokenResult
import com.matugr.token_request.external.convertFromTokenOAuthNetworkingError
import com.matugr.token_request.networking.OAuthTokenErrorBody
import com.matugr.token_request.networking.TokenNetworking
import com.matugr.token_request.networking.TokenNetworkingResponse
import com.matugr.token_request.networking.TokenResponseBody
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Core, high-level business logic for token requests.
 */
@Singleton
internal class TokenRequestModel @Inject constructor(
    private val tokenNetworking: TokenNetworking,
    private val tokenResponseBodyJsonAdapter: JsonAdapter<TokenResponseBody>,
    private val tokenTokenErrorResponseJsonAdapter: JsonAdapter<OAuthTokenErrorBody>,
    private val dispatcherProvider: DispatcherProvider
) : TokenRequestPort {

    override suspend fun performTokenRequest(
        tokenUrl: String,
        clientId: String,
        redirectUri: String,
        tokenGrantType: TokenGrantType,
        customParameters: Map<String, String>
    ): TokenResult {
        return withContext(dispatcherProvider.io()) {
            when(tokenGrantType) {
                is TokenGrantType.AuthorizationCode ->
                    executeTokenRequestWithCode(
                        tokenUrl,
                        tokenGrantType.authorizationCode,
                        clientId,
                        tokenGrantType.codeVerifier,
                        redirectUri,
                        customParameters)
                is TokenGrantType.RefreshToken ->
                    executeTokenRequestWithRefreshToken(
                        tokenUrl,
                        tokenGrantType.refreshToken,
                        clientId,
                        customParameters
                    )
            }
        }
    }

    private suspend fun executeTokenRequestWithCode(
        tokenUrl: String,
        authorizationCode: String,
        clientId: String,
        codeVerifier: String,
        redirectUri: String,
        customParameters: Map<String, String>
    ): TokenResult {
        val tokenNetworkingResponse = tokenNetworking.tokenRequestWithCode(
            tokenUrl,
            authorizationCode,
            clientId,
            codeVerifier,
            redirectUri,
            customParameters)
        return when(tokenNetworkingResponse) {
            is TokenNetworkingResponse.Success -> processSuccessfulNetworkResponse(tokenNetworkingResponse)
            is TokenNetworkingResponse.Failure -> processFailedNetworkResponse(tokenNetworkingResponse)
        }
    }

    private suspend fun executeTokenRequestWithRefreshToken(
        tokenUrl: String,
        refreshToken: String,
        clientId: String,
        customParameters: Map<String, String>): TokenResult {
        val tokenNetworkingResponse = tokenNetworking.tokenRequestWithRefreshToken(
            tokenUrl,
            refreshToken,
            clientId,
            customParameters)
        return when(tokenNetworkingResponse) {
            is TokenNetworkingResponse.Success -> processSuccessfulNetworkResponse(tokenNetworkingResponse)
            is TokenNetworkingResponse.Failure -> processFailedNetworkResponse(tokenNetworkingResponse)
        }
    }

    private fun processSuccessfulNetworkResponse(success: TokenNetworkingResponse.Success): TokenResult {
        val tokenResponse = tokenResponseBodyJsonAdapter.fromJson(success.jsonBody)
            ?: return TokenResult.Error.CannotTransformTokenJson(success.jsonBody.readUtf8())
        return TokenResult.Success(
            tokenResponse.accessToken,
            tokenResponse.tokenType,
            tokenResponse.refreshToken,
            tokenResponse.expiresIn,
            tokenResponse.scope
        )
    }

    private fun processFailedNetworkResponse(failure: TokenNetworkingResponse.Failure): TokenResult.Error {
        return when(failure) {
            is TokenNetworkingResponse.Failure.NoResponseBody -> TokenResult.Error.NoResponseBody
            is TokenNetworkingResponse.Failure.HttpError -> {
                val oauthErrorBody = tokenTokenErrorResponseJsonAdapter.fromJson(failure.jsonBody)
                if (oauthErrorBody == null) {
                    TokenResult.Error.HttpError(failure.responseCode, failure.jsonBody.readUtf8())
                } else {
                    val tokenOAuthErrorCode = convertFromTokenOAuthNetworkingError(oauthErrorBody.oAuthErrorCodeBody)
                    TokenResult.Error.OAuthError(
                        tokenOAuthErrorCode,
                        oauthErrorBody.errorDescription,
                        oauthErrorBody.errorUri
                    )
                }
            }
        }
    }

}
