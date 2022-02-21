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

package com.matugr.authorization_request.domain

import com.matugr.authorization_request.external.AuthorizationRequestConfiguration
import com.matugr.authorization_request.external.AuthorizationResult
import com.matugr.authorization_request.external.convertFromAuthorizationOAuthNetworkingError
import com.matugr.authorization_request.networking.AuthURI
import com.matugr.common.oauth.*
import timber.log.Timber
import java.net.URI
import javax.inject.Inject

class AuthorizationResultManufacturerImpl @Inject constructor(
    private val authorizationRequestConfiguration: AuthorizationRequestConfiguration
): AuthorizationResultManufacturer {
    /**
     * Built like an "unhappy path". Check one by one any known error states that may exist along
     * the way, and at the end, no errors must exist so return a success response.
     */
    override fun processAuthorizationUri(uri: URI, state: String?, codeVerifier: String): AuthorizationResult {
        val params = AuthURI(uri).getParams(authorizationRequestConfiguration.parametersIdentifier)
        Timber.d(javaClass.simpleName, "redirect uri parameters: $params")

        // check for oauth error
        val oAuthError = params[ERROR]
        if (oAuthError != null) {
            val oauthNetworkingErrorCode = AuthorizationErrorCodeBody.authorizationErrorCodeFromOAuthValue(oAuthError)
            val oAuthErrorCode = convertFromAuthorizationOAuthNetworkingError(oauthNetworkingErrorCode)
            return AuthorizationResult.Error.OAuthError(
                oAuthErrorCode,
                params[ERROR_DESCRIPTION],
                params[ERROR_URI]
            )
        }

        // At this point, a success response is expected. keyword: "expected".
        val code = params[CODE]
        val receivedState = params[STATE]

        // Check for illegal state
        if(code == null) return AuthorizationResult.Error.IllegalStateError.NoCodeInAuthorizationResponse(uri.toString())
        if(state != receivedState) return AuthorizationResult.Error.IllegalStateError.StateDoesNotMatch(receivedState, state)

        // Success!
        return AuthorizationResult.Success(code, codeVerifier)
    }
}