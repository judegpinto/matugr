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

package com.matugr.token_request.external

/**
 * Result container for a client token request
 */
sealed class TokenResult {
    data class Success(
        val accessToken: String,
        val tokenType: String,
        val refreshToken: String?,
        val expiresIn: Long,
        val scope: String?,
    ): TokenResult()
    sealed class Error: TokenResult() {
        data class OAuthError(
            val oAuthErrorCode: TokenOAuthErrorCode,
            val errorDescription: String?,
            val errorUri: String?): Error()
        data class HttpError(val code: Int, val jsonBody: String): Error()
        data class CannotTransformTokenJson(val jsonBody: String): Error()
        object NoResponseBody: Error()
    }
}