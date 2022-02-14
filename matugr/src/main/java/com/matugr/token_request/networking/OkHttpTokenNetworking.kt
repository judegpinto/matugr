package com.matugr.token_request.networking

import com.matugr.common.oauth.*
import com.matugr.common.tools.DispatcherProvider
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

internal class OkHttpTokenNetworking(
    private val okHttpClient: OkHttpClient,
    private val dispatcherProvider: DispatcherProvider

): TokenNetworking {
    override suspend fun tokenRequestWithCode(
        tokenUrl: String,
        authorizationCode: String,
        clientId: String,
        codeVerifier: String,
        redirectUri: String,
        customParameters: Map<String, String>
    ): TokenNetworkingResponse {
        return withContext(dispatcherProvider.io()) {
            val formBody = FormBody.Builder().apply {
                add(CODE, authorizationCode)
                add(CLIENT_ID, clientId)
                add(CODE_VERIFIER, codeVerifier)
                add(REDIRECT_URI, redirectUri)
                add(GRANT_TYPE, AUTHORIZATION_CODE)
                customParameters.forEach {
                    add(it.key, it.value)
                }
            }.build()
            val request = Request.Builder()
                .url(tokenUrl)
                .post(formBody)
                .build()

            return@withContext executeRequest(request)
        }
    }

    override suspend fun tokenRequestWithRefreshToken(
        tokenUrl: String,
        refreshToken: String,
        clientId: String,
        customParameters: Map<String, String>
    ): TokenNetworkingResponse {
        return withContext(dispatcherProvider.io()) {
            val formBody = FormBody.Builder().apply {
                add(REFRESH_TOKEN, refreshToken)
                add(CLIENT_ID, clientId)
                add(GRANT_TYPE, REFRESH_TOKEN)
                customParameters.forEach {
                    add(it.key, it.value)
                }
            }
                .build()
            val request = Request.Builder()
                .url(tokenUrl)
                .post(formBody)
                .build()
            return@withContext executeRequest(request)
        }
    }

    private fun executeRequest(request: Request): TokenNetworkingResponse {
        val response = okHttpClient.newCall(request).execute()
        val responseAsBufferedSource = response.body?.source()
            ?: return TokenNetworkingResponse.Failure.NoResponseBody
        if (!response.isSuccessful) {
            return TokenNetworkingResponse.Failure.HttpError(
                response.code,
                responseAsBufferedSource
            )
        }
        return TokenNetworkingResponse.Success(responseAsBufferedSource)
    }
}
