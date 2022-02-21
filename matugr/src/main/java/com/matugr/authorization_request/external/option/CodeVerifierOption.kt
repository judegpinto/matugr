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

package com.matugr.authorization_request.external.option

import com.matugr.authorization_request.oauth.CodeChallengeMethod

/**
 * Client has the following options to dictate the code verifier
 * and corresponding challenge sent to the server
 * PKCE flow is required for authorization code flow in OAuth 2.1
 *
 * See README: Code Verifier
 */
sealed class CodeVerifierOption {
    data class Input(val codeVerifier: String,
                     val codeChallenge: String,
                     val codeChallengeMethod: CodeChallengeMethod
    ): CodeVerifierOption()
    data class GenerateChallenge(val codeVerifier: String): CodeVerifierOption()
    object Generate: CodeVerifierOption()
}
