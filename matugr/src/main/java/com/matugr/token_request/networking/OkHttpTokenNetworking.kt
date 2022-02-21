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
