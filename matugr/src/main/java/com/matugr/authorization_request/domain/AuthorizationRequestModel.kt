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

import com.matugr.authorization_request.external.AuthorizationRequest
import com.matugr.authorization_request.external.AuthorizationResult
import com.matugr.authorization_request.networking.AuthUrlBuilder
import com.matugr.authorization_request.networking.AuthorizationNetworkingRequest
import com.matugr.authorization_request.oauth.CodeChallengeGenerator
import com.matugr.authorization_request.oauth.StateGenerator
import com.matugr.common.external.UriCharacter
import com.matugr.common.external.UrlLauncherPort
import com.matugr.common.tools.DispatcherProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Core, high-level business logic for authorization requests.
 */
@Singleton
internal class AuthorizationRequestModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val stateGenerator: StateGenerator,
    private val codeChallengeGenerator: CodeChallengeGenerator,
    private val authUrlBuilder: AuthUrlBuilder,
    private val pendingAuthorizationStore: PendingAuthorizationStore
): AuthorizationRequestPort {

    override suspend fun performAuthorizationRequest(authorizationRequest: AuthorizationRequest,
                                                     urlLauncherPort: UrlLauncherPort
    ): AuthorizationResult {
        val codeVerifier = codeChallengeGenerator.codeVerifierAndChallenge(authorizationRequest.codeVerifierOption)
        val state = stateGenerator.generate(authorizationRequest.stateOption)
        val authRequest = AuthorizationNetworkingRequest(
            clientId = authorizationRequest.clientId,
            responseType = authorizationRequest.responseType.toNetworkingResponseType(),
            codeChallenge = codeVerifier.codeChallenge,
            codeChallengeMethod = codeVerifier.codeChallengeMethod.toString(),
            redirectUri = authorizationRequest.redirectUri,
            scope = authorizationRequest.scope,
            state = state
        )
        val url = authUrlBuilder.generate(
            authorizationRequest.authUrl,
            authRequest,
            UriCharacter.ParametersIdentifier.Query,
            authorizationRequest.customParameters)

        withContext(dispatcherProvider.main()) {
            urlLauncherPort.launchUrl(url)
        }

        return suspendCancellableCoroutine {
            val pendingAuthorization = PendingAuthorization(it, codeVerifier.codeVerifier, state)
            pendingAuthorizationStore.storePendingAuthorization(pendingAuthorization)
        }
    }

    override fun extinguishAuthFlow() {
        pendingAuthorizationStore.cancelPendingAuthorization()
    }
}