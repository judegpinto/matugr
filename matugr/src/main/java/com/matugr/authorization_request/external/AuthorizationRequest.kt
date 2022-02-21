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

import com.matugr.authorization_request.external.option.CodeVerifierOption
import com.matugr.authorization_request.external.option.StateOption

/**
 * API for client to specify authorization request parameters, including OAuth specific parameters,
 * as well as dictate options for OAuth configuration, i.e. state and code verifier.
 */
data class AuthorizationRequest(
    val authUrl: String,
    val clientId: String,
    val responseType: AuthorizationResponseType,
    val redirectUri: String? = null,
    val scope: String? = null,
    val stateOption: StateOption = StateOption.Generate,
    val codeVerifierOption: CodeVerifierOption = CodeVerifierOption.Generate,
    val customParameters: Map<String, String> = emptyMap()
)
