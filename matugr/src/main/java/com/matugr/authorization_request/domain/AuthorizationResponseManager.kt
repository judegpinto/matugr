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

import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Delegate for authorization response processing. In turn this delegates the response
 * processing to [AuthorizationResultManufacturer]
 *
 * DI Scoped so the same instance stores the pending authorization and process the response
 */
@Singleton
internal class AuthorizationResponseManager @Inject constructor(
    private val authorizationResultManufacturer: AuthorizationResultManufacturer
): AuthRedirectPort, PendingAuthorizationStore {

    private var pendingAuthorization: PendingAuthorization? = null

    override fun handleRedirectUri(uri: URI) {
        pendingAuthorization?.apply {
            if (authorizationContinuation.isCancelled) return
            val authorizationResponse = authorizationResultManufacturer.processAuthorizationUri(uri, state, codeVerifier)
            authorizationContinuation.resume(authorizationResponse, null)
        }
        pendingAuthorization = null
    }

    override fun storePendingAuthorization(pendingAuthorization: PendingAuthorization) {
        this.pendingAuthorization = pendingAuthorization
    }

    override fun cancelPendingAuthorization() {
        pendingAuthorization?.authorizationContinuation?.cancel()
        pendingAuthorization = null
    }

}