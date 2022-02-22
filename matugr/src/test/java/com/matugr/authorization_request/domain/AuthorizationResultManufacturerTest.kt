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
import com.matugr.authorization_request.external.AuthorizationOAuthErrorCode
import com.matugr.authorization_request.external.AuthorizationResult
import com.matugr.common.external.UriCharacter
import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.URI

class AuthorizationResultManufacturerTest {

    private val authRequestConfiguration = AuthorizationRequestConfiguration(UriCharacter.ParametersIdentifier.Query)
    private val authorizationResultManufacturer: AuthorizationResultManufacturer =
        AuthorizationResultManufacturerImpl(authRequestConfiguration)

    // reusable test parameters
    private val validState: String = "ValidState"
    private val validAuthorizationCode: String = "ValidAuthorizationCode"
    private val validCodeVerifier = "ValidCodeVerifier"
    private val validRedirectUri: URI =
        URI.create("testauth://?state=$validState&code=$validAuthorizationCode")

    @Test
    fun `when uri contains oauth error then error is OAuth`() {
        // given
        val error = "invalid_scope"
        val errorDescription = "custom+scopes+are+not+supported"
        val errorUri = "null"
        val authorizationResponseUri =
            URI.create("matugr://?error=$error&error_description=$errorDescription&error_uri=$errorUri")

        // when
        val authorizationResult = authorizationResultManufacturer.processAuthorizationUri(
            authorizationResponseUri, validState, validCodeVerifier)

        // then
        val expected = AuthorizationResult.Error.OAuthError(
            AuthorizationOAuthErrorCode.InvalidScope, errorDescription, errorUri)
        assertEquals(expected, authorizationResult)
    }

    @Test
    fun `when valid authorization parameters exist in uri then success returned`() {
        // given

        // when
        val result = authorizationResultManufacturer.processAuthorizationUri(
            validRedirectUri, validState, validCodeVerifier)

        assertEquals(
            AuthorizationResult.Success(validAuthorizationCode, validCodeVerifier),
            result
        )
    }

    @Test
    fun `when invalid state exists in uri then error returned`() {
        // given
        val invalidState = "invalid_state"
        val invalidStateRedirectUri: URI =
            URI.create("testauth://?state=$invalidState&code=$validAuthorizationCode")

        // when
        val result = authorizationResultManufacturer.processAuthorizationUri(
            invalidStateRedirectUri, validState, validCodeVerifier)

        // then
        assertEquals(
            AuthorizationResult.Error.IllegalStateError.StateDoesNotMatch(invalidState, validState),
            result
        )
    }

    @Test
    fun `when no authorization code exists in uri then error returned`() {
        // given
        val redirectUriWithoutAuthorizationCode = URI.create("testauth://?no_code_in_uri")

        // when
        val result = authorizationResultManufacturer.processAuthorizationUri(
            redirectUriWithoutAuthorizationCode, validState, validCodeVerifier)

        // then
        assertEquals(
            AuthorizationResult.Error.IllegalStateError.NoCodeInAuthorizationResponse(redirectUriWithoutAuthorizationCode.toString()),
            result
        )
    }
}