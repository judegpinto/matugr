package matugr.authorization_request

import matugr.authorization_request.oauth.CodeChallengeMethod

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
