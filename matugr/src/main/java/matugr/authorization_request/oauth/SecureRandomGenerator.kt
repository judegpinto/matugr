package matugr.authorization_request.oauth

import java.util.Base64
import java.security.SecureRandom

internal class SecureRandomGenerator(private val secureRandom: SecureRandom,
                            private val length: Int,
                            private val base64Encoder: Base64.Encoder) {
    fun generateSecureRandomBase64(): String {
        val random = ByteArray(length)
        secureRandom.nextBytes(random)
        return base64Encoder.encodeToString(random)
    }
}
