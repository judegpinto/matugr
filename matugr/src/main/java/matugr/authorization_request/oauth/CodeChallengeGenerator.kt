package matugr.authorization_request.oauth

import java.util.Base64
import matugr.authorization_request.domain.CodeVerifierOption
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
