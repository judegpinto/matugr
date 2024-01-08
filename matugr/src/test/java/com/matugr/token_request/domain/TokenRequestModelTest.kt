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

import com.matugr.token_request.external.TokenGrantType
import com.matugr.token_request.external.TokenOAuthErrorCode
import com.matugr.token_request.external.TokenResult
import com.matugr.token_request.networking.TokenNetworking
import com.matugr.token_request.networking.TokenNetworkingResponse
import com.matugr.tools.CoroutineTestRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.BufferedSource
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class TokenRequestModelTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val tokenUrl = "https://www.nothing.com/"
    private val clientId = "0000-00000000-0000"
    private val redirectUri = "matugr://"
    private val codeVerifier: String = "ValidCodeVerifier"
    private val customParameters: Map<String, String> = emptyMap()

    private val tokenNetworking: TokenNetworking = mockk()
    private val tokenSuccessResponseTransformer: TokenSuccessResponseTransformer = mockk()
    private val httpErrorResponseTransformer: HttpErrorResponseTransformer = mockk()

    private lateinit var tokenRequestModel: TokenRequestModel

    @Before
    fun setup() {
        tokenRequestModel = TokenRequestModel(
            tokenNetworking = tokenNetworking,
            tokenSuccessResponseTransformer = tokenSuccessResponseTransformer,
            tokenErrorResponseTransformer = httpErrorResponseTransformer,
            dispatcherProvider = coroutinesTestRule.testDispatcherProvider
        )
    }

    @Test
    fun `when grant type is code then token request made with code parameters`()
    = coroutinesTestRule.testDispatcher.runBlockingTest {
        // given
        val authorizationCode = "AuthorizationCode"
        val tokenSuccessResponse: TokenNetworkingResponse.Success = mockk()
        val tokenResult: TokenResult.Success = mockk()
        coEvery {
            tokenNetworking.tokenRequestWithCode(
                tokenUrl, authorizationCode, clientId, codeVerifier, redirectUri, customParameters)
        } returns tokenSuccessResponse
        coEvery {
            tokenSuccessResponseTransformer.transformSuccessResponse(tokenSuccessResponse)
        } returns tokenResult

        // when
        tokenRequestModel.performTokenRequest(
            tokenUrl,
            clientId,
            redirectUri,
            TokenGrantType.AuthorizationCode(authorizationCode, codeVerifier)
        )

        coVerify {
            tokenNetworking.tokenRequestWithCode(
                tokenUrl, authorizationCode, clientId, codeVerifier, redirectUri, emptyMap()
            )
        }
    }

    @Test
    fun `when grant type is refresh token then token request made with refresh token parameters`()
            = coroutinesTestRule.testDispatcher.runBlockingTest {
        val refreshToken = "RefreshToken"
        val tokenSuccessResponse: TokenNetworkingResponse.Success = mockk()
        val tokenResult: TokenResult.Success = mockk()
        coEvery {
            tokenNetworking.tokenRequestWithRefreshToken(
                tokenUrl, refreshToken, clientId, customParameters)
        } returns tokenSuccessResponse
        coEvery {
            tokenSuccessResponseTransformer.transformSuccessResponse(tokenSuccessResponse)
        } returns tokenResult

        tokenRequestModel.performTokenRequest(
            tokenUrl,
            clientId,
            redirectUri,
            TokenGrantType.RefreshToken(refreshToken)
        )

        coVerify {
            tokenNetworking.tokenRequestWithRefreshToken(
                tokenUrl, refreshToken, clientId, emptyMap()
            )
        }
    }

    @Test
    fun `should include custom parameters in network request when provided with refresh token`()
            = coroutinesTestRule.testDispatcher.runBlockingTest {
        // given
        val refreshToken = "RefreshToken"
        val customParameters = mapOf("custom_parameter" to "custom value")
        val tokenSuccessResponse: TokenNetworkingResponse.Success = mockk()
        val tokenResult: TokenResult.Success = mockk()
        coEvery {
            tokenNetworking.tokenRequestWithRefreshToken(
                tokenUrl, refreshToken, clientId, customParameters)
        } returns tokenSuccessResponse
        coEvery {
            tokenSuccessResponseTransformer.transformSuccessResponse(tokenSuccessResponse)
        } returns tokenResult

        // when
        tokenRequestModel.performTokenRequest(
            tokenUrl,
            clientId,
            redirectUri,
            TokenGrantType.RefreshToken(refreshToken),
            customParameters
        )

        // then
        coVerify {
            tokenNetworking.tokenRequestWithRefreshToken(
                tokenUrl, refreshToken, clientId, customParameters
            )
        }
    }

    @Test
    fun `should include custom parameters in network request when provided with authorization code`()
            = coroutinesTestRule.testDispatcher.runBlockingTest {
        // given
        val authorizationCode = "AuthorizationCode"
        val customParameters = mapOf("custom_parameter" to "custom value")
        val tokenSuccessResponse: TokenNetworkingResponse.Success = mockk()
        val tokenResult: TokenResult.Success = mockk()
        coEvery {
            tokenNetworking.tokenRequestWithCode(
                tokenUrl, authorizationCode, clientId, codeVerifier, redirectUri, customParameters)
        } returns tokenSuccessResponse
        coEvery {
            tokenSuccessResponseTransformer.transformSuccessResponse(tokenSuccessResponse)
        } returns tokenResult

        // when
        tokenRequestModel.performTokenRequest(
            tokenUrl,
            clientId,
            redirectUri,
            TokenGrantType.AuthorizationCode(authorizationCode, codeVerifier),
            customParameters
        )

        // then
        coVerify {
            tokenNetworking.tokenRequestWithCode(
                tokenUrl, authorizationCode, clientId, codeVerifier, redirectUri, customParameters
            )
        }
    }

    @Test
    fun `Should return no response body error when no body in network response`()
        = coroutinesTestRule.testDispatcher.runBlockingTest {
        // given
        val authorizationCode = "AuthorizationCode"
        val customParameters = mapOf("custom_parameter" to "custom value")
        val tokenNetworkingResponse = TokenNetworkingResponse.Failure.NoResponseBody
        val tokenResult = TokenResult.Error.NoResponseBody
        coEvery {
            tokenNetworking.tokenRequestWithCode(
                tokenUrl, authorizationCode, clientId, codeVerifier, redirectUri, customParameters)
        } returns tokenNetworkingResponse

        // when
        val result = tokenRequestModel.performTokenRequest(
            tokenUrl,
            clientId,
            redirectUri,
            TokenGrantType.AuthorizationCode(authorizationCode, codeVerifier),
            customParameters
        )

        // then
        assertEquals(tokenResult, result)
    }

    @Test
    fun `Should return transformed error when http error received in network response`()
        = coroutinesTestRule.testDispatcher.runBlockingTest {
        // given
        val refreshToken = "RefreshToken"
        val mockResponseBody: BufferedSource = mockk()
        val tokenErrorResponse = TokenNetworkingResponse.Failure.HttpError(
            responseCode = 500,
            jsonBody = mockResponseBody
        )
        val tokenResult = TokenResult.Error.OAuthError(TokenOAuthErrorCode.InvalidGrant, null, null)
        coEvery {
            tokenNetworking.tokenRequestWithRefreshToken(
                tokenUrl, refreshToken, clientId, customParameters)
        } returns tokenErrorResponse
        coEvery {
            httpErrorResponseTransformer.transformHttpResponseError(tokenErrorResponse)
        } returns tokenResult

        // when
        val result = tokenRequestModel.performTokenRequest(
            tokenUrl,
            clientId,
            redirectUri,
            TokenGrantType.RefreshToken(refreshToken),
            customParameters
        )

        // then
        assertEquals(tokenResult, result)
    }
}
