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
