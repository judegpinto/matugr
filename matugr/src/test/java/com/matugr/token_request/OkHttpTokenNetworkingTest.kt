package com.matugr.token_request

import com.matugr.token_request.networking.OkHttpTokenNetworking
import com.matugr.token_request.networking.TokenNetworkingResponse
import com.matugr.tools.CoroutineTestRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.*
import okio.BufferedSource
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class OkHttpTokenNetworkingTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val url = "https://matugr"
    private val code = "authorizationCode"
    private val refreshToken = "refreshToken"
    private val clientId = "clientId"
    private val codeVerifier = "codeVerifier"
    private val redirectUri = "redirectUri"
    private val customParameters = emptyMap<String, String>()

    private val okHttpClient: OkHttpClient = mockk()
    private val call: Call = mockk()
    private val successResponse: Response = mockk()
    private val successResponseBufferedSource: BufferedSource = mockk()
    private val successResponseBody: ResponseBody = mockk()
    private lateinit var okHttpTokenNetworking: OkHttpTokenNetworking

    @Before
    fun setup() {
        okHttpTokenNetworking = OkHttpTokenNetworking(okHttpClient, coroutinesTestRule.testDispatcherProvider)
        every { okHttpClient.newCall(any()) } returns call
    }

    @Test
    fun `successful token request with code returns success response`()
    = coroutinesTestRule.testDispatcher.runBlockingTest {
        // given
        setupSuccessResponse()

        // when
        val tokenNetworkingResponse = okHttpTokenNetworking.tokenRequestWithCode(
            url, code, clientId, codeVerifier, redirectUri, customParameters
        )

        // then
        assert(tokenNetworkingResponse is TokenNetworkingResponse.Success)
        assertEquals(successResponseBufferedSource, (tokenNetworkingResponse as TokenNetworkingResponse.Success).jsonBody)
    }

    @Test
    fun `successful token request with refresh token returns success response`()
            = coroutinesTestRule.testDispatcher.runBlockingTest {
        // given
        setupSuccessResponse()

        // when
        val tokenNetworkingResponse = okHttpTokenNetworking.tokenRequestWithRefreshToken(
            url, refreshToken, clientId, customParameters
        )

        // then
        assert(tokenNetworkingResponse is TokenNetworkingResponse.Success)
        assertEquals(successResponseBufferedSource, (tokenNetworkingResponse as TokenNetworkingResponse.Success).jsonBody)
    }

    @Test
    fun `successful token request with no response body returns appropriate failure`()
            = coroutinesTestRule.testDispatcher.runBlockingTest {
        // given
        val responseWithNoBody: Response = mockk()
        every { call.execute() } returns responseWithNoBody
        every { responseWithNoBody.body } returns null

        // when
        val tokenNetworkingResponse = okHttpTokenNetworking.tokenRequestWithRefreshToken(
            url, refreshToken, clientId, customParameters
        )

        // then
        assert(tokenNetworkingResponse is TokenNetworkingResponse.Failure.NoResponseBody)
    }

    @Test
    fun `failed token request returns appropriate failure`()
            = coroutinesTestRule.testDispatcher.runBlockingTest {
        // given
        val failedResponse: Response = mockk()
        every { call.execute() } returns failedResponse
        val failedResponseBody: ResponseBody = mockk()
        every { failedResponse.body } returns failedResponseBody
        val failedResponseBodySource: BufferedSource = mockk()
        every { failedResponseBody.source() } returns failedResponseBodySource
        every { failedResponse.isSuccessful } returns false
        val failureCode = 500
        every { failedResponse.code } returns failureCode

        // when
        val tokenNetworkingResponse = okHttpTokenNetworking.tokenRequestWithRefreshToken(
            url, refreshToken, clientId, customParameters
        )

        // then
        assert(tokenNetworkingResponse is TokenNetworkingResponse.Failure.HttpError)
        val failedNetworkingResponse = tokenNetworkingResponse as TokenNetworkingResponse.Failure.HttpError
        assertEquals(failureCode, failedNetworkingResponse.responseCode)
        assertEquals(failedResponseBodySource, failedNetworkingResponse.jsonBody)
    }

    private fun setupSuccessResponse() {
        every { call.execute() } returns successResponse
        every { successResponse.body } returns successResponseBody
        every { successResponseBody.source() } returns successResponseBufferedSource
        every { successResponse.isSuccessful } returns true
    }
}