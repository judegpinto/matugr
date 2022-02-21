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

import java.util.Base64
import com.matugr.authorization_request.external.option.CodeVerifierOption
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Named

const val digestSHA256 = "SHA-256"
const val codeVerifier_length = "64"

/**
 * [MessageDigest] injected and will be null if SHA256 algorithm isn't available.
 * [CodeChallengeMethod.PLAIN] is used in that case.
 *
 * See README: Code Challenge
 */
internal class CodeChallengeGenerator @Inject constructor(
    @Named(digestSHA256) private val messageDigest: MessageDigest?,
    @Named(codeVerifier_length) private val secureRandomGenerator: SecureRandomGenerator,
    private val base64Encoder: Base64.Encoder
) {

    fun codeVerifierAndChallenge(codeVerifierOption: CodeVerifierOption): CodeVerifierContainer {
        return when(codeVerifierOption) {
            is CodeVerifierOption.Generate -> generateCodeVerifierWithChallenge()
            is CodeVerifierOption.GenerateChallenge -> generateCodeVerifierWithChallenge(codeVerifierOption.codeVerifier)
            is CodeVerifierOption.Input -> CodeVerifierContainer(codeVerifierOption.codeVerifier, codeVerifierOption.codeChallenge, codeVerifierOption.codeChallengeMethod)
        }
    }

    private fun generateCodeVerifierWithChallenge(codeVerifier: String = generateCodeVerifier()): CodeVerifierContainer {
        if (messageDigest == null) {
            return CodeVerifierContainer(codeVerifier, codeVerifier, CodeChallengeMethod.PLAIN)
        }
        messageDigest.update(codeVerifier.toByteArray(StandardCharsets.ISO_8859_1))
        return CodeVerifierContainer(codeVerifier, base64Encoder.encodeToString(messageDigest.digest()), CodeChallengeMethod.S256)
    }

    private fun generateCodeVerifier(): String {
        return secureRandomGenerator.generateSecureRandomBase64()
    }
}
