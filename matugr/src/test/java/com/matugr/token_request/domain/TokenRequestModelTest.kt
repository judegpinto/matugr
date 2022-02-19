package com.matugr.token_request.domain

import com.matugr.token_request.external.TokenGrantType
import com.matugr.token_request.networking.OAuthTokenErrorBody
import com.matugr.token_request.networking.TokenNetworking
import com.matugr.token_request.networking.TokenNetworkingResponse
import com.matugr.token_request.networking.TokenResponseBody
import com.matugr.tools.CoroutineTestRule
import com.squareup.moshi.JsonAdapter
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.BufferedSource
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

    private val accessToken = "access_token"
    private val tokenType = "token_type"
    private val refreshToken = "refresh_token"
    private val expiresIn = 0L
    private val scope = "scope"

    private val tokenNetworking: TokenNetworking = mockk()
    private val tokenResponseBodyJsonAdapter: JsonAdapter<TokenResponseBody> = mockk()
    private val oAuthTokenErrorBodyJsonAdapter: JsonAdapter<OAuthTokenErrorBody>  = mockk()

    private lateinit var tokenRequestModel: TokenRequestModel

    @Before
    fun setup() {
        tokenRequestModel = TokenRequestModel(
            tokenNetworking,
            tokenResponseBodyJsonAdapter,
            oAuthTokenErrorBodyJsonAdapter,
            coroutinesTestRule.testDispatcherProvider)
    }

    @Test
    fun `when grant type is code then token request made with code parameters`()
    = coroutinesTestRule.testDispatcher.runBlockingTest {
        // given
        val authorizationCode = "AuthorizationCode"
        val jsonBody: BufferedSource = mockk()
        coEvery {
            tokenNetworking.tokenRequestWithCode(
                tokenUrl, authorizationCode, clientId, codeVerifier, redirectUri, customParameters)
        } returns TokenNetworkingResponse.Success(jsonBody)
        every { tokenResponseBodyJsonAdapter.fromJson(jsonBody) } returns TokenResponseBody(accessToken, tokenType, refreshToken, expiresIn, scope)

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
        val jsonBody: BufferedSource = mockk()
        coEvery {
            tokenNetworking.tokenRequestWithRefreshToken(
                tokenUrl, refreshToken, clientId, customParameters)
        } returns TokenNetworkingResponse.Success(jsonBody)
        every { tokenResponseBodyJsonAdapter.fromJson(jsonBody) } returns TokenResponseBody(accessToken, tokenType, refreshToken, expiresIn, scope)

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
        val refreshToken = "RefreshToken"
        val jsonBody: BufferedSource = mockk()
        coEvery {
            tokenNetworking.tokenRequestWithRefreshToken(
                tokenUrl, refreshToken, clientId, customParameters)
        } returns TokenNetworkingResponse.Success(jsonBody)
        every { tokenResponseBodyJsonAdapter.fromJson(jsonBody) } returns TokenResponseBody(accessToken, tokenType, refreshToken, expiresIn, scope)
        tokenRequestModel.performTokenRequest(
            tokenUrl,
            clientId,
            redirectUri,
            TokenGrantType.RefreshToken(refreshToken),
            customParameters
        )

        coVerify {
            tokenNetworking.tokenRequestWithRefreshToken(
                tokenUrl, refreshToken, clientId, customParameters
            )
        }
    }

    @Test
    fun `should include custom parameters in network request when provided with authorization code`()
            = coroutinesTestRule.testDispatcher.runBlockingTest {
        val authorizationCode = "AuthorizationCode"
        val jsonBody: BufferedSource = mockk()
        coEvery {
            tokenNetworking.tokenRequestWithCode(
                tokenUrl, authorizationCode, clientId, codeVerifier, redirectUri, customParameters)
        } returns TokenNetworkingResponse.Success(jsonBody)
        every { tokenResponseBodyJsonAdapter.fromJson(jsonBody) } returns TokenResponseBody(accessToken, tokenType, refreshToken, expiresIn, scope)
        tokenRequestModel.performTokenRequest(
            tokenUrl,
            clientId,
            redirectUri,
            TokenGrantType.AuthorizationCode(authorizationCode, codeVerifier),
            customParameters
        )

        coVerify {
            tokenNetworking.tokenRequestWithCode(
                tokenUrl, authorizationCode, clientId, codeVerifier, redirectUri, customParameters
            )
        }
    }
}