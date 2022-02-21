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

package com.matugr.authorization_request.external

/**
 * Result container for a client authorization request. Error types provide granularity in
 * identifying a more exact error state.
 */
sealed class AuthorizationResult {
    data class Success(val code: String, val codeVerifier: String): AuthorizationResult()
    sealed class Error: AuthorizationResult() {
        data class OAuthError(val authorizationOAuthErrorCode: AuthorizationOAuthErrorCode,
                              val errorDescription: String?,
                              val errorUri: String?): Error()
        sealed class IllegalStateError: Error() {
            data class NoCodeInAuthorizationResponse(val authorizationResponse: String): IllegalStateError()
            data class StateDoesNotMatch(val receivedState: String?, val expectedState: String?): IllegalStateError()
        }
    }
}
