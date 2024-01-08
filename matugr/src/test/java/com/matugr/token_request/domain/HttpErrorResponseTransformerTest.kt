package com.matugr.token_request.domain

import com.matugr.common.oauth.TokenErrorCodeBody
import com.matugr.token_request.external.TokenOAuthErrorCode
import com.matugr.token_request.external.TokenResult
import com.matugr.token_request.networking.OAuthTokenErrorBody
import com.matugr.token_request.networking.TokenNetworkingResponse
import com.squareup.moshi.JsonAdapter
import io.mockk.every
import io.mockk.mockk
import okio.Buffer
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.nio.charset.Charset

class HttpErrorResponseTransformerTest {

    private val tokenErrorResponseJsonAdapter: JsonAdapter<OAuthTokenErrorBody> = mockk()
    private lateinit var httpErrorResponseTransformer: HttpErrorResponseTransformer

    @Before
    fun setUp() {
        httpErrorResponseTransformer = HttpErrorResponseTransformer(
            tokenErrorResponseJsonAdapter = tokenErrorResponseJsonAdapter
        )
    }

    @Test
    fun `Should return oauth error when response contains json`() {
        // given
        val responseCode = 400
        val mockBuffer: Buffer = mockk()
        val peekedBuffer: Buffer = mockk()
        val httpError = TokenNetworkingResponse.Failure.HttpError(
            responseCode = responseCode,
            jsonBody = mockBuffer
        )
        every { mockBuffer.peek() } returns peekedBuffer
        val oAuthTokenErrorBody = OAuthTokenErrorBody(
            TokenErrorCodeBody.InvalidGrant,
            null,
            null
        )
        every { tokenErrorResponseJsonAdapter.fromJson(peekedBuffer) } returns oAuthTokenErrorBody

        // when
        val result = httpErrorResponseTransformer.transformHttpResponseError(httpError)

        // then
        assertEquals(
            TokenResult.Error.OAuthError(
                TokenOAuthErrorCode.InvalidGrant,
                null,
                null
            ),
            result
        )
    }

    @Test
    fun `Should return http error when adapter throws exception`() {
        // given
        val responseCode = 400
        val mockBuffer: Buffer = mockk()
        val peekedBuffer: Buffer = mockk()
        val httpError = TokenNetworkingResponse.Failure.HttpError(
            responseCode = responseCode,
            jsonBody = mockBuffer
        )
        every { mockBuffer.peek() } returns peekedBuffer
        every { tokenErrorResponseJsonAdapter.fromJson(peekedBuffer) } throws Exception()
        val responseBody = ""
        every { mockBuffer.readString(Charset.defaultCharset()) } returns responseBody

        // when
        val result = httpErrorResponseTransformer.transformHttpResponseError(httpError)

        // then
        assertEquals(
            TokenResult.Error.HttpError(
                responseCode,
                responseBody
            ),
            result
        )
    }
}
