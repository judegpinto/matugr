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

package com.matugr.authorization_request.oauth

import java.util.regex.Pattern

/**
 * Wraps code verifier along with code challenge information
 *
 * See README: Code Verifier
 */
internal data class CodeVerifierContainer(val codeVerifier: String,
                                 val codeChallenge: String,
                                 val codeChallengeMethod: CodeChallengeMethod
) {
    companion object {
        const val MIN_LENGTH = 43
        const val MAX_LENGTH = 128
    }
    private val regexLegalCodeVerifier = Pattern.compile("^[0-9a-zA-Z\\-._~]{43,128}$")

    init {
        if (codeVerifier.length < MIN_LENGTH) {
            throw IllegalArgumentException("Code Verifier Too Short. See Spec rfc7636 section-4.1")
        }
        if (codeVerifier.length > MAX_LENGTH) {
            throw IllegalArgumentException("Code Verifier Too Long. See Spec rfc7636 section-4.1")
        }
        if (!regexLegalCodeVerifier.matcher(codeVerifier).matches()) {
            throw IllegalArgumentException("Illegal Code Verifier. See Spec rfc7636 section-4.1")
        }
    }
}
