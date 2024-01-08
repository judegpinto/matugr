package com.matugr.token_request.domain

import com.matugr.common.oauth.TokenErrorCodeBody
import com.matugr.token_request.external.TokenResult
import com.matugr.token_request.networking.OAuthTokenErrorBody
import com.matugr.token_request.networking.TokenNetworkingResponse
import com.matugr.token_request.networking.TokenResponseBody
import com.squareup.moshi.JsonAdapter
import io.mockk.every
import io.mockk.mockk
import okio.Buffer
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class TokenSuccessResponseTransformerTest {

    private val tokenResponseBodyJsonAdapter: JsonAdapter<TokenResponseBody> = mockk()
    private lateinit var tokenSuccessResponseTransformer: TokenSuccessResponseTransformer

    @Before
    fun setUp() {
        tokenSuccessResponseTransformer = TokenSuccessResponseTransformer(
            tokenResponseBodyJsonAdapter
        )
    }

    @Test
    fun `Should return success if json indicates success`() {
        // given
        val json = "{}"
        val mockBuffer: Buffer = mockk()
        every { mockBuffer.readUtf8() } returns json
        val tokenNetworkingResponse = TokenNetworkingResponse.Success(
            mockBuffer
        )
        val accessToken = "kjkhbncmskldiofu"
        val tokenType = "Bearer"
        val refreshToken = "hjknsdckcvkdr"
        val expiresIn = 3600L
        val scope = null
        val tokenResponse = TokenResponseBody(
            accessToken = accessToken,
            tokenType = tokenType,
            refreshToken = refreshToken,
            expiresIn = expiresIn,
            scope = null
        )
        every { tokenResponseBodyJsonAdapter.fromJson(json) } returns tokenResponse

        // when
        val result = tokenSuccessResponseTransformer.transformSuccessResponse(
            tokenNetworkingResponse
        )

        // then
        assertEquals(
            TokenResult.Success(
                accessToken = accessToken,
                tokenType = tokenType,
                refreshToken = refreshToken,
                expiresIn = expiresIn,
                scope = scope
            ),
            result
        )
    }

    @Test
    fun `Should return error if json cannot be transformed`() {
        // given
        val json = "{}"
        val mockBuffer: Buffer = mockk()
        every { mockBuffer.readUtf8() } returns json
        val tokenNetworkingResponse = TokenNetworkingResponse.Success(
            mockBuffer
        )
        every { tokenResponseBodyJsonAdapter.fromJson(json) } returns null

        // when
        val result = tokenSuccessResponseTransformer.transformSuccessResponse(
            tokenNetworkingResponse
        )

        // then
        assertEquals(
            TokenResult.Error.CannotTransformTokenJson(json),
            result
        )
    }
}
