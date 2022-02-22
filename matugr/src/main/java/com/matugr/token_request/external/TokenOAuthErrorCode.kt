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

import com.matugr.common.oauth.TokenErrorCodeBody

/**
 * External result error codes specific to OAuth Token Request
 *
 * See README: Token Error
 */
enum class TokenOAuthErrorCode {
    InvalidRequest,
    InvalidClient,
    InvalidGrant,
    UnAuthorizedClient,
    UnSupportedGrantType,
    InvalidScope;
}

fun convertFromTokenOAuthNetworkingError(
    tokenErrorCodeBody: TokenErrorCodeBody
): TokenOAuthErrorCode {
    return when(tokenErrorCodeBody) {
        TokenErrorCodeBody.InvalidRequest -> TokenOAuthErrorCode.InvalidRequest
        TokenErrorCodeBody.InvalidClient -> TokenOAuthErrorCode.InvalidClient
        TokenErrorCodeBody.InvalidGrant -> TokenOAuthErrorCode.InvalidGrant
        TokenErrorCodeBody.UnauthorizedClient -> TokenOAuthErrorCode.UnAuthorizedClient
        TokenErrorCodeBody.UnsupportedGrantType -> TokenOAuthErrorCode.UnSupportedGrantType
        TokenErrorCodeBody.InvalidScope -> TokenOAuthErrorCode.InvalidScope
    }
}