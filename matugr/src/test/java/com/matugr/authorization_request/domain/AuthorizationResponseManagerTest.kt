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

import com.matugr.authorization_request.external.AuthorizationResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CancellableContinuation
import org.junit.Before
import org.junit.Test
import java.net.URI

class AuthorizationResponseManagerTest {

    private val authorizationResultManufacturer: AuthorizationResultManufacturer = mockk()
    private val continuation: CancellableContinuation<AuthorizationResult> =
        mockk(relaxed = true)
    private val validState: String = "ValidState"
    private val validAuthorizationCode: String = "ValidAuthorizationCode"
    private val validCodeVerifier = "ValidCodeVerifier"
    private val validRedirectUri: URI =
        URI.create("testauth://#state=$validState&code=$validAuthorizationCode")

    private lateinit var authorizationResponseManager: AuthorizationResponseManager

    @Before
    fun setup() {
        authorizationResponseManager = AuthorizationResponseManager(authorizationResultManufacturer)
    }

    @Test
    fun `given stored authorization result when valid redirect uri is handled then success returned`() {
        // given
        every { continuation.isCancelled } returns false
        val pendingAuthorization = PendingAuthorization(continuation, validCodeVerifier, validState)
        every {
            authorizationResultManufacturer.processAuthorizationUri(
                validRedirectUri, validState, validCodeVerifier)
        } returns AuthorizationResult.Success(validAuthorizationCode, validCodeVerifier)

        // when
        authorizationResponseManager.storePendingAuthorization(pendingAuthorization)
        authorizationResponseManager.handleRedirectUri(validRedirectUri)

        // then
        verify {
            pendingAuthorization.authorizationContinuation.resume(
                AuthorizationResult.Success(validAuthorizationCode, validCodeVerifier),
                null
            )
        }
    }

    @Test
    fun `when error state is returned then error propagated`() {
        // given
        every { continuation.isCancelled } returns false
        val invalidState = "invalid_state"
        val invalidStateRedirectUri: URI =
            URI.create("testauth://#state=$invalidState&code=$validAuthorizationCode")
        val pendingAuthorization =
            PendingAuthorization(continuation, validCodeVerifier, validState)
        every {
            authorizationResultManufacturer.processAuthorizationUri(
                invalidStateRedirectUri, validState, validCodeVerifier)
        } returns AuthorizationResult.Error.IllegalStateError.StateDoesNotMatch(invalidState, validState)

        authorizationResponseManager.storePendingAuthorization(pendingAuthorization)
        authorizationResponseManager.handleRedirectUri(invalidStateRedirectUri)

        verify {
            pendingAuthorization.authorizationContinuation.resume(
                AuthorizationResult.Error.IllegalStateError.StateDoesNotMatch(invalidState, validState),
                null
            )
        }
    }

    @Test
    fun `when pending authorization is cancelled then internal continuation is cancelled`() {
        every { continuation.isCancelled } returns false
        val pendingAuthorization =
            PendingAuthorization(continuation, validCodeVerifier, validState)

        authorizationResponseManager.storePendingAuthorization(pendingAuthorization)
        authorizationResponseManager.cancelPendingAuthorization()

        verify {
            pendingAuthorization.authorizationContinuation.cancel()
        }
    }
}